package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
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
                wikiAbstract.setText(actorDet.getWikiAbstract());

                TextView birthName = (TextView) findViewById(R.id.tvBirthName);
                birthName.setText(actorDet.getBirthName());

                TextView birthDate = (TextView) findViewById(R.id.tvBirthDate);
                birthDate.setText(actorDet.getBirthDate());

                TextView birthPlace = (TextView) findViewById(R.id.tvBirthPlace);
                birthPlace.setText(actorDet.getBirthPlace());

                TextView partner = (TextView) findViewById(R.id.tvPartner);
                partner.setText(actorDet.getPartner());

                TextView parent = (TextView) findViewById(R.id.tvParent);
                parent.setText(actorDet.getParent());

                TextView children = (TextView) findViewById(R.id.tvChildren);
                children.setText(String.valueOf(actorDet.getChildren()));

                TextView nationality = (TextView) findViewById(R.id.tvNationality);
                nationality.setText(actorDet.getNationality());

                TextView occupation = (TextView) findViewById(R.id.tvOccupation);
                occupation.setText(actorDet.getOccupation());

                TextView activeYear = (TextView) findViewById(R.id.tvActiveYear);
                activeYear.setText(String.valueOf(actorDet.getActiveYear()));

                TextView movies = (TextView) findViewById(R.id.tvMovies);
                movies.setText(String.valueOf(actorDet.createTvOutOfList(actorDet.getMovies())));


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
}
