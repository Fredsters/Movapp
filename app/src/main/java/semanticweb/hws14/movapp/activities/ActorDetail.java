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
import java.net.URL;
import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.ActorDet;
import semanticweb.hws14.movapp.model.EventActorListener;
import semanticweb.hws14.movapp.request.SparqlQueries;

/**
 * Actors detail page. Shows all available data of the actor.
 */
public class ActorDetail extends Activity {
    private Activity that = this;
    private ActorDet actorDet;
    private int rowCount=0; //needed to color
    private queryforActorDetail q; //private inner class for asyncTask
    Button btnToHomepage;
    Button btnToMovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For Progress Bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_actor_detail);
        //Remove Back Button
        getActionBar().setDisplayHomeAsUpEnabled(false);

        //Initialize the buttons
        btnToHomepage = (Button) findViewById(R.id.btnToHompage);
        btnToMovieList = (Button) findViewById(R.id.btnToMovieList);

        //Get the Actorname and create the actor object for this detail page
        Intent intent = getIntent();
        String actorName = intent.getStringExtra("actorName");
        final ActorDet actor = new ActorDet();
        actor.setName(actorName);

        //start the asyncTask to start the SPARQL query and get the result
        q = new queryforActorDetail();
        q.execute(actor);

        //This method is called when the SPARQL response has returned and the actor object has gotten all the data
        actor.setOnFinishedEventListener(new EventActorListener() {
            @Override
            public void onFinished(ActorDet actor) {
                //make the actor object everywhere in the class available
                actorDet = actor;
                //This methods sets the data in the UI-Components
                setData(actor);
            }
        });
    }

    //Ends asyncTask when Activity is left while asyncTask is running
    @Override
    protected void onStop () {
        super.onStop();
        if(null != q) {
            q.cancel(true);
        }
    }

    //Method to get to MovieList of this actor
    public void toMovieList(View view){
        Intent intent = new Intent(that, MovieList.class);
        HashMap<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("isActor", true);
        criteria.put("actorName", actorDet.getName());
        intent.putExtra("criteria", criteria);
        startActivity(intent);
    }

    //To actors Homepage
    public void toActorHomepage(View view) {
        String homepage = actorDet.getHomepage();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
        startActivity(browserIntent);
    }

    //sets all the data
    private void setData(final ActorDet actor) {
        //Picture
        Thread picThread = new Thread(new Runnable() {
            public void run() {
                try {
                    ImageView img = (ImageView) findViewById(R.id.imageViewActor);
                    URL url = new URL(actor.getPictureURL());
                    HttpGet httpRequest = null;

                    httpRequest = new HttpGet(url.toURI());

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = (HttpResponse) httpclient
                            .execute(httpRequest);

                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
                    InputStream input = b_entity.getContent();

                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    img.setImageBitmap(bitmap);

                } catch (Exception ex) {
                    Log.d("actorDetail", "picture: " + ex.toString());
                }
            }
        });
        picThread.start();

        //Actor Name
        TextView actorDetailName = (TextView) findViewById(R.id.tvActorDetailName);
        actorDetailName.setText(actor.getName());
        actorDetailName.setVisibility(View.VISIBLE);

        //Actor wiki description
        TextView wikiAbstract = (TextView) findViewById(R.id.tvWikiAbstract);
        String wikiAbstractText = actor.getWikiAbstract();

        if(wikiAbstractText.equals("") || wikiAbstractText.equals("N/A")){
            String sorry = "Sorry...There is no detailed description available for this actor.";
            wikiAbstract.setText(sorry);
        } else {
            wikiAbstract.setText(wikiAbstractText);
        }
        wikiAbstract.setVisibility(View.VISIBLE);

        //birthname
        TextView birthName = (TextView) findViewById(R.id.tvBirthName);
        TextView birthNameHc = (TextView) findViewById(R.id.tvBirthNameHC);
        String birthNameText = actor.getBirthName();
        birthName.setText(birthNameText);
        manageEmptyTextfields(birthNameHc,birthName,birthNameText);

        //birthdate
        TextView birthDate = (TextView) findViewById(R.id.tvBirthDate);
        TextView birthDateHc = (TextView) findViewById(R.id.tvBirthDateHC);
        String birthDateText =actor.getBirthDate();
        if(birthDateText.contains("+")){
            birthDateText= birthDateText.substring(0, birthDateText.indexOf("+"));
        }
        birthDate.setText(birthDateText);
        manageEmptyTextfields(birthDateHc,birthDate,birthDateText);

        //birthplace
        TextView birthPlace = (TextView) findViewById(R.id.tvBirthPlace);
        TextView birthPlaceHc = (TextView) findViewById(R.id.tvBirthPlaceHC);
        String birthPlaceText = String.valueOf(actor.createTvOutOfList(actor.getBirthPlace()));
        birthPlace.setText(birthPlaceText);
        manageEmptyTextfields(birthPlaceHc,birthPlace,birthPlaceText);

        //Spouse/partner
        TextView partner = (TextView) findViewById(R.id.tvPartner);
        TextView partnerHc = (TextView) findViewById(R.id.tvPartnerHC);
        String partnerText= actor.getPartner();
        partner.setText(partnerText);
        manageEmptyTextfields(partnerHc,partner,partnerText);

        //Children
        TextView parent = (TextView) findViewById(R.id.tvParent);
        TextView parentHc = (TextView) findViewById(R.id.tvParentHC);
        String parentText = String.valueOf(actor.createTvOutOfList(actor.getParent()));
        parent.setText(parentText);
        manageEmptyTextfields(parentHc,parent,parentText);

        //Amount of Children
        TextView children = (TextView) findViewById(R.id.tvChildren);
        TextView childrenHc = (TextView) findViewById(R.id.tvChildrenHC);
        String childrenText = String.valueOf(actor.getChildren());
        children.setText(childrenText);
        manageEmptyTextfields(childrenHc,children,childrenText);

        //nationality
        TextView nationality = (TextView) findViewById(R.id.tvNationality);
        TextView nationalityHc = (TextView) findViewById(R.id.tvNationalityHC);
        String nationalityText = actor.getNationality();
        nationality.setText(nationalityText);
        manageEmptyTextfields(nationalityHc,nationality,nationalityText);

        //job/occupation
        TextView occupation = (TextView) findViewById(R.id.tvOccupation);
        TextView occupationHc = (TextView) findViewById(R.id.tvOccupationHC);
        String occupationText = actor.getOccupation();
        occupation.setText(occupationText);
        manageEmptyTextfields(occupationHc,occupation,occupationText);

        //active since
        TextView activeYear = (TextView) findViewById(R.id.tvActiveYear);
        TextView activeYearHc = (TextView) findViewById(R.id.tvActiveYearHC);
        String activeYearText = String.valueOf(actor.getActiveYear());
        activeYear.setText(activeYearText);
        manageEmptyTextfields(activeYearHc,activeYear,activeYearText);

        //characters
        TextView roles = (TextView) findViewById(R.id.tvRoles);
        TextView rolesHc = (TextView) findViewById(R.id.tvRolesHC);
        String rolesText = String.valueOf(actor.createTvOutOfList(actor.getRoles()));
        roles.setText(rolesText);
        manageEmptyTextfields(rolesHc,roles,rolesText);

        //movies
        TextView movies = (TextView) findViewById(R.id.tvMovies);
        TextView moviesHc = (TextView) findViewById(R.id.tvMoviesHC);
        String moviesText = String.valueOf(actor.createTvOutOfList(actor.getMovies()));
        movies.setText(moviesText);
        manageEmptyTextfields(moviesHc,movies,moviesText);

        //Set Buttons visible if data is available
        if(!"".equals(actor.getHomepage())) {
            btnToHomepage.setVisibility(View.VISIBLE);
        }
        if(actor.getMovies().size() > 0) {
            btnToMovieList.setVisibility(View.VISIBLE);
        }

        try {
            picThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Progressbar invisible
        setProgressBarIndeterminateVisibility(false);
    }

    //Defines Visibility of Textviews in Actor Detail
    private void manageEmptyTextfields(TextView tvHc, TextView tv, String text){
        if(!text.equals("") && ( !text.equals("N/A") && !text.equals("0"))){
            tv.setVisibility(View.VISIBLE);
            tvHc.setVisibility(View.VISIBLE);
            colorIt(tvHc);
        }
    }

    //Colors the rows
    private void colorIt(TextView tv){
        LinearLayout p = (LinearLayout) tv.getParent();
        if(rowCount%2==0){
            //darker color - intvalue: 1947832
            p.setBackgroundColor(Color.rgb(206, 238, 237));
        }
        else{
            //brighter color - intvalue: 16764144
            p.setBackgroundColor(Color.rgb(236,248,248));
        }
        rowCount++;
    }

    //private inner class for ayncTask query the data
    private class queryforActorDetail extends AsyncTask<ActorDet, String, ActorDet> {

        @Override
        protected ActorDet doInBackground(ActorDet... actorDets) {


            final SparqlQueries sparqler = new SparqlQueries();
            final ActorDet actor = actorDets[0];
            //LMDB  Query its a extra thread that lmdb and dbpedia query can query at the same time
            Thread tLMDBActorDetail = new Thread(new Runnable() {
                public void run() {
                    //Returns the needed sparql query
                    String LMDBsparqlQueryString = sparqler.LMDBActorDetailQuery(actor);
                    Query query = QueryFactory.create(LMDBsparqlQueryString);
                    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
                    ResultSet results;
                    //reads the data of the result set and adds it to the actor object
                    try {
                        results = qexec.execSelect();
                        for (; results.hasNext(); ) {
                            QuerySolution soln = results.nextSolution();
                            try {
                                if (soln.getLiteral("mN") != null) {
                                    actor.addMovie(soln.getLiteral("mN").getString());
                                }} catch (Exception e){
                                Log.d("actorDetail Problem", e.toString());
                            }
                            try {
                                if (soln.getLiteral("rN") != null) {
                                    actor.addRole(soln.getLiteral("rN").getString());
                                }} catch (Exception e){
                                Log.d("actorDetail Problem", e.toString());
                            }
                        }
                    }
                    catch (Exception e) {
                        Log.e("LINKEDMDBDetail", "Failed " + e.toString());
                    }
                    qexec.close();
                }
            });

            tLMDBActorDetail.start();

            //DBpedia query
            String dbPediaSparqlQueryString = sparqler.DBPEDIAActorDetailQuery(actor);
            Query query = QueryFactory.create(dbPediaSparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    try {
                        if (soln.getLiteral("wA") != null && "".equals(actor.getWikiAbstract())) {
                            actor.setWikiAbstract(soln.getLiteral("wA").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("birthN") != null && "".equals(actor.getBirthName())) {
                            actor.setBirthName(soln.getLiteral("birthN").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("birthD") != null && "".equals(actor.getBirthDate())) {
                            actor.setBirthDate(soln.getLiteral("birthD").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("bPN") != null) {
                            actor.addBirthPlace(soln.getLiteral("bPN").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("natN") != null && "".equals(actor.getNationality())) {
                            actor.setNationality(soln.getLiteral("natN").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("citS") != null && "".equals(actor.getNationality())) {
                            actor.setNationality(soln.getLiteral("citS").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("childC") != null && 0 == actor.getChildren()) {
                            actor.setChildren(soln.getLiteral("childC").getInt());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("yearA") != null && 0 == actor.getActiveYear()) {
                            actor.setActiveYear(soln.getLiteral("yearA").getInt());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("oC") != null && "".equals(actor.getOccupation())) {
                            actor.setOccupation(soln.getLiteral("oC").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.get("picLink") != null && "".equals(actor.getPictureURL())) {
                            actor.setPictureURL(soln.get("picLink").toString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.get("hp") != null && "".equals(actor.getHomepage())) {
                            actor.setHomepage(soln.get("hp").toString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("partnerN") != null && "".equals(actor.getPartner())) {
                            actor.setPartner(soln.getLiteral("partnerN").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("parentN") != null) {
                            actor.addChild(soln.getLiteral("parentN").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                    try {
                        if (soln.getLiteral("mN") != null) {
                            actor.addMovie(soln.getLiteral("mN").getString());
                        }} catch (Exception e){
                        Log.d("actorDetail Problem", e.toString());
                    }
                }
            }catch (Exception e){
                Log.e("DBPEDIADetail", "Failed DBPEDIA DOWN "+ e.toString());
                publishProgress("A problem with DBpedia occurred");
            }
            qexec.close();

            //wait for the lmdb thread
            try {
                tLMDBActorDetail.join();
            } catch (Exception e) {
                Log.e("LINKEDMDBActorDetail", "Failed " + e.toString());
            }

            publishProgress("Sparql loaded. Now loading additional data");

            return actor;
        }

        @Override
        protected void onProgressUpdate (String... values) {
            //Make some messages during loading
            Toast.makeText(that, values[0], Toast.LENGTH_SHORT).show();
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }
        public void onPostExecute(ActorDet actor) {
            actor.geteListener().onFinished(actor);
            //Call the finish method to set the data
        }
    }
}
