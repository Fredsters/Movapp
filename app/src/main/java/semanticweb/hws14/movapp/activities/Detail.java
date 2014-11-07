package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import java.util.ArrayList;
import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.model.MovieDetail;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;


public class Detail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_detail);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();
        Movie movie = (Movie)intent.getParcelableExtra("movie");

        TextView movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieTitle.setText(movie.getTitle());

        TextView movieRating = (TextView) findViewById(R.id.movieRating);
        movieRating.setText(movie.getImdbRating());

        TextView releaseYear = (TextView) findViewById(R.id.releaseYear);
        releaseYear.setText(String.valueOf(movie.getReleaseYear()) );

        TextView ImdbId = (TextView) findViewById(R.id.ImdbId);
        ImdbId.setText(String.valueOf(movie.getImdbId()));

        queryForMovieData q = new queryForMovieData();
        q.execute(movie);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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

    private class queryForMovieData extends AsyncTask<Movie, Movie, Movie> {

        @Override
        protected Movie doInBackground(Movie... movieArray) {

            SparqlQueries sparqler = new SparqlQueries();
            MovieDetail movie = new MovieDetail(movieArray[0]);
        /* LMDB */
            String LMDBsparqlQueryString = sparqler.LMDBDetailQuery(movie);
            Query query = QueryFactory.create(LMDBsparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    if("".equals(movie.getRuntime())) {
                        movie.setRuntime(soln.getLiteral("r").getString());
                    }
                    if(!movie.getActors().contains(soln.getLiteral("aN").getString())) {
                        movie.addActor(soln.getLiteral("aN").getString());
                    }

                    if(!movie.getDirectors().contains(soln.getLiteral("dN").getString())) {
                        movie.addDirector(soln.getLiteral("dN").getString());
                    }

                    if(!movie.getWriters().contains(soln.getLiteral("wN").getString())) {
                        movie.addWriter(soln.getLiteral("wN").getString());
                    }
                    if(!movie.getGenres().contains(soln.getLiteral("gN").getString())) {
                        movie.addGenre(soln.getLiteral("gN").getString());
                    }
                }
            }catch (Exception e){
                Log.e("LINKEDMDBDetail", "Failed" + e.toString());
            }
            qexec.close();


        /* DPBEDIA */
/*
            String dbPediaSparqlQueryStringDetail = sparqlerus.DBPEDIADetailQuery();
            query = QueryFactory.create(dbPediaSparqlQueryStringDetail);
            qexecDetail = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    //Todo set Movie Attributes
                }
            }catch (Exception e){
                Log.e("DBPEDIADetail", "Failed DBPEDIA DOWN"+ e.toString());
            }
            qexec.close();
*/
            return movie;
        }
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }
        public void onPostExecute(Movie movie) {
            //UPdate UI Elementes
            setProgressBarIndeterminateVisibility(false);
        }
    }
}
