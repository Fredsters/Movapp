package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.EventListener;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.model.MovieDet;
import semanticweb.hws14.movapp.request.HttpRequestQueueSingleton;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;


public class MovieDetail extends Activity {

    private Activity that = this;
    private MovieDet movieDet;
    private Button btnSpoiler;
    private Button btnActorList;
    private int rowCount=0;
    private queryForMovieData q;
    private boolean staticRequestCanceled;
    private static Movie staticMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_movie_detail);

        initDetailView();

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");
        MovieDet movieD = new MovieDet(movie);
        if(movie.equals(staticMovie)) {
            movie.geteListener().onFinished(movieDet);
        } else {
            staticMovie = movie;
            q = new queryForMovieData();
            q.execute(movieD);
        }

        movieD.setOnFinishedEventListener(new EventListener() {
            @Override
            public void onFinished(final MovieDet movie) {
                movieDet = movie;
                staticRequestCanceled = false;
                setData(movie);
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
        if (id == R.id.action_settings) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop () {
        super.onStop();
        if(null != q) {
            q.cancel(true);
        }
        if(staticRequestCanceled) {
            HttpRequestQueueSingleton.getInstance(this.getApplicationContext()).cancelPendingRequests("movieDetail");
        }
    }

    private void colorIt(TextView tv){
        LinearLayout p = (LinearLayout) tv.getParent();
        if(rowCount%2==0){

            //darker color - intvalue: 1947832
            p.setBackgroundColor(Color.rgb(206,238,237));

        }
        else{
            //brighter color - intvalue: 16764144
            p.setBackgroundColor(Color.rgb(236,248,248));
        }
        rowCount++;
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

        btnActorList =  (Button) findViewById(R.id.btnToActorList);
        btnActorList.setVisibility(View.GONE);
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

    private void setData(final MovieDet movie) {
        Thread picThread = new Thread(new Runnable() {
            public void run() {
                try {
                    ImageView img = (ImageView) findViewById(R.id.imageViewMovie);
                    URL url = new URL(movie.getPoster());
                    HttpGet httpRequest;

                    httpRequest = new HttpGet(url.toURI());

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(httpRequest);

                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
                    InputStream input = b_entity.getContent();

                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    img.setImageBitmap(bitmap);

                } catch (Exception ex) {
                    Log.d("MovieDetail Picture Error ", ex.toString());
                }
            }
        });

        picThread.start();

        TextView moviePlot = (TextView) findViewById(R.id.tvPlot);
        String moviePlotText = movie.getPlot();

        if(moviePlotText.equals("")){
            moviePlot.setVisibility(View.GONE);
        }
        else{
            moviePlot.setVisibility(View.VISIBLE);
            moviePlot.setText(moviePlotText);
        }

        TextView tvGenreHc = (TextView) findViewById(R.id.tvGenreHC);
        TextView genre = (TextView) findViewById(R.id.tvGenre);
        ArrayList<String> genreList = movie.getGenres();

        if(genreList.size() == 0){
            genre.setVisibility(View.GONE);
            tvGenreHc.setVisibility(View.GONE);
        }
        else{
            genre.setVisibility(View.VISIBLE);
            tvGenreHc.setVisibility(View.VISIBLE);
            genre.setText(String.valueOf(movie.createTvOutOfList(genreList)));
            colorIt(tvGenreHc);
        }

        TextView releaseYear = (TextView) findViewById(R.id.tvReleaseYear);
        TextView releaseYearHc = (TextView) findViewById(R.id.tvReleaseYearHC);
        int releaseYearText = movie.getReleaseYear();

        if(releaseYearText == 0){
            releaseYear.setVisibility(View.GONE);
            releaseYearHc.setVisibility(View.GONE);
        }
        else{
            releaseYear.setVisibility(View.VISIBLE);
            releaseYearHc.setVisibility(View.VISIBLE);
            releaseYear.setText(String.valueOf(releaseYearText) );
            colorIt(releaseYear);
        }

        TextView runtime = (TextView) findViewById(R.id.tvRuntime);
        TextView runTimeHc = (TextView) findViewById(R.id.tvRuntimeHC);
        String runtimeText = movie.getRuntime();

        if(runtimeText.equals("")){
            runtime.setVisibility(View.GONE);
            runTimeHc.setVisibility(View.GONE);
        }
        else{
            runtime.setVisibility(View.VISIBLE);
            runTimeHc.setVisibility(View.VISIBLE);
            runtime.setText(String.valueOf(movie.getRuntime() + " minutes") );
            colorIt(runTimeHc);
        }

        TextView budget = (TextView) findViewById(R.id.tvBudget);
        TextView budgetHc = (TextView) findViewById(R.id.tvBudgetHC);
        String budgetText = movie.getBudget();

        if(budgetText.contains("E")){
            BigDecimal myNumber = new BigDecimal(budgetText);
            long budgetLong = myNumber.longValue();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            budgetText = formatter.format(budgetLong);
        }

        if(budgetText.equals("")){
            budget.setVisibility(View.GONE);
            budgetHc.setVisibility(View.GONE);
        }
        else{
            budget.setVisibility(View.VISIBLE);
            budgetHc.setVisibility(View.VISIBLE);
            budget.setText(String.valueOf(budgetText));
            colorIt(budgetHc);
        }

        TextView awards = (TextView) findViewById(R.id.tvAwards);
        TextView awardsHc = (TextView) findViewById(R.id.tvAwardsHC);
        String awardsText = movie.getAwards();

        if(awardsText.equals("")){
            awards.setVisibility(View.GONE);
            awardsHc.setVisibility(View.GONE);
        }
        else{
            awards.setVisibility(View.VISIBLE);
            awardsHc.setVisibility(View.VISIBLE);
            awards.setText(awardsText);
            colorIt(awardsHc);
        }

        TextView tvDirHc = (TextView) findViewById(R.id.tvDirectorsHC);
        TextView directors = (TextView) findViewById(R.id.tvDirectors);
        ArrayList<String> directorslist = movie.getDirectors();

        if(directorslist.size() == 0){
            directors.setVisibility(View.GONE);
            tvDirHc.setVisibility(View.GONE);
        }
        else{
            directors.setVisibility(View.VISIBLE);
            tvDirHc.setVisibility(View.VISIBLE);
            directors.setText(String.valueOf(movie.createTvOutOfList(directorslist)));
            colorIt(tvDirHc);
        }

        TextView tvWriterHc = (TextView) findViewById(R.id.tvWritersHC);
        TextView writers = (TextView) findViewById(R.id.tvWriters);
        ArrayList<String> writerslist = movie.getWriters();

        if(writerslist.size() == 0){
            writers.setVisibility(View.GONE);
            tvWriterHc.setVisibility(View.GONE);
        }
        else{
            writers.setVisibility(View.VISIBLE);
            tvWriterHc.setVisibility(View.VISIBLE);
            writers.setText(String.valueOf(movie.createTvOutOfList(writerslist)));
            colorIt(tvWriterHc);
        }

        TextView tvActorsHc = (TextView) findViewById(R.id.tvActorsHC);
        TextView actors = (TextView) findViewById(R.id.tvActors);
        ArrayList<String> actorsList = movie.getActors();

        if(actorsList.size() == 0){
            actors.setVisibility(View.GONE);
            tvActorsHc.setVisibility(View.GONE);
            btnActorList.setVisibility(View.GONE);
        }
        else{
            actors.setVisibility(View.VISIBLE);
            tvActorsHc.setVisibility(View.VISIBLE);
            btnActorList.setVisibility(View.VISIBLE);
            actors.setText(String.valueOf(movie.createTvOutOfList(actorsList)));
            colorIt(tvActorsHc);
        }

        TextView ageRestriction = (TextView) findViewById(R.id.tvAgeRestriction);
        TextView arHc = (TextView) findViewById(R.id.tvAgeRestrictionHC);
        String aR = String.valueOf(movie.getRated());


        if(aR.equals("")){
            ageRestriction.setVisibility(View.GONE);
            arHc.setVisibility(View.GONE);
        }
        else{
            ageRestriction.setVisibility(View.VISIBLE);
            arHc.setVisibility(View.VISIBLE);
        }

        if(aR.equals("X")){
            ageRestriction.setText("18+");
        }
        else if(aR.equals("R")){
            ageRestriction.setText("16+");
        }
        else if(aR.equals("M")){
            ageRestriction.setText("12+");
        }
        else if(aR.equals("G")){
            ageRestriction.setText("6+");
        }
        else{
            ageRestriction.setVisibility(View.GONE);
            arHc.setVisibility(View.GONE);
        }

        TextView metaScore = (TextView) findViewById(R.id.tvMetaScore);
        TextView metaScoreHc = (TextView) findViewById(R.id.tvMetaScoreHC);
        int metaSoreText = movie.getMetaScore();

        if(metaSoreText == 0){
            metaScore.setVisibility(View.GONE);
            metaScoreHc.setVisibility(View.GONE);
        }
        else{
            metaScore.setVisibility(View.VISIBLE);
            metaScoreHc.setVisibility(View.VISIBLE);
            metaScore.setText(String.valueOf(metaSoreText+"/100"));
        }


        TextView wikiAbstract = (TextView) findViewById(R.id.tvWikiAbstract);
        String wikiAbstractText = movie.getWikiAbstract();

        if(wikiAbstract.equals("")){
            wikiAbstract.setVisibility(View.GONE);
        }
        else{
            wikiAbstract.setVisibility(View.VISIBLE);
            wikiAbstract.setText((String.valueOf(wikiAbstractText)));
        }

        TextView ratingCount = (TextView) findViewById(R.id.tvMovieRatingCount);
        TextView ratingCountHc = (TextView) findViewById(R.id.tvMovieRatingCountHC);
        String ratingCountText = movie.getVoteCount();

        if(ratingCountText.equals("")){
            ratingCount.setVisibility(View.GONE);
            ratingCountHc.setVisibility(View.GONE);
        }
        else{
            ratingCount.setVisibility(View.VISIBLE);
            ratingCountHc.setVisibility(View.VISIBLE);
            ratingCount.setText((String.valueOf(ratingCountText)));
        }

        TextView movieRating = (TextView) findViewById(R.id.tvMovieRating);
        movieRating.setText(movie.getImdbRating()+"/10");

        try {
            picThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setProgressBarIndeterminateVisibility(false);
    }


    private class queryForMovieData extends AsyncTask<MovieDet, String, MovieDet> {

        @Override
        protected MovieDet doInBackground(MovieDet... movieArray) {
            staticRequestCanceled = true;
            final SparqlQueries sparqler = new SparqlQueries();
            final MovieDet movie = movieArray[0];
        /* LMDB */
            Thread tLMDBDetail = new Thread(new Runnable() {
                public void run() {
                    String LMDBsparqlQueryString = sparqler.LMDBDetailQuery(movie);
                    Query query = QueryFactory.create(LMDBsparqlQueryString);
                    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
                    ResultSet results;
                    try {
                        results = qexec.execSelect();
                        for (; results.hasNext(); ) {
                            QuerySolution soln = results.nextSolution();
                            try {
                            if (soln.getLiteral("r") != null && "".equals(movie.getRuntime())) {
                                movie.setRuntime(soln.getLiteral("r").getString());
                            }}catch (Exception e) {
                                Log.d("movieDetail Problem ", e.toString());
                            }
                            try {
                            if (soln.getLiteral("aN") != null && !movie.getActors().contains(soln.getLiteral("aN").getString())) {
                                movie.addActor(soln.getLiteral("aN").getString());
                            }}catch (Exception e) {
                                Log.d("movieDetail Problem ", e.toString());
                            }
                            try {
                            if (soln.getLiteral("dN") != null && !movie.getDirectors().contains(soln.getLiteral("dN").getString())) {
                                movie.addDirector(soln.getLiteral("dN").getString());
                            }}catch (Exception e) {
                                Log.d("movieDetail Problem ", e.toString());
                            }
                            try {
                            if (soln.getLiteral("wN") != null && !movie.getWriters().contains(soln.getLiteral("wN").getString())) {
                                movie.addWriter(soln.getLiteral("wN").getString());
                            }}catch (Exception e) {
                                Log.d("movieDetail Problem ", e.toString());
                            }
                            try {
                            if (soln.getLiteral("gN") != null && !movie.getGenres().contains(soln.getLiteral("gN").getString())) {
                                movie.addGenre(soln.getLiteral("gN").getString());
                            }}catch (Exception e) {
                                Log.d("movieDetail Problem ", e.toString());
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

            String dbPediaSparqlQueryString = sparqler.DBPEDIADetailQuery(movie);
            Query query = QueryFactory.create(dbPediaSparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    try {
                    if(soln.getLiteral("abs") != null && "".equals(movie.getWikiAbstract())) {
                        movie.setWikiAbstract(soln.getLiteral("abs").getString());
                    }}catch (Exception e) {
                        Log.d("movieDetail Problem ", e.toString());
                    }
                    try {
                    if(soln.getLiteral("bu") != null && "".equals(movie.getBudget())) {
                        movie.setBudget(soln.getLiteral("bu").getString());
                    }}catch (Exception e) {
                        Log.d("movieDetail Problem ", e.toString());
                    }
                    try {
                    if (soln.getLiteral("r") != null && "".equals(movie.getRuntime())) {
                        movie.setRuntime(soln.getLiteral("r").getString());
                    }}catch (Exception e) {
                        Log.d("movieDetail Problem ", e.toString());
                    }
                    try {
                    if (soln.getLiteral("aN") != null && !movie.getActors().contains(soln.getLiteral("aN").getString())) {
                        movie.addActor(soln.getLiteral("aN").getString());
                    }}catch (Exception e) {
                        Log.d("movieDetail Problem ", e.toString());
                    }
                    try {
                    if (soln.getLiteral("dN") != null && !movie.getDirectors().contains(soln.getLiteral("dN").getString())) {
                        movie.addDirector(soln.getLiteral("dN").getString());
                    }}catch (Exception e) {
                        Log.d("movieDetail Problem ", e.toString());
                    }
                    try {
                    if (soln.getLiteral("wN") != null && !movie.getWriters().contains(soln.getLiteral("wN").getString())) {
                        movie.addWriter(soln.getLiteral("wN").getString());
                    }}catch (Exception e) {
                        Log.d("movieDetail Problem ", e.toString());
                    }
                    try {
                    if (soln.getLiteral("gN") != null && !movie.getGenres().contains(soln.getLiteral("gN").getString())) {
                        movie.addGenre(soln.getLiteral("gN").getString());
                    }}catch (Exception e) {
                        Log.d("movieDetail Problem ", e.toString());
                    }
                }
            }catch (Exception e){
                Log.e("DBPEDIADetail", "Failed DBPEDIA DOWN "+ e.toString());
                publishProgress("A problem with DBPedia occured");
            }
            qexec.close();

            try {
                tLMDBDetail.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return movie;
        }

        @Override
        protected void onProgressUpdate (String... values) {
            Toast.makeText(that, values[0], Toast.LENGTH_LONG).show();
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }
        public void onPostExecute(MovieDet movie) {
            HttpRequester.loadWebServiceData(that, movie);
        }
    }
}


//DEINE
//TODO Buttons colored  different when criteria is active (olli)
//TODO nicer layout in Detail( Texts should always have same offset, so that the length of the label does not matter) (oLLI)
//TODO nicer Layout in listview (olli)
//TODO implement a back button (olli)
//TODO Close keyboard with return button(olli)
//TODO Link to imdb and actor page (only display button when link is available) (olli)
//TODO Style for criteria actor (olli)

//Hier kannste dir was nehmen :)
//TODO Use Foaf (other) database for better actor info
//TODO Animate the panel open close in criteria view
//TODO try to figure some bugs out, when app crashs after longer use and several foreward backward navigations
//TODO Change titles of activities (that what is displayed in the action bar)
//TODO Additional Actor Criteria einen namen eingeben und alle actor kriegen die diesen als vor oder nachname haben oder sonst im namen haben
//TODO Endcoding: Manchmal gibts komische Namen, die keine gescheiten Buchstaben haben.

//DONE einigermaßen
//TODO Das Mit dem Absturz wenn geladen wird und dann die activit geändert wird  sollte behoben werden
//TODO Memory leaks --> That can be the reason for the slow loading. But I can't say how to remove them correctly. Or just a phone with more ram :D

//MEINE
//TODO ERROR bei kate winslet (fred)
//TODO Kate Upton not born in USA?
//TODO Delete unnessecary code and auskommentierten code und erklärende kommentare adden (fred)
//TODO make year array bigger. Every number (fred)
//TODO add more cities and states in array (fred)
