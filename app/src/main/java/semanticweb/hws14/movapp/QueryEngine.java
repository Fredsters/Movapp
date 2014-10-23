package semanticweb.hws14.movapp;

import android.os.AsyncTask;
import android.util.Log;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Frederik on 23.10.2014.
 */
public class QueryEngine {
    //TODO: Input bereinigen
    protected static ArrayList<HashMap<String,String>> runListQuery(String actorName) {//Extend more parameters
        ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();
        String sparqlQueryString1 =
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                        "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
                        "SELECT ?m ?t ?d WHERE { " +
                        "?a movie:actor_name '"+actorName+"'. " +
                        "?m movie:actor ?a; " +
                        "rdfs:label ?t; " +
                        "} LIMIT 10 ";

        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
        ResultSet results = qexec.execSelect();



        for (; results.hasNext(); ) {
            QuerySolution soln = results.nextSolution();
          //  RDFNode x = soln.get("m");       // Get a result variable by name.
           // Resource r = soln.getResource("m"); // Get a result variable - must be a resource
            Literal title = soln.getLiteral("t");   // Get a result variable - must be a literal
          //  Log.d("SPARQLRESULT", i++ + " : " + x);
          //  Log.d("SPARQLRESULT", i++ + " : " + r);
           // Log.d("SPARQLRESULT", i++ + " : " + l);
            HashMap<String, String> movieDataMap = new HashMap<String, String>();
            movieDataMap.put("title", title.toString());
            //TODO: NEED NAME, Id and some data only for list view URI ? DBPEDIA USES TITLE OR LABEL OR WHAT?
            result.add(movieDataMap);
        }
        qexec.close();
        return result;
    }
}
