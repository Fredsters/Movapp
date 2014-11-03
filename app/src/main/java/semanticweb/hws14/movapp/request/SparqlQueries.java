package semanticweb.hws14.movapp.request;

/**
 * Created by Frederik on 23.10.2014.
 */
public class SparqlQueries {

    public static String LMDBQuery(String actorName) {
        return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
                "SELECT ?t ?i ?y WHERE { " +
                "?a movie:actor_name '"+actorName+"'. " +
                "?m movie:actor ?a; " +
                "movie:filmid ?i;" +
                "rdfs:label ?t;" +
                "movie:initial_release_date ?y." +
                "}";
    }

    public static String DBPEDIAQuery(String actorName) {
        return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX dbpprop: <http://dbpedia.org/property/> "+
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
                "SELECT ?t WHERE { "+
                "?actor rdfs:label '"+actorName+"'@en. "+
                "?movies dbpprop:starring ?actor; "+
                "foaf:name ?t.}";
    }
}
