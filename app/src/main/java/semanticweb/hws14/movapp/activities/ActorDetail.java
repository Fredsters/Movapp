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
import java.net.URL;
import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.ActorDet;
import semanticweb.hws14.movapp.model.EventActorListener;
import semanticweb.hws14.movapp.request.HttpRequestQueueSingleton;
import semanticweb.hws14.movapp.request.SparqlQueries;



public class ActorDetail extends Activity {
    private Activity that = this;
    private ActorDet actorDet;
    private int rowCount=0; //needed to color
    private queryforActorDetail q;
    Button btnToHomepage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_actor_detail);

        getActionBar().setDisplayHomeAsUpEnabled(false);
        Intent intent = getIntent();
        String actorName = intent.getStringExtra("actorName");

        final ActorDet actor = new ActorDet();
        actor.setName(actorName);

        btnToHomepage = (Button) findViewById(R.id.btnToHompage);

        q = new queryforActorDetail();
        q.execute(actor);

        actor.setOnFinishedEventListener(new EventActorListener() {
            @Override
            public void onFinished(ActorDet actor) {
                actorDet = actor;
                setData(actor);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actor_detail, menu);
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

    @Override
    protected void onStop () {
        super.onStop();
        if(null != q) {
            q.cancel(true);
        }
    }

    public void toMovieList(View view){
        Intent intent = new Intent(that, MovieList.class);

        HashMap<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("isActor", true);
        criteria.put("actorName", actorDet.getName());
        criteria.put("isGenre", false);
        criteria.put("isTime", false);
        criteria.put("isDirector", false);
        criteria.put("isCity", false);
        criteria.put("isState", false);
        intent.putExtra("criteria", criteria);
        startActivity(intent);
    }

    public void toActorHomepage(View view) {
        String homepage = actorDet.getHomepage();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
        startActivity(browserIntent);
    }

    private void setData(final ActorDet actor) {
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

                }
            }
        });

        picThread.start();

        TextView actorDetailName = (TextView) findViewById(R.id.tvActorDetailName);
        actorDetailName.setText(actor.getName());
        actorDetailName.setVisibility(View.VISIBLE);

        TextView wikiAbstract = (TextView) findViewById(R.id.tvWikiAbstract);
        String wikiAbstractText = actor.getWikiAbstract();

        if(wikiAbstractText.equals("") || wikiAbstractText.equals("N/A")){
            String sorry = "Sorry...There is no detailed description available for this actor.";
            wikiAbstract.setText(sorry);
        } else {
            wikiAbstract.setText(wikiAbstractText);
        }
        wikiAbstract.setVisibility(View.VISIBLE);
        //HC = "Hard-Coded" textviews are the corresponding labes

        TextView birthName = (TextView) findViewById(R.id.tvBirthName);
        TextView birthNameHc = (TextView) findViewById(R.id.tvBirthNameHC);
        String birthNameText = actor.getBirthName();
        birthName.setText(birthNameText);
        manageEmptyTextfields(birthNameHc,birthName,birthNameText);

        TextView birthDate = (TextView) findViewById(R.id.tvBirthDate);
        TextView birthDateHc = (TextView) findViewById(R.id.tvBirthDateHC);
        String birthDateText =actor.getBirthDate();
        if(birthDateText.contains("+")){
            // birthDateText=birthDateText.replace(birthDateText.substring('+'),"");
            birthDateText= birthDateText.substring(0, birthDateText.indexOf("+"));
        }
        birthDate.setText(birthDateText);
        manageEmptyTextfields(birthDateHc,birthDate,birthDateText);

        TextView birthPlace = (TextView) findViewById(R.id.tvBirthPlace);
        TextView birthPlaceHc = (TextView) findViewById(R.id.tvBirthPlaceHC);
        String birthPlaceText = String.valueOf(actor.createTvOutOfList(actor.getBirthPlace()));
        birthPlace.setText(birthPlaceText);
        manageEmptyTextfields(birthPlaceHc,birthPlace,birthPlaceText);

        TextView partner = (TextView) findViewById(R.id.tvPartner);
        TextView partnerHc = (TextView) findViewById(R.id.tvPartnerHC);
        String partnerText= actor.getPartner();
        partner.setText(partnerText);
        manageEmptyTextfields(partnerHc,partner,partnerText);

        TextView parent = (TextView) findViewById(R.id.tvParent);
        TextView parentHc = (TextView) findViewById(R.id.tvParentHC);
        String parentText = String.valueOf(actor.createTvOutOfList(actor.getParent()));
        parent.setText(parentText);
        manageEmptyTextfields(parentHc,parent,parentText);

        TextView children = (TextView) findViewById(R.id.tvChildren);
        TextView childrenHc = (TextView) findViewById(R.id.tvChildrenHC);
        String childrenText = String.valueOf(actor.getChildren());
        children.setText(childrenText);
        manageEmptyTextfields(childrenHc,children,childrenText);

        TextView nationality = (TextView) findViewById(R.id.tvNationality);
        TextView nationalityHc = (TextView) findViewById(R.id.tvNationalityHC);
        String nationalityText = actor.getNationality();
        nationality.setText(nationalityText);
        manageEmptyTextfields(nationalityHc,nationality,nationalityText);

        TextView occupation = (TextView) findViewById(R.id.tvOccupation);
        TextView occupationHc = (TextView) findViewById(R.id.tvOccupationHC);
        String occupationText = actor.getOccupation();
        occupation.setText(occupationText);
        manageEmptyTextfields(occupationHc,occupation,occupationText);

        TextView activeYear = (TextView) findViewById(R.id.tvActiveYear);
        TextView activeYearHc = (TextView) findViewById(R.id.tvActiveYearHC);
        String activeYearText = String.valueOf(actor.getActiveYear());
        activeYear.setText(activeYearText);
        manageEmptyTextfields(activeYearHc,activeYear,activeYearText);

        TextView roles = (TextView) findViewById(R.id.tvRoles);
        TextView rolesHc = (TextView) findViewById(R.id.tvRolesHC);
        String rolesText = String.valueOf(actor.createTvOutOfList(actor.getRoles()));
        roles.setText(rolesText);
        manageEmptyTextfields(rolesHc,roles,rolesText);

        TextView movies = (TextView) findViewById(R.id.tvMovies);
        TextView moviesHc = (TextView) findViewById(R.id.tvMoviesHC);
        String moviesText = String.valueOf(actor.createTvOutOfList(actor.getMovies()));
        movies.setText(moviesText);
        manageEmptyTextfields(moviesHc,movies,moviesText);

        if(!"".equals(actor.getHomepage())) {
            btnToHomepage.setVisibility(View.VISIBLE);
        }

        try {
            picThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setProgressBarIndeterminateVisibility(false);
    }

    private class queryforActorDetail extends AsyncTask<ActorDet, String, ActorDet> {

        @Override
        protected ActorDet doInBackground(ActorDet... actorDets) {

            final SparqlQueries sparqler = new SparqlQueries();
            final ActorDet actor = actorDets[0];

            /* LMDB */
            Thread tLMDBActorDetail = new Thread(new Runnable() {
                public void run() {
                    String LMDBsparqlQueryString = sparqler.LMDBActorDetailQuery(actor);
                    Query query = QueryFactory.create(LMDBsparqlQueryString);
                    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
                    ResultSet results;
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


        /* DPBEDIA */

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
            Toast.makeText(that, values[0], Toast.LENGTH_SHORT).show();
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }
        public void onPostExecute(ActorDet actor) {
            actor.geteListener().onFinished(actor);
         //   setProgressBarIndeterminateVisibility(false);
        }
    }

    //Defines Visibility of Textviews in Actor Detail
    public void manageEmptyTextfields(TextView tvHc, TextView tv, String text){
        if(!text.equals("") && ( !text.equals("N/A") && !text.equals("0"))){
            tv.setVisibility(View.VISIBLE);
            tvHc.setVisibility(View.VISIBLE);
            colorIt(tvHc);
        }
    }

    public void colorIt(TextView tv){
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
}
