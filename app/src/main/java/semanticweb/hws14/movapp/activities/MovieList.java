package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.model.MovieComparator;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;


public class MovieList extends Activity {

    protected ArrayAdapter<Movie> mlAdapter;
    private Activity that = this;
    HashMap<String, Object> criteria;

    public static ArrayList<Movie> staticMovieList;
    static HashMap<String, Object> staticCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        final ArrayList<Movie> movieList = new ArrayList<Movie>();

        criteria = (HashMap<String, Object>)intent.getSerializableExtra("criteria");
        ListView listView = (ListView) findViewById(R.id.resultList);
        if(criteria.equals(staticCriteria)) {
            this.mlAdapter = new ArrayAdapter<Movie>(this,android.R.layout.simple_list_item_1, movieList);

            listView.setAdapter(mlAdapter);
            mlAdapter.addAll(staticMovieList);

        } else {
            this.mlAdapter = new ArrayAdapter<Movie>(this,android.R.layout.simple_list_item_1, movieList);

            listView.setAdapter(mlAdapter);
            //Executes SPARQL Queries, Private class queryForMovies is called.
            staticCriteria = criteria;
            queryForMovies q = new queryForMovies();
            q.execute(criteria);
        }

        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(that, Detail.class);
                Movie movie = movieList.get(position);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(clickListen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class queryForMovies extends AsyncTask<HashMap<String, Object>, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(HashMap<String, Object>... criterias) {

           final SparqlQueries sparqler = new SparqlQueries(criterias[0]);

           final ArrayList<Movie> movieList = new ArrayList<Movie>();


            /* LMDB */
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

                        for (; results.hasNext(); ) {
                            QuerySolution soln = results.nextSolution();

                            String title = InputCleaner.cleanMovieTitle(soln.getLiteral("t"));
                            Literal movieId = soln.getLiteral("i");
                            Literal releaseYearLiteral = soln.getLiteral("y");
                            int releaseYear = InputCleaner.cleanReleaseYear(releaseYearLiteral);
                            String imdbId = InputCleaner.cleanImdbId(soln.getResource("p"));
                            String genreName = InputCleaner.cleanGenreName(soln.getLiteral("gn"));

                            Movie movie = new Movie(title, movieId.getInt(), releaseYear,imdbId, genreName);
                            movieList.add(movie);
                        }
                    }catch (Exception e){
                        Log.e("LINKEDMDB", "Failed"+ e.toString());
                        Toast.makeText(that, "A problem with LinkedMDB occured", Toast.LENGTH_SHORT).show();
                    }
                    qexec.close();

                }
            });

            tLMDB.start();
        /* DPBEDIA */

            String dbPediaSparqlQueryString = sparqler.DBPEDIAQuery();
            Query query = QueryFactory.create(dbPediaSparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    String title = InputCleaner.cleanMovieTitle(soln.getLiteral("t"));
                    Literal releaseYearLiteral = soln.getLiteral("y");
                    String genreName = InputCleaner.cleanGenreName(soln.getLiteral("gn"));

                    Movie movie = new Movie(title, 0, InputCleaner.cleanReleaseYear(releaseYearLiteral), genreName);
                    movieList.add(movie);
                }
            } catch (Exception e) {
                Log.e("DBPEDIA", "Failed DBPEDIA DOWN" + e.toString());
                Toast.makeText(that, "A problem with DBPedia occured", Toast.LENGTH_SHORT).show();
            }
            qexec.close();

            try {
                tLMDB.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        /* Eliminate doublicates */

            if(movieList.size() == 200 ) {
                Toast.makeText(that, "Maximum Number of Movies reached. There might be some movies missing. Please specify your search", Toast.LENGTH_SHORT).show();
            }

            ArrayList indexArray = new ArrayList();
            for(int i=0; i<movieList.size();i++) {
                for(int j=i+1; j<movieList.size();j++) {
                    if(movieList.get(i).getTitle().equals(movieList.get(j).getTitle())) {
                            indexArray.add(movieList.get(j));
                    }
                }
            }
            movieList.removeAll(indexArray);

            return movieList;
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        public void onPostExecute(ArrayList<Movie> movieList) {
            if (movieList.size() > 0) {
                if ((Boolean) criteria.get("isTime")) {
                    Iterator<Movie> i = movieList.iterator();
                    while (i.hasNext()) {
                        Movie movie = i.next();
                        if (movie.getReleaseYear() != 0 && SparqlQueries.filterReleaseDate(movie)) {
                            i.remove();
                        }
                    }
                }

                mlAdapter.addAll(movieList);
                HttpRequester.addOmdbData(that, movieList, mlAdapter, (Boolean) criteria.get("isTime"), (Boolean) criteria.get("isGenre"), (Boolean) criteria.get("isActor"), (Boolean) criteria.get("isDirector"));
            } else {
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
