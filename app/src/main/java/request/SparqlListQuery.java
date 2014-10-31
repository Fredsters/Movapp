package request;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Movie;

/**
 * Created by Frederik on 23.10.2014.
 */
public class SparqlListQuery {
    //TODO: Input bereinigen
    //TODO: AsyncTask
    public static ArrayList<Movie> runListQuery(String actorName) {//Extend more parameters
        ArrayList<Movie> movieList = new ArrayList<Movie>();
   //     actorName = "jim Carrey";
        String sparqlQueryString =
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
                "SELECT ?t ?i "/*?p*/+" WHERE { " +
                "?a movie:actor_name '"+actorName+"'. " +
                "?m movie:actor ?a; " +
                "movie:filmid ?i;" +
                "rdfs:label ?t. " +
//                "OPTIONAL { ?m foaf:page ?p." +
 //               "FILTER (REGEX(STR(?p), 'imdb.com/title'))}" +
                "}";

        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
        ResultSet results = qexec.execSelect();

        for (; results.hasNext(); ) {
            QuerySolution soln = results.nextSolution();
            Literal title = soln.getLiteral("t");
            Literal movieId = soln.getLiteral("i");

/*            String imdbId="";
            if(soln.contains("p")) {
                Resource page = soln.getResource("p");
                String uri = page.getURI();
                uri.toString();
                Pattern p = Pattern.compile("tt[\\d]+");
                Matcher m = p.matcher(uri);
                if(m.find()) {
                    imdbId = m.group();
                }
            } */
            Movie movie = new Movie(title.getString(), movieId.getInt()/*, imdbId*/);
            movieList.add(movie);
        }
        qexec.close();

        sparqlQueryString =
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
            "PREFIX dbpprop: <http://dbpedia.org/property/> "+
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
            "SELECT ?t WHERE { "+
            "?actor rdfs:label '"+actorName+"'@en. "+
            "?movies dbpprop:starring ?actor; "+
            "foaf:name ?t.}";

        query = QueryFactory.create(sparqlQueryString);
        qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        results = qexec.execSelect();

        for (; results.hasNext(); ) {
            QuerySolution soln = results.nextSolution();
            Literal title = soln.getLiteral("t");

            Movie movie = new Movie(title.getString());
            movieList.add(movie);
        }
        qexec.close();

        for(int i=0; i<movieList.size();i++) {
            for(int j=i+1; j<movieList.size();j++) {
                if(movieList.get(i).getTitle().equals(movieList.get(j).getTitle())) {
                    movieList.remove(movieList.get(j));
                    break;
                }
            }
        }
        return movieList;
    }
}
