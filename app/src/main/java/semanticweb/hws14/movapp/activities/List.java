package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;


public class List extends Activity {

    protected ArrayAdapter<Movie> mlAdapter;
    private Activity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        String actorName = intent.getStringExtra("actorName");

        ArrayList<Movie> movieList = new ArrayList<Movie>();
        this.mlAdapter = new ArrayAdapter<Movie>(this,android.R.layout.simple_list_item_1, movieList);

        //TODO: Remove and make the query async
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

       ListView listView = (ListView) findViewById(R.id.resultList);
       listView.setAdapter(mlAdapter);


        queryForMovies q = new queryForMovies();
        q.execute(actorName);



  //      Collections.sort(movieList,  new MovieComparator());

/*        for(Movie movie : movieList) {
            Log.d(" Rating: " +movie.getImdbRating() + "   Title: ",movie.getTitle());
        }*/
        //HttpRequester.addImdbRating(this, this.movieList);

//        ArrayAdapter adapter = new ArrayAdapter<Movie>(list.this,android.R.layout.simple_list_item_1, movieList);
    //    listView.setAdapter(adapter);


        // TODO : Work this shit
        //adds ImdbRating to the movieList. Since the Http Request to get the imdb Rating is
        // asynchronous. The next activity can only started, after the response is received.
        // Therefore the next activity is started within this method
        // It would be much nicer if i could make the Http Request synchronous --> Does not work for now
        // The best would be if everything is async again, but for now its ok like this.
        // The whole damn thing is so extraordinary ugly. It would not get laid.
        // IN SHORT: This methods add ImdbRating and starts next Activity


        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: edit startIntent in a way that its open the activity result
                startIntent();


            }
        };
        listView.setOnItemClickListener(clickListen);
    }

    private void startIntent() {
        Intent intent = new Intent(this,Detail.class);
    }

    private Movie[] convertToArray(ArrayList<Movie> movieList) {

        Movie[] m = new Movie[movieList.size()];
        for(int index=0;index < movieList.size();index++){
            m[index] = movieList.get(index);
        }
        return m;

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


    private class queryForMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

        //TODO: Input bereinigen
        //TODO start buffer gif or soemthing
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            String actorName = params[0];
            ArrayList<Movie> movieList = new ArrayList<Movie>();


        /* LMDB */
            String LMDBsparqlQueryString = SparqlQueries.LMDBQuery(actorName);
            Query query = QueryFactory.create(LMDBsparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
            ResultSet results = qexec.execSelect();

            for (; results.hasNext(); ) {
                QuerySolution soln = results.nextSolution();
                Literal title = soln.getLiteral("t");
                Literal movieId = soln.getLiteral("i");
                Literal releaseYearLiteral = soln.getLiteral("y");
                String releaseYearString = releaseYearLiteral.toString();
                Pattern p = Pattern.compile("\\d\\d\\d\\d");
                Matcher m = p.matcher(releaseYearString);
                if(m.find()) {
                    releaseYearString = m.group();
                }
                int releaseYear = Integer.parseInt(releaseYearString);

                Movie movie = new Movie(title.getString(), movieId.getInt()/*, releaseYear*/);
                movieList.add(movie);
            }
            qexec.close();


        /* DPBEDIA */
            String dbPediaSparqlQueryString = SparqlQueries.DBPEDIAQuery(actorName);
            query = QueryFactory.create(dbPediaSparqlQueryString);
            qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            results = qexec.execSelect();

            for (; results.hasNext(); ) {
                QuerySolution soln = results.nextSolution();
                Literal title = soln.getLiteral("t");
                Movie movie = new Movie(title.getString(),1999);
                movieList.add(movie);
            }
            qexec.close();

            //TODO Encode & und and
        /* Put the Lists together */
            for(int i=0; i<movieList.size();i++) {
                for(int j=i+1; j<movieList.size();j++) {
                    if(movieList.get(i).getTitle().equals(movieList.get(j).getTitle())) {
                        movieList.remove(movieList.get(j));
                        break;
                    }
                }
            }
            return movieList;
        }

        public void onPostExecute(ArrayList<Movie> movieList) {
            mlAdapter.addAll(movieList);
            mlAdapter.notifyDataSetChanged();

           movieList =  HttpRequester.addImdbRating(that, movieList, mlAdapter);
        }
    }
}
