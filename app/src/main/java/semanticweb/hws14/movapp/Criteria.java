package semanticweb.hws14.movapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;


public class Criteria extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criteria);

        Button btnProfile = (Button) findViewById(R.id.btnProfile);
        Button btnSettings = (Button) findViewById(R.id.btnSettings);
        Button btnPrivacy = (Button) findViewById(R.id.btnPrivacy);

        View panelProfile = findViewById(R.id.panelProfile);
        panelProfile.setVisibility(View.GONE);

        View panelSettings = findViewById(R.id.panelSettings);
        panelSettings.setVisibility(View.GONE);

        View panelPrivacy = findViewById(R.id.panelPrivacy);
        panelPrivacy.setVisibility(View.GONE);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.VISIBLE);

                View panelSettings = findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.GONE);

                View panelPrivacy = findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.GONE);

            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.GONE);

                View panelSettings = findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.VISIBLE);

                View panelPrivacy = findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.GONE);

            }
        });

        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.GONE);

                View panelSettings = findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.GONE);

                View panelPrivacy = findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.VISIBLE);

            }
        });

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.criteria, menu);
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


    public void submitSearch(View view) {
        Intent intent = new Intent(this, list.class);
        EditText editText = (EditText) findViewById(R.id.actor_name);
        String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
//dasge
        //WORKAROUND TODO: PUT in assync method
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String sparqlQueryString1=
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
                        "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> "+
                        "SELECT ?m ?t ?d WHERE { "+
                        "?a movie:actor_name 'Edward Norton'. " +
                        "?m movie:actor ?a; "+
                        "rdfs:label ?t; "+
                        "} LIMIT 10 ";

        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
        ResultSet results = qexec.execSelect();

        int i = 1;
        for ( ; results.hasNext() ; )
        {
            QuerySolution soln = results.nextSolution() ;
            RDFNode x = soln.get("m") ;       // Get a result variable by name.
            Resource r = soln.getResource("m") ; // Get a result variable - must be a resource
            Literal l = soln.getLiteral("t") ;   // Get a result variable - must be a literal
            Log.d("SPARQLRESULT", i++ + " : " + x);
            Log.d("SPARQLRESULT",i++ + " : " + r);
            Log.d("SPARQLRESULT",i++ + " : " + l);

        }
        qexec.close() ;
        startActivity(intent);
    }
}
