package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;


import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;

public class ActorList extends Activity {

    private static ArrayList<String> staticActorList;
    private static HashMap<String, Object> staticCriteria;
    private ArrayAdapter<String> alAdapter;
    private Activity that = this;
    private HashMap<String, Object> actorCriteria;
    private queryForActors q;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_actor_list);


        Intent intent = getIntent();
        listView = (ListView) findViewById(R.id.actorList);
        ArrayList<String> actorList = new ArrayList<String>();
        String city = "";
        if(intent.hasExtra("actorList")) {
            actorList = intent.getStringArrayListExtra("actorList");
        } else if(intent.hasExtra("criteria")) {
            actorCriteria = (HashMap<String, Object>)intent.getSerializableExtra("criteria");
        }

        if(intent.hasExtra("actorList")) {
            this.alAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, actorList);
            listView.setAdapter(alAdapter);
        } else if(city.equals(staticCriteria)){
            this.alAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, actorList);
            listView.setAdapter(alAdapter);
            alAdapter.addAll(staticActorList);
        } else {
            staticCriteria = actorCriteria;
            q = new queryForActors();
            q.execute(actorCriteria);
            this.alAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, actorList);
            listView.setAdapter(alAdapter);
        }

        final ArrayList<String> finalActorList = actorList;

        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(that, ActorDetail.class);
                String actorName = finalActorList.get(position);
                intent.putExtra("actorName", actorName);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(clickListen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actor_list, menu);
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


    private class queryForActors extends AsyncTask<HashMap<String, Object>, String, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(HashMap<String, Object>... criterias) {
            HashMap<String, Object> actorCriteria = criterias[0];
            final SparqlQueries sparqler = new SparqlQueries(actorCriteria);

            final ArrayList<String> actorList = new ArrayList<String>();


            /* LMDB */
            Thread tLMDB = new Thread(new Runnable()
            {
                public void run()
                {
                    String LMDBsparqlQueryString = sparqler.LMDBActorQuery();
                    Query query = QueryFactory.create(LMDBsparqlQueryString);
                    QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
                    ResultSet results;
                    try {
                        results = qexec.execSelect();
                        for (; results.hasNext(); ) {
                            QuerySolution soln = results.nextSolution();
                            String actorName = "";
                            try {
                            actorName = soln.getLiteral("aN").getString();
                            }catch (Exception e) {
                                Log.d("actorList Problem ", e.toString());
                            }
                            actorList.add(actorName);
                        }
                    }catch (Exception e){
                        Log.e("LINKEDMDB", "Failed"+ e.toString());
                        publishProgress("A problem with LinkedMDB occured");
                    }
                    qexec.close();
                }
            });


            if(!((Boolean) actorCriteria.get("isTime") || (Boolean) actorCriteria.get("isCity") || (Boolean) actorCriteria.get("isState"))) {
                tLMDB.start();
            }

        /* DPBEDIA */

            String dbPediaSparqlQueryString = sparqler.DBPEDIAActorQuery();
            Query query = QueryFactory.create(dbPediaSparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    String actorName = "";
                    try {
                    actorName = soln.getLiteral("aN").getString();
                    }catch (Exception e) {
                        Log.d("actorList Problem ", e.toString());
                    }
                    actorList.add(actorName);
                }
            } catch (Exception e) {
                Log.e("DBPEDIA", "Failed DBPEDIA DOWN" + e.toString());
                publishProgress("A problem with DBPedia occured");
            }
            qexec.close();


            if(!((Boolean) actorCriteria.get("isTime") || (Boolean) actorCriteria.get("isCity") || (Boolean) actorCriteria.get("isState"))) {
                try {
                    tLMDB.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        /* Eliminate doublicates */

 //           ArrayList indexArray = new ArrayList();
            for (int i = 0; i < actorList.size(); i++) {
                for (int j = i + 1; j < actorList.size(); j++) {
                    if (actorList.get(i).equals(actorList.get(j))) {
                        actorList.remove(actorList.get(j));
                    }
                }
            }

            return actorList;
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onProgressUpdate (String... values) {
            Toast.makeText(that, values[0], Toast.LENGTH_LONG).show();
        }

        public void onPostExecute(ArrayList<String> actorList) {

            if (actorList.size() > 0) {
                Collections.sort(actorList);
                alAdapter.addAll(actorList);
                that.setProgressBarIndeterminateVisibility(false);
                ActorList.staticActorList = actorList;
                if(actorList.size() == 1) {
                    listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
                }
            } else {
                AlertDialog ad = new AlertDialog.Builder(that).create();
                ad.setMessage("No actors found!");
                ad.setCancelable(false); // This blocks the 'BACK' button
                ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        that.finish();
                    }
                });
                ad.show();
                setProgressBarIndeterminateVisibility(false);
            }
        }

    }
}
