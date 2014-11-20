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



public class ActorDetail extends Activity {
    private Activity that = this;
    private ActorDet actorDet;
    int rowCount=0; //needed to color

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_actor_detail);


        Intent intent = getIntent();
        String actorName = intent.getStringExtra("actorName");

        final ActorDet actor = new ActorDet();
        actor.setName(actorName);

        queryforActorDetail q = new queryforActorDetail();
        q.execute(actor);

        actor.setOnFinishedEventListener(new EventActorListener() {
            @Override
            public void onFinished(ActorDet actor) {
                actorDet = actor;

          //      WebView actorPic = (WebView) findViewById(R.id.webViewActor);
           //     actorPic.loadUrl(actorDet.getPictureURL());



                Thread picThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            ImageView img = (ImageView) findViewById(R.id.imageViewActor);
                            URL url = new URL(actorDet.getPictureURL());
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
                actorDetailName.setText(actorDet.getName());

                TextView wikiAbstract = (TextView) findViewById(R.id.tvWikiAbstract);
                String wikiAbstractText = actorDet.getWikiAbstract();

                if(wikiAbstractText.equals("")|| wikiAbstractText.equals("N/A")){
                    String sorry = "Sorry...There is no detailed description available for this actor.";
                    wikiAbstract.setText(sorry);
                }
                else{

                    wikiAbstract.setVisibility(View.VISIBLE);

                }


                //HC = "Hard-Coded" textviews are the corresponding labes

                TextView birthName = (TextView) findViewById(R.id.tvBirthName);
                TextView birthNameHc = (TextView) findViewById(R.id.tvBirthNameHC);
                String birthNameText = actorDet.getBirthName();
                birthName.setText(birthNameText);

                manageEmptyTextfields(birthNameHc,birthName,birthNameText);


                TextView birthDate = (TextView) findViewById(R.id.tvBirthDate);
                TextView birthDateHc = (TextView) findViewById(R.id.tvBirthDateHC);
                String birthDateText =actorDet.getBirthDate();

                if(birthDateText.contains("+")){
                  // birthDateText=birthDateText.replace(birthDateText.substring('+'),"");
                  birthDateText= birthDateText.substring(0, birthDateText.indexOf("+"));
                }
                birthDate.setText(birthDateText);

                manageEmptyTextfields(birthDateHc,birthDate,birthDateText);

                TextView birthPlace = (TextView) findViewById(R.id.tvBirthPlace);
                TextView birthPlaceHc = (TextView) findViewById(R.id.tvBirthPlaceHC);
                String birthPlaceText = actorDet.getBirthPlace();
                birthPlace.setText(birthPlaceText);

                manageEmptyTextfields(birthPlaceHc,birthPlace,birthPlaceText);

                TextView partner = (TextView) findViewById(R.id.tvPartner);
                TextView partnerHc = (TextView) findViewById(R.id.tvPartnerHC);
                String partnerText= actorDet.getPartner();
                partner.setText(partnerText);

                manageEmptyTextfields(partnerHc,partner,partnerText);

                TextView parent = (TextView) findViewById(R.id.tvParent);
                TextView parentHc = (TextView) findViewById(R.id.tvParentHC);
                String parentText = actorDet.getParent();
                parent.setText(parentText);

                manageEmptyTextfields(parentHc,parent,parentText);

                TextView children = (TextView) findViewById(R.id.tvChildren);
                TextView childrenHc = (TextView) findViewById(R.id.tvChildrenHC);
                String childrenText = String.valueOf(actorDet.getChildren());
                children.setText(childrenText);

                manageEmptyTextfields(childrenHc,children,childrenText);

                TextView nationality = (TextView) findViewById(R.id.tvNationality);
                TextView nationalityHc = (TextView) findViewById(R.id.tvNationalityHC);
                String nationalityText = actorDet.getNationality();
                nationality.setText(nationalityText);

                manageEmptyTextfields(nationalityHc,nationality,nationalityText);

                TextView occupation = (TextView) findViewById(R.id.tvOccupation);
                TextView occupationHc = (TextView) findViewById(R.id.tvOccupationHC);
                String occupationText = actorDet.getOccupation();
                occupation.setText(occupationText);

                manageEmptyTextfields(occupationHc,occupation,occupationText);

                TextView activeYear = (TextView) findViewById(R.id.tvActiveYear);
                TextView activeYearHc = (TextView) findViewById(R.id.tvActiveYearHC);
                String activeYearText = String.valueOf(actorDet.getActiveYear());
                activeYear.setText(activeYearText);

                manageEmptyTextfields(activeYearHc,activeYear,activeYearText);

                TextView movies = (TextView) findViewById(R.id.tvMovies);
                TextView moviesHc = (TextView) findViewById(R.id.tvMoviesHC);
                String moviesText = String.valueOf(actorDet.createTvOutOfList(actorDet.getMovies()));
                movies.setText(moviesText);

                manageEmptyTextfields(moviesHc,movies,moviesText);

                try {
                    picThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    public void toMovieList(View view){
        Intent intent = new Intent(that, MovieList.class);

        HashMap<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("isActor", true);
        criteria.put("actorName", actorDet.getName());
        criteria.put("isGenre", false);
        criteria.put("isTime", false);
        criteria.put("isDirector", false);
        intent.putExtra("criteria", criteria);
        startActivity(intent);
    }

    public void toActorHomepage(View view) {
        String homepage = actorDet.getHomepage();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
        startActivity(browserIntent);
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

    private class queryforActorDetail extends AsyncTask<ActorDet, String, ActorDet> {

        @Override
        protected ActorDet doInBackground(ActorDet... actorDets) {

            final SparqlQueries sparqler = new SparqlQueries();
            final ActorDet actorDet = actorDets[0];

        /* DPBEDIA */

            String dbPediaSparqlQueryString = sparqler.DBPEDIAActorDetailQuery(actorDet);
            Query query = QueryFactory.create(dbPediaSparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    if (soln.getLiteral("wA") != null && "".equals(actorDet.getWikiAbstract())) {
                        actorDet.setWikiAbstract(soln.getLiteral("wA").getString());
                    }
                    if (soln.getLiteral("birthN") != null && "".equals(actorDet.getBirthName())) {
                        actorDet.setBirthName(soln.getLiteral("birthN").getString());
                    }
                    if (soln.getLiteral("birthD") != null && "".equals(actorDet.getBirthDate())) {
                        actorDet.setBirthDate(soln.getLiteral("birthD").getString());
                    }
                    if (soln.getLiteral("birthP") != null && "".equals(actorDet.getBirthPlace())) {
                        actorDet.setBirthPlace(soln.getLiteral("birthP").getString());
                    }
                    if (soln.getLiteral("natN") != null && "".equals(actorDet.getNationality())) {
                        actorDet.setNationality(soln.getLiteral("natN").getString());
                    }
                    if (soln.getLiteral("citS") != null && "".equals(actorDet.getNationality())) {
                        actorDet.setNationality(soln.getLiteral("citS").getString());
                    }
                    if (soln.getLiteral("childC") != null && 0 == actorDet.getChildren()) {
                        actorDet.setChildren(soln.getLiteral("childC").getInt());
                    }
                    if (soln.getLiteral("yearA") != null && 0 == actorDet.getActiveYear()) {
                        actorDet.setActiveYear(soln.getLiteral("yearA").getInt());
                    }
                    if (soln.getLiteral("oC") != null && "".equals(actorDet.getOccupation())) {
                        actorDet.setOccupation(soln.getLiteral("oC").getString());
                    }
                    if (soln.get("picLink") != null && "".equals(actorDet.getPictureURL())) {
                        actorDet.setPictureURL(soln.get("picLink").toString());
                    }
                    if (soln.get("hp") != null && "".equals(actorDet.getHomepage())) {
                        actorDet.setHomepage(soln.get("hp").toString());
                    }
                    if (soln.getLiteral("partnerN") != null && "".equals(actorDet.getPartner())) {
                        actorDet.setPartner(soln.getLiteral("partnerN").getString());
                    }
                    if (soln.getLiteral("parentN") != null && "".equals(actorDet.getParent())) {
                        actorDet.setParent(soln.getLiteral("parentN").getString());
                    }
                    if (soln.getLiteral("mN") != null) {
                        actorDet.addMovie(soln.getLiteral("mN").getString());
                    }
                }
            }catch (Exception e){
                Log.e("DBPEDIADetail", "Failed DBPEDIA DOWN "+ e.toString());
                publishProgress("A problem with DBPedia occured");
            }
            qexec.close();

            return actorDet;
        }

        @Override
        protected void onProgressUpdate (String... values) {
            Toast.makeText(that, values[0], Toast.LENGTH_LONG).show();
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }
        public void onPostExecute(ActorDet actorDet) {
            actorDet.geteListener().onFinished(actorDet);
         //   setProgressBarIndeterminateVisibility(false);
        }


    }

    //Defines Visibility of Textviews in Actor Detail
    public void manageEmptyTextfields(TextView tvHc, TextView tv, String text){
        if(text.equals("")|| ( text.equals("N/A") || text.equals("0"))){
            tv.setVisibility(View.GONE);
            tvHc.setVisibility(View.GONE);
        }
        else{
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
