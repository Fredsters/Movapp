package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.fragments.ActorListAdapter;
import semanticweb.hws14.movapp.request.SparqlQueries;

//Contains a list of actors
public class ActorList extends Activity {
    //Statics, so that if the user goes back and again on actor list, it is not neccessary to load the data again
    private static ArrayList<String> staticActorList;
    private static HashMap<String, Object> staticCriteria;
    private ActorListAdapter alAdapter;
    private Activity that = this;
    private HashMap<String, Object> actorCriteria;
    private queryForActors q; //private inner class for asyncTask
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For Progress Bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_actor_list);
        //Remove Back Button
        getActionBar().setDisplayHomeAsUpEnabled(false);

        //Get the list
        Intent intent = getIntent();
        listView = (ListView) findViewById(R.id.actorList);
        ArrayList<String> actorList = new ArrayList<String>();

        //if actorList is already available (coming from movieDetail) we do not need to load it again
        if(intent.hasExtra("actorList")) {
            actorList = intent.getStringArrayListExtra("actorList");
        } else if(intent.hasExtra("criteria")) { //From Actor criteria
            actorCriteria = (HashMap<String, Object>)intent.getSerializableExtra("criteria");
        }

        //set the already available actorlist
        if(intent.hasExtra("actorList")) {
            this.alAdapter = new ActorListAdapter(this,R.layout.listview_item_actor, actorList);
            listView.setAdapter(alAdapter);
        } else if(actorCriteria.equals(staticCriteria)){
            //if the criteria for the query are the same as the last time, we do not need to load again
            this.alAdapter = new ActorListAdapter (this, R.layout.listview_item_actor, actorList);
            listView.setAdapter(alAdapter);
            alAdapter.addAll(staticActorList);
        } else {
            //load the data
            checkCriteria();
            staticCriteria = actorCriteria;
            q = new queryForActors();
            q.execute(actorCriteria);
            this.alAdapter = new ActorListAdapter (this, R.layout.listview_item_actor, actorList);
            listView.setAdapter(alAdapter);
        }

        final ArrayList<String> finalActorList = actorList;

        //ListItem click Listener
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

    //Ends asyncTask when Activity is left while asyncTask is running
    @Override
    protected void onStop () {
        super.onStop();
        if(null != q) {
            q.cancel(true);
        }
    }
    //Set all unset criteria to false, so that it is not neccessary to always set them
    private void checkCriteria() {
        if(!actorCriteria.containsKey("isMovie")) {
            actorCriteria.put("isMovie", false);
        }
        if(!actorCriteria.containsKey("isPartName")) {
            actorCriteria.put("isPartName", false);
        }
        if(!actorCriteria.containsKey("isTime")) {
            actorCriteria.put("isTime", false);
        }
        if(!actorCriteria.containsKey("isState")) {
            actorCriteria.put("isState", false);
        }
        if(!actorCriteria.containsKey("isCity")) {
            actorCriteria.put("isCity", false);
        }
    }
    //private inner class for ayncTask query the data
    private class queryForActors extends AsyncTask<HashMap<String, Object>, String, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(HashMap<String, Object>... criterias) {
            HashMap<String, Object> actorCriteria = criterias[0];
            final SparqlQueries sparqler = new SparqlQueries(actorCriteria);
            final ArrayList<String> actorList = new ArrayList<String>();

            //LMDB  Query its a extra thread that lmdb and dbpedia query can query at the same time
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


            //Only use LMDB when the criteria contains data that lmdb can use
            if(!((Boolean) actorCriteria.get("isTime") || (Boolean) actorCriteria.get("isCity") || (Boolean) actorCriteria.get("isState"))) {
                tLMDB.start();
            }

            //DBpedia query
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

            //Only use LMDB when the criteria contains data that lmdb can use
            if(!((Boolean) actorCriteria.get("isTime") || (Boolean) actorCriteria.get("isCity") || (Boolean) actorCriteria.get("isState"))) {
                try {
                    tLMDB.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        /* Eliminate doublicates */
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
            //when there are actors, show those and if there is only one go directy to actorDetail
            if (actorList.size() > 0) {
                Collections.sort(actorList);
                alAdapter.addAll(actorList);
                that.setProgressBarIndeterminateVisibility(false);
                ActorList.staticActorList = actorList;
                if(actorList.size() == 1) {
                    listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
                }
            } else {
                //if there are no actors show dialog with this message
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
