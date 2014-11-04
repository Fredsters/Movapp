package semanticweb.hws14.movapp.request;

import java.util.HashMap;

/**
 * Created by Frederik on 23.10.2014.
 */
public class SparqlQueries {

    HashMap<String, Object> criteria;

    public SparqlQueries(HashMap<String, Object> criteria) {
        this.criteria = criteria;
    }

    public String LMDBQuery() {
        return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
                "SELECT ?t ?i ?y ?p WHERE { " +
                "?a movie:actor_name '"+criteria.get("actorName")+"'. " +
                "?m movie:actor ?a; " +
                "movie:filmid ?i;" +
                "rdfs:label ?t." +
                "OPTIONAL {?m movie:initial_release_date ?y.} "+
                "OPTIONAL { ?m foaf:page ?p." +
                "FILTER (REGEX(STR(?p), 'imdb.com/title'))}" +
                "}";
    }

    public String DBPEDIAQuery() {
        return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX dbpprop: <http://dbpedia.org/property/> "+
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
                "SELECT ?t ?y WHERE { "+
                "?actor rdfs:label '"+criteria.get("actorName")+"'@en. "+
                "?movies dbpprop:starring ?actor; "+
                "foaf:name ?t." +
                "OPTIONAL{?movies dbpprop:released ?y.}}";
    }
}
