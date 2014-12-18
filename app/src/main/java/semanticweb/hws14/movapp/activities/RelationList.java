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
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.request.SparqlQueries;

public class RelationList extends Activity {
    //This activity shows a list of the relations of a movie

    private ListView listView;
    private ActorListAdapter rlAdapter;
    private static Movie staticMovie;
    private static ArrayList<String> staticRelationList;
    private queryForRelations q;
    private Activity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_relation_list);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        //Basicly works as the other list activities
        Intent intent = getIntent();
        listView = (ListView) findViewById(R.id.relationList);
        ArrayList<String> relationList = new ArrayList<String>();
        Movie movie = intent.getParcelableExtra("movie");

        if(intent.hasExtra("movie")) {
            if (movie.equals(staticMovie)) {
                this.rlAdapter = new ActorListAdapter(this, R.layout.listview_item_actor, relationList);
                listView.setAdapter(rlAdapter);
                rlAdapter.addAll(staticRelationList);
            } else {
                staticMovie = movie;
                q = new queryForRelations();
                q.execute(movie);
                this.rlAdapter = new ActorListAdapter(this, R.layout.listview_item_actor, relationList);
                listView.setAdapter(rlAdapter);
            }
        }

        final ArrayList<String> finalRelationList = relationList;
        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(that, MovieList.class);
                String relationName = finalRelationList.get(position);
                HashMap<String, Object> criteria = new HashMap<String, Object>();
                criteria.put("isRelated", true);
                criteria.put("relation", relationName);
                intent.putExtra("criteria", criteria);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(clickListen);
    }

    @Override
    protected void onStop () {
        super.onStop();
        if(null != q) {
            q.cancel(true);
        }
    }
    //Inner class for the async task
    private class queryForRelations extends AsyncTask<Movie, String, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Movie... movies) {
            Movie movie = movies[0];

            final SparqlQueries sparqler = new SparqlQueries();
            final ArrayList<String> relationList = new ArrayList<String>();

            //Only dbpedia, because lmdb has nor relations
            /* DPBEDIA */
            String dbPediaSparqlQueryString = sparqler.DBPEDIARelationQuery(movie);
            Query query = QueryFactory.create(dbPediaSparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    String subName = "";
                    try {
                        subName = soln.getLiteral("subN").getString();
                    }catch (Exception e) {
                        Log.d("actorList Problem ", e.toString());
                    }
                    relationList.add(subName);
                }
            } catch (Exception e) {
                Log.e("DBPEDIA", "Failed DBPEDIA DOWN" + e.toString());
                publishProgress("A problem with DBPedia occured");
            }
            qexec.close();
            return relationList;
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onProgressUpdate (String... values) {
            Toast.makeText(that, values[0], Toast.LENGTH_LONG).show();
        }

        public void onPostExecute(ArrayList<String> relationList) {

            if (relationList.size() > 0) {
                Collections.sort(relationList);
                rlAdapter.addAll(relationList);
                that.setProgressBarIndeterminateVisibility(false);
                RelationList.staticRelationList = relationList;
                if(relationList.size() == 1) {
                    listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
                }
            } else {
                AlertDialog ad = new AlertDialog.Builder(that).create();
                ad.setMessage("No Relations found!");
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
