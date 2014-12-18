package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.fragments.MovieListAdapter;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.EventListener;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.model.MovieComparator;
import semanticweb.hws14.movapp.model.MovieDet;
import semanticweb.hws14.movapp.request.HttpRequestQueueSingleton;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;

public class MovieList extends Activity {

    private MovieListAdapter mlAdapter;
    private Activity that = this;
    private HashMap<String, Object> criteria;
    public static  MenuItem imdbButton;
    private queryForMovies q;
    public static boolean staticRatingLoaded;
    private ListView listView;
    public static boolean staticRequestCanceled;

    public static ArrayList<Movie> staticMovieList;
    private static HashMap<String, Object> staticCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_movie_list);

        Intent intent = getIntent();
        final ArrayList<Movie> movieList = new ArrayList<Movie>();

        criteria = (HashMap<String, Object>)intent.getSerializableExtra("criteria");
        listView = (ListView) findViewById(R.id.movieList);
        staticRequestCanceled = false;
        checkCriteria();
        //If staticCriteria equals criteria, the criteria did not change to the last time, so we dont need to query again.
        if(criteria.equals(staticCriteria)) {
           this.mlAdapter = new MovieListAdapter(this,R.layout.listview_item_movie, movieList);
            listView.setAdapter(mlAdapter);
            mlAdapter.addAll(staticMovieList);

        } else {
            this.mlAdapter = new MovieListAdapter(this,R.layout.listview_item_movie, movieList);

            listView.setAdapter(mlAdapter);
            //Executes SPARQL Queries, Private class queryForMovies is called.
            staticCriteria = criteria;
            staticRatingLoaded = false;
            q = new queryForMovies();
            q.execute(criteria);
        }

        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(that, MovieDetail.class);
                Movie movie = movieList.get(position);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        };

        listView.setOnItemClickListener(clickListen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list, menu);
        //Only show the imdbButton if needed
        imdbButton =  menu.findItem(R.id.imdb_rating_button).setVisible(false);
        if(null != staticMovieList && staticMovieList.size() >= 200 && !staticRatingLoaded) {
            imdbButton.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.imdb_rating_button) {
            queryForImdbRating();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Stops async Task and async HttpRequester
    //Wipe all working tasks if the activity is stopped, otherwise app is threatened to crash
    @Override
    protected void onStop () {
        super.onStop();
        if(null != q) {
            q.cancel(true);
        }
        if(staticRequestCanceled) {
            HttpRequestQueueSingleton.getInstance(this.getApplicationContext()).cancelPendingRequests("movieList");
            that.setProgressBarIndeterminateVisibility(false);
        }
    }
        //query manually for imdb rating
    public void queryForImdbRating () {
        that.setProgressBarIndeterminateVisibility(true);
        staticRequestCanceled = true;
        HttpRequester.addOmdbData(that, MovieList.staticMovieList, mlAdapter, (Boolean) criteria.get("isTime"), (Boolean) criteria.get("isGenre"), (Boolean) criteria.get("isActor"), (Boolean) criteria.get("isDirector"), (Boolean) criteria.get("isCity"), (Boolean) criteria.get("isState"), (Boolean) criteria.get("isPartName"));
    }

    //Clean all the criteria, so if a criteria is not available it will be set to default false. --> No need to always set them, to call this activity
    private void checkCriteria() {
        if(!criteria.containsKey("isPartName")) {
            criteria.put("isPartName", false);
        }
        if(!criteria.containsKey("isActor")) {
            criteria.put("isActor", false);
        }
        if(!criteria.containsKey("isTime")) {
            criteria.put("isTime", false);
        }
        if(!criteria.containsKey("isGenre")) {
            criteria.put("isGenre", false);
        }
        if(!criteria.containsKey("isDirector")) {
            criteria.put("isDirector", false);
        }
        if(!criteria.containsKey("isCity")) {
            criteria.put("isCity", false);
        }
        if(!criteria.containsKey("isState")) {
            criteria.put("isState", false);
        }
        if(!criteria.containsKey("isRandomRelated")) {
            criteria.put("isRandomRelated", false);
        }
        if(!criteria.containsKey("isRelated")) {
            criteria.put("isRelated", false);
        }
    }

    //Inner class for the async task
    private class queryForMovies extends AsyncTask<HashMap<String, Object>, String, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(HashMap<String, Object>... criterias) {

           HashMap<String, Object> criteria = criterias[0];
           final SparqlQueries sparqler = new SparqlQueries(criteria);

           final ArrayList<Movie> movieList = new ArrayList<Movie>();

            /* LMDB */
            //Get the lmdb movies
            Thread tLMDB = new Thread(new Runnable()
            {
                public void run()
                {
                    String LMDBsparqlQueryString = sparqler.LMDBQuery();
                    Query query = QueryFactory.create(LMDBsparqlQueryString);
                    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
                    ResultSet results;
                    try {
                        results = qexec.execSelect();
//Each with a try catch, so that we get still data, when one fails
                        for (; results.hasNext(); ) {
                            QuerySolution soln = results.nextSolution();
                            String movieResource = "";
                            try {
                                movieResource = soln.getResource("m").toString();
                            } catch (Exception e) {
                                Log.d("movieList Problem ", e.toString());
                            }
                            String title = "";
                            try {
                                title = InputCleaner.cleanMovieTitle(soln.getLiteral("t"));
                            } catch (Exception e) {
                                Log.d("movieList Problem ", e.toString());
                            }
                            int releaseYear = 0;
                            try {
                                Literal releaseYearLiteral = soln.getLiteral("y");
                                releaseYear = InputCleaner.cleanReleaseYear(releaseYearLiteral);
                            } catch (Exception e) {
                                Log.d("movieList Problem ", e.toString());
                            }
                            String imdbId = "";
                            try {
                                imdbId = InputCleaner.cleanImdbId(soln.getResource("p"));
                            } catch (Exception e) {
                                Log.d("movieList Problem ", e.toString());
                            }
                            String genreName = "";
                            try {
                                genreName = InputCleaner.cleanGenreName(soln.getLiteral("gn"));
                            }catch (Exception e) {
                                Log.d("movieList Problem ", e.toString());
                            }

                            Movie movie = new Movie(title, releaseYear, genreName);
                            movie.setImdbId(imdbId);
                            movie.setLMDBmovieResource("<"+movieResource+">");
                            movieList.add(movie);
                        }
                    }catch (Exception e){
                        Log.e("LINKEDMDB", "Failed"+ e.toString());
                        publishProgress("A problem with Linked MDB occurred");
                    }
                    qexec.close();

                  }
              });

            //LMDB is not always useful, in particular criteria combinations, we don't need lmdb
            if(!((Boolean) criteria.get("isCity") || (Boolean) criteria.get("isState") || (Boolean) criteria.get("isRandomRelated") || (Boolean) criteria.get("isRelated"))) {
                if(!((Boolean) criteria.get("isTime") && !((Boolean) criteria.get("isActor")) && !((Boolean) criteria.get("isDirector")) && !((Boolean) criteria.get("isGenre")) && !((Boolean)criteria.get("isPartName")))) {
                tLMDB.start();
                }
            }

            String dbPediaSparqlQueryString = "";
        /* DPBEDIA */
            //Choose the right dbpedia query
            if((Boolean) criteria.get("isRelated")) {
                dbPediaSparqlQueryString = sparqler.relatedDBPEDIAQuery((String)criteria.get("relation"));
            } else if((Boolean)criteria.get("isRandomRelated")) {
                dbPediaSparqlQueryString = sparqler.randomRelatedDBPEDIAQuery((Movie)criteria.get("relatedMovie"));
            } else {
                dbPediaSparqlQueryString = sparqler.DBPEDIAQuery();
            }
//Get the DBpedia movies
            Query query = QueryFactory.create(dbPediaSparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();

                    String movieResource = "";
                    try {
                        movieResource = soln.getResource("m").toString();
                    } catch (Exception e) {
                        Log.d("movieList Problem ", e.toString());
                    }
                    String title = "";
                    try {
                        title = InputCleaner.cleanMovieTitle(soln.getLiteral("t"));
                    } catch (Exception e) {
                        Log.d("movieList Problem ", e.toString());
                    }

                    int releaseYear = 0;
                    try {
                        Literal releaseYearLiteral = soln.getLiteral("y");
                        releaseYear = InputCleaner.cleanReleaseYear(releaseYearLiteral);
                    } catch (Exception e) {
                        Log.d("movieList Problem ", e.toString());
                    }
                    String genreName = "";
                    try {
                        genreName = InputCleaner.cleanGenreName(soln.getLiteral("gn"));
                    } catch (Exception e) {
                        Log.d("movieList Problem ", e.toString());
                    }

                    Movie movie = new Movie(title, releaseYear, genreName);
                    movie.setDBPmovieResource("<"+movieResource+">");
                    movieList.add(movie);
                }
            } catch (Exception e) {
                Log.e("DBPEDIA", "Failed DBPEDIA DOWN" + e.toString());
                publishProgress("A problem with DBpedia occurred");
            }
            qexec.close();

            //if lmdb thread did not start, we don't need to join it
            if(!((Boolean) criteria.get("isCity") || (Boolean) criteria.get("isState") || (Boolean) criteria.get("isRandomRelated") || (Boolean) criteria.get("isRelated"))) {
                if(!((Boolean) criteria.get("isTime") && !((Boolean) criteria.get("isActor")) && !((Boolean) criteria.get("isDirector")) && !((Boolean) criteria.get("isGenre")) && !((Boolean)criteria.get("isPartName")))) {
                    try {
                        tLMDB.join(25000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                    }
                }
            }
            publishProgress("Sparql result is loaded");
        /* Eliminate doublicates */
            if(movieList.size() >= 750 ) {
                publishProgress("Maximum Number of Movies reached. There might be some movies missing. Please specify your search");
            }

            //Removies Doublicates
            //Before removing a movie, all the data will be saved in the one with same title
            ArrayList indexArray = new ArrayList();
            for (int i = 0; i < movieList.size(); i++) {
                for (int j = i + 1; j < movieList.size(); j++) {
                    if (movieList.get(i).getTitle().equals(movieList.get(j).getTitle())) {
                        if (!"".equals(movieList.get(j).getImdbId())) {
                            movieList.get(j).setMovieResource(movieList.get(i).getMovieResource());
                            indexArray.add(movieList.get(i));
                        } else if (0 != movieList.get(j).getReleaseYear()) {
                            movieList.get(j).setMovieResource(movieList.get(i).getMovieResource());
                            indexArray.add(movieList.get(i));
                        } else if (!"".equals(movieList.get(j).getGenre())) {
                            movieList.get(j).setMovieResource(movieList.get(i).getMovieResource());
                            indexArray.add(movieList.get(i));
                        } else {
                            movieList.get(i).setMovieResource(movieList.get(j).getMovieResource());
                            indexArray.add(movieList.get(j));
                        }
                    }
                }
            }
            movieList.removeAll(indexArray);

            publishProgress("Results are merged. Now loading Rating");
            return movieList;
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onProgressUpdate (String... values) {
            Toast.makeText(that, values[0], Toast.LENGTH_SHORT).show();
        }

        public void onPostExecute(ArrayList<Movie> movieList) {
            //If time criteria was used as OPTIONAL in the SPARQL, it needs to be filtered afterwards. On this way we get better results, because the date is worse maintained in the databases
            //If it is still 0, we might get a time from the web service. So keep those
            if (movieList.size() > 0) {
                if ((Boolean) criteria.get("isTime") && ((Boolean) criteria.get("isActor") || (Boolean) criteria.get("isDirector") || (Boolean) criteria.get("isCity") || (Boolean) criteria.get("isState") || (Boolean) criteria.get("isTime") || (Boolean) criteria.get("isPartName"))) {
                    Iterator<Movie> i = movieList.iterator();
                    while (i.hasNext()) {
                        Movie movie = i.next();
                        if (!(0 == movie.getReleaseYear()) &&  SparqlQueries.filterReleaseDate(movie)) {
                            i.remove();
                        }
                    }
                }
        //same for genre
                if ((Boolean) criteria.get("isGenre") && ((Boolean) criteria.get("isActor") || (Boolean) criteria.get("isDirector") || (Boolean) criteria.get("isCity") || (Boolean) criteria.get("isState") || (Boolean) criteria.get("isTime") || (Boolean) criteria.get("isPartName"))) {
                    Iterator<Movie> i = movieList.iterator();
                    while (i.hasNext()) {
                        Movie movie = i.next();
                        if (!"".equals(movie.getGenre()) &&  SparqlQueries.filterGenre(movie)) {
                            i.remove();
                        }
                    }
                }

                //If we have over 200 movies we don't load the imdb rating immediately
                if(movieList.size() <= 200) {
                    staticRequestCanceled = true;
                    //Now load the rating and additional times or genres
                    HttpRequester.addOmdbData(that, movieList, mlAdapter, (Boolean) criteria.get("isTime"), (Boolean) criteria.get("isGenre"), (Boolean) criteria.get("isActor"), (Boolean) criteria.get("isDirector"), (Boolean) criteria.get("isCity"), (Boolean) criteria.get("isState"), (Boolean) criteria.get("isPartName"));
                } else {
                    //Add the movies to the list and hide the progress indicator
                    imdbButton.setVisible(true);
                    mlAdapter.addAll(movieList);
                    that.setProgressBarIndeterminateVisibility(false);
                    MovieList.staticMovieList = movieList;
                }
            } else {
                //If no movies are found, make a dialog box for this
                AlertDialog ad = new AlertDialog.Builder(that).create();
                ad.setMessage("No movies found!");
                ad.setCancelable(false); // This blocks the 'BACK' button
                ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        that.finish();
                    }
                });
                ad.show();
                setProgressBarIndeterminateVisibility(false);
            }
        }
    }
}
