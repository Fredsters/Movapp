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

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.EventListener;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.model.MovieDetail;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;


public class Detail extends Activity {

    private Activity that = this;
   // MovieDetail movieDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_detail);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Intent intent = getIntent();
        Movie movie = (Movie)intent.getParcelableExtra("movie");

        final MovieDetail movieD = new MovieDetail(movie);

        movieD.setOnFinishedEventListener(new EventListener() {
            @Override
            public void onFinished(MovieDetail movie) {
                //TODO update UIELEMENTS
                //TODO DO THIS Design Pattern with list too??
                //TODO Check why so many films go missing when searching with only time or only genre
                //TODO Buttons colored
                //TODO Detail screen bauen
                //TODO Trailer and Picture in view.
                setProgressBarIndeterminateVisibility(false);
                Log.d("movie", movie.toString());
            }
        });

        TextView movieTitle = (TextView) findViewById(R.id.tvMovieTitle);
        movieTitle.setText(movie.getTitle());

        TextView movieRating = (TextView) findViewById(R.id.tvMovieRating);
        movieRating.setText(movie.getImdbRating());

      //  TextView movieAbstract = (TextView) findViewById(R.id.tvAbstract);
       // movieRating.setText(movie.getAbstract());

        TextView genre = (TextView) findViewById(R.id.tvGenre);
        genre.setText(String.valueOf(movie.getGenre()));

        TextView releaseYear = (TextView) findViewById(R.id.tvReleaseYear);
        releaseYear.setText(String.valueOf(movie.getReleaseYear()) );

        TextView ImdbId = (TextView) findViewById(R.id.ImdbId);
        ImdbId.setText(String.valueOf(movie.getImdbId()));

        queryForMovieData q = new queryForMovieData();
        q.execute(movieD);

    }

    public static boolean updateUiComponents(MovieDetail movie) {
        return false;
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

    private class queryForMovieData extends AsyncTask<MovieDetail, MovieDetail, MovieDetail> {

        @Override
        protected MovieDetail doInBackground(MovieDetail... movieArray) {

            SparqlQueries sparqler = new SparqlQueries();
            MovieDetail movieDetail = movieArray[0];
        /* LMDB */
            String LMDBsparqlQueryString = sparqler.LMDBDetailQuery(movieDetail);
            Query query = QueryFactory.create(LMDBsparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    if(soln.getLiteral("r") != null && "".equals(movieDetail.getRuntime())) {
                        movieDetail.setRuntime(soln.getLiteral("r").getString());
                    }
                    if(soln.getLiteral("aN") != null && !movieDetail.getActors().contains(soln.getLiteral("aN").getString())) {
                        movieDetail.addActor(soln.getLiteral("aN").getString());
                    }
                    if(soln.getLiteral("dN") != null && !movieDetail.getDirectors().contains(soln.getLiteral("dN").getString())) {
                        movieDetail.addDirector(soln.getLiteral("dN").getString());
                    }
                    if(soln.getLiteral("wN") != null && !movieDetail.getWriters().contains(soln.getLiteral("wN").getString())) {
                        movieDetail.addWriter(soln.getLiteral("wN").getString());
                    }
                    if(soln.getLiteral("gN") != null && !movieDetail.getGenres().contains(soln.getLiteral("gN").getString())) {
                        movieDetail.addGenre(soln.getLiteral("gN").getString());
                    }
                }
            }catch (Exception e){
                Log.e("LINKEDMDBDetail", "Failed " + e.toString());
            }
            qexec.close();


        /* DPBEDIA */

            String dbPediaSparqlQueryString = sparqler.DBPEDIADetailQuery(movieDetail);
            query = QueryFactory.create(dbPediaSparqlQueryString);
            qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();

                    if(soln.getLiteral("abs") != null && "".equals(movieDetail.getWikiAbstract())) {
                        movieDetail.setWikiAbstract(soln.getLiteral("abs").getString());
                    }
                    if(soln.getLiteral("bu") != null && "".equals(movieDetail.getBudget())) {
                        movieDetail.setBudget(soln.getLiteral("bu").getString());
                    }
                }
            }catch (Exception e){
                Log.e("DBPEDIADetail", "Failed DBPEDIA DOWN "+ e.toString());
            }
            qexec.close();

            return movieDetail;
        }
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }
        public void onPostExecute(MovieDetail movieDetail) {
            HttpRequester.loadWebServiceData(that, movieDetail);
        }
    }
}
