package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;


public class List extends Activity {

    protected ArrayAdapter<Movie> mlAdapter;
    private Activity that = this;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        String actorName = intent.getStringExtra("actorName");

        final ArrayList<Movie> movieList = new ArrayList<Movie>();
        this.mlAdapter = new ArrayAdapter<Movie>(this,android.R.layout.simple_list_item_1, movieList);

//        Only neccessary if we use Request calls in the UI-Thread
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ListView listView = (ListView) findViewById(R.id.resultList);
        listView.setAdapter(mlAdapter);


        queryForMovies q = new queryForMovies();
        //Executes SPARQL Queries, Private class queryForMovies is called.
        q.execute(actorName);

        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(that, Detail.class);
                intent.putExtra("movie", movieList.get(position));
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


    private class queryForMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

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
                String title = InputCleaner.cleanMovieTitle(soln.getLiteral("t").getString());
                Literal movieId = soln.getLiteral("i");
                Literal releaseYearLiteral = soln.getLiteral("y");
                String imdbId = InputCleaner.cleanImdbId(soln.getResource("p"));
                Movie movie = new Movie(title, movieId.getInt(), InputCleaner.cleanReleaseYear(releaseYearLiteral),imdbId);
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
                String title = InputCleaner.cleanMovieTitle(soln.getLiteral("t").getString());
                Literal releaseYearLiteral = soln.getLiteral("y");

                Movie movie = new Movie(title, InputCleaner.cleanReleaseYear(releaseYearLiteral));
                movieList.add(movie);
            }
            qexec.close();

        /* Put the Lists together */

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
            progressBar.setVisibility(View.VISIBLE);
        }

        public void onPostExecute(ArrayList<Movie> movieList) {
           HttpRequester.addImdbRating(that, movieList, mlAdapter, progressBar);
        }
    }
}
