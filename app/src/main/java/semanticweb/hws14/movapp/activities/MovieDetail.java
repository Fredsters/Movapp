package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
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
import semanticweb.hws14.movapp.model.MovieDet;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;


public class MovieDetail extends Activity {

    private Activity that = this;
    MovieDet movieDet;
    Button btnSpoiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_movie_detail);

        initDetailView();

        Intent intent = getIntent();
        Movie movie = (Movie)intent.getParcelableExtra("movie");

        final MovieDet movieD = new MovieDet(movie);

        queryForMovieData q = new queryForMovieData();
        q.execute(movieD);

        movieD.setOnFinishedEventListener(new EventListener() {
            @Override
            public void onFinished(final MovieDet movie) {
                movieDet = movie;
                //Todo Title Criteria?
                //TODO Buttons colored
                //TODO get Trailer
                //TODO Show more Ui Elements in movie_detail
                //TODO Mapping from rated to age
                //TODO nicer layout in Detail
                //TODO nicer Layout in listview
                //TODO nicer Layout in Criteriaview
                //TODO 2 actors
                //TODO When pressing home, save state bundle onSave...
                //TODO check in movie_detail if property is there and if not then dont try to display it
                //TODO THeme
                /*

                The original movie ratings consisted of:
Rated G – Acceptable to "general" audiences, including children.
Rated M – For "Mature" audiences.
Rated R – Restricted. Children under the age of 17 must be accompanied by a parent or "guardian" (i.e., supervised by an adult).
Rated X – Children under the age of 17 not admitted.

                 */
                setProgressBarIndeterminateVisibility(false);

            //    Thread pictureThread = new Thread(new Runnable() {
          //          public void run() {
                        WebView web = (WebView) findViewById(R.id.webView);
                        web.loadUrl(movie.getPoster());
         //           }
        //        });
     //           pictureThread.start();

                TextView moviePlot = (TextView) findViewById(R.id.tvPlot);
                moviePlot.setText(movieD.getPlot());

                TextView actors = (TextView) findViewById(R.id.tvActors);
                actors.setText(String.valueOf(movieD.createTvOutOfList(movieD.getActors())));

                TextView directors = (TextView) findViewById(R.id.tvDirectors);
                directors.setText(String.valueOf(movieD.createTvOutOfList(movieD.getDirectors())));

                TextView writers = (TextView) findViewById(R.id.tvWriters);
                writers.setText(String.valueOf(movieD.createTvOutOfList(movieD.getWriters())));



                TextView genre = (TextView) findViewById(R.id.tvGenre);
                genre.setText(String.valueOf(movieD.createTvOutOfList(movieD.getGenres())));

                TextView releaseYear = (TextView) findViewById(R.id.tvReleaseYear);
                releaseYear.setText(String.valueOf(movie.getReleaseYear()) );

                TextView runtime = (TextView) findViewById(R.id.tvRuntime);
                runtime.setText(String.valueOf(movie.getRuntime()) );

                TextView ageRestriction = (TextView) findViewById(R.id.tvAgeRestriction);
                ageRestriction.setText(String.valueOf(movie.getRated()) );

                TextView budget = (TextView) findViewById(R.id.tvBudget);
                budget.setText(String.valueOf(movie.getBudget()) );

                TextView awards = (TextView) findViewById(R.id.tvAwards);
                awards.setText(String.valueOf(movie.getAwards()) );

                TextView metaScore = (TextView) findViewById(R.id.tvMetaScore);
                metaScore.setText(String.valueOf(movie.getMetaScore()+"/100"));

                TextView wikiAbstract = (TextView) findViewById(R.id.tvWikiAbstract);
                wikiAbstract.setText((String.valueOf(movie.getWikiAbstract())));

                TextView ratingCount = (TextView) findViewById(R.id.tvMovieRatingCount);
                ratingCount.setText((String.valueOf(movie.getVoteCount())));

                TextView movieRating = (TextView) findViewById(R.id.tvMovieRating);
                movieRating.setText(movie.getImdbRating()+"/10");
            }

        });

        TextView movieTitle = (TextView) findViewById(R.id.tvMovieTitle);
        movieTitle.setText(movie.getTitle());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movie_detail, menu);
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

    private void initDetailView(){
       View panelSpoiler = findViewById(R.id.panelSpoiler);
        panelSpoiler.setVisibility(View.GONE);

        btnSpoiler = (Button) findViewById(R.id.btnSpoiler);

        btnSpoiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View panelSpoiler = findViewById(R.id.panelSpoiler);
                if(panelSpoiler.getVisibility() == View.VISIBLE) {
                    panelSpoiler.setVisibility(View.GONE);
                } else {
                    panelSpoiler.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void linkToImdb(View view){
        String imdbUrl = "http://www.imdb.com/title/"+ movieDet.getImdbId()+"/";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imdbUrl));
        startActivity(browserIntent);
    }

    public void toActorList(View view) {
        Intent intent = new Intent(that, ActorList.class);
        intent.putStringArrayListExtra("actorList", movieDet.getActors());
        startActivity(intent);
    }

    private class queryForMovieData extends AsyncTask<MovieDet, MovieDet, MovieDet> {

        @Override
        protected MovieDet doInBackground(MovieDet... movieArray) {

            final SparqlQueries sparqler = new SparqlQueries();
            final MovieDet movieDet = movieArray[0];
        /* LMDB */
            Thread tLMDBDetail = new Thread(new Runnable() {
                public void run() {
                    String LMDBsparqlQueryString = sparqler.LMDBDetailQuery(movieDet);
                    Query query = QueryFactory.create(LMDBsparqlQueryString);
                    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
                    ResultSet results;
                    try {
                        results = qexec.execSelect();
                        for (; results.hasNext(); ) {
                            QuerySolution soln = results.nextSolution();
                            if (soln.getLiteral("r") != null && "".equals(movieDet.getRuntime())) {
                                movieDet.setRuntime(soln.getLiteral("r").getString());
                            }
                            if (soln.getLiteral("aN") != null && !movieDet.getActors().contains(soln.getLiteral("aN").getString())) {
                                movieDet.addActor(soln.getLiteral("aN").getString());
                            }
                            if (soln.getLiteral("dN") != null && !movieDet.getDirectors().contains(soln.getLiteral("dN").getString())) {
                                movieDet.addDirector(soln.getLiteral("dN").getString());
                            }
                            if (soln.getLiteral("wN") != null && !movieDet.getWriters().contains(soln.getLiteral("wN").getString())) {
                                movieDet.addWriter(soln.getLiteral("wN").getString());
                            }
                            if (soln.getLiteral("gN") != null && !movieDet.getGenres().contains(soln.getLiteral("gN").getString())) {
                                movieDet.addGenre(soln.getLiteral("gN").getString());
                            }
                        }
                    } catch (Exception e) {
                        Log.e("LINKEDMDBDetail", "Failed " + e.toString());
                    }
                    qexec.close();
                }
            });

            tLMDBDetail.start();
        /* DPBEDIA */

            String dbPediaSparqlQueryString = sparqler.DBPEDIADetailQuery(movieDet);
            Query query = QueryFactory.create(dbPediaSparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();

                    if(soln.getLiteral("abs") != null && "".equals(movieDet.getWikiAbstract())) {
                        movieDet.setWikiAbstract(soln.getLiteral("abs").getString());
                    }
                    if(soln.getLiteral("bu") != null && "".equals(movieDet.getBudget())) {
                        movieDet.setBudget(soln.getLiteral("bu").getString());
                    }
                }
            }catch (Exception e){
                Log.e("DBPEDIADetail", "Failed DBPEDIA DOWN "+ e.toString());
            }
            qexec.close();

            try {
                tLMDBDetail.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return movieDet;
        }
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }
        public void onPostExecute(MovieDet movieDet) {
            HttpRequester.loadWebServiceData(that, movieDet);
        }







    }


}

