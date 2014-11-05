package semanticweb.hws14.movapp.request;

import java.util.ArrayList;
import java.util.HashMap;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.model.TimePeriod;

/**
 * Created by Frederik on 23.10.2014.
 */
public class SparqlQueries {

    static HashMap<String, Object> criteria;

    public SparqlQueries(HashMap<String, Object> criteria) {
        this.criteria = criteria;
    }

    public String LMDBQuery() {
        String queryString =
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
            "SELECT distinct ?t ?i ?y ?p ?gn WHERE { ";
            if((Boolean)criteria.get("isActor")) {
                queryString += "?a movie:actor_name '"+criteria.get("actorName")+"'. "+
                "?m movie:actor ?a. ";
            }
            if((Boolean)criteria.get("isDirector")) {
                queryString += "?d movie:director_name '"+criteria.get("directorName")+"'. "+
                "?m movie:director ?d. ";
            }
            queryString +=
            "?m movie:filmid ?i; " +
            "rdfs:label ?t. ";

            if((Boolean)criteria.get("isGenre")) {
                queryString +=
                "?g movie:film_genre_name ?gn. "+
                "FILTER(regex(?gn, '"+ criteria.get("genreName")+"','i')) " +
                "?m movie:genre ?g. ";
            }

        queryString +=
            "OPTIONAL {?m movie:initial_release_date ?y.} "+
            "OPTIONAL { ?m foaf:page ?p." +
            "FILTER (REGEX(STR(?p), 'imdb.com/title'))}" +
            "} LIMIT 100";

        return queryString;
    }

    public String DBPEDIAQuery() {
        String queryString =
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
            "PREFIX dbpprop: <http://dbpedia.org/property/> "+
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
            "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "+
            "SELECT distinct ?t ?y ?gn WHERE { ";
            if((Boolean)criteria.get("isActor")) {
                queryString += "?actor rdfs:label '"+criteria.get("actorName")+"'@en. "+
                "?m dbpprop:starring ?actor. ";
            }
            if((Boolean)criteria.get("isDirector")) {
                queryString += "?d rdfs:label '"+criteria.get("directorName")+"'@en. "+
                "?m dbpedia-owl:director ?d. ";
            }
            if((Boolean)criteria.get("isGenre")) {
                queryString +=
                "?g rdfs:label ?gn. "+
                "FILTER(langMatches(lang(?gn), 'EN')) "+
                "FILTER(regex(?gn, '"+criteria.get("genreName")+"', 'i')) "+
                "?m dbpprop:genre ?g.";
            }

            queryString+= "?m foaf:name ?t." +
            "OPTIONAL{?m dbpprop:released ?y.";

            if((Boolean)criteria.get("isTime")) {
                queryString +="FILTER(?y >= \""+((TimePeriod) criteria.get("timePeriod")).getFrom()+"-01-01\"^^xsd:date && ?y <= \""+((TimePeriod) criteria.get("timePeriod")).getTo()+"-12-31\"^^xsd:date)";
            }
            queryString += "}" +
            "} LIMIT 100";

        return queryString;
    }

    public static ArrayList<Movie> filterReleaseDate(ArrayList<Movie> movieList) {
        int from = ((TimePeriod) criteria.get("timePeriod")).getFrom();
        int to = ((TimePeriod) criteria.get("timePeriod")).getTo();
        for(Movie movie : movieList) {
            if(movie.getReleaseYear() < from || movie.getReleaseYear() > to) {
                movieList.remove(movie);
            }
        }
        return movieList;
    }

    public static ArrayList<Movie> filterReleaseDate(ArrayList<Movie> movieList, Movie movie) {
        int from = ((TimePeriod) criteria.get("timePeriod")).getFrom();
        int to = ((TimePeriod) criteria.get("timePeriod")).getTo();
        if(movie.getReleaseYear() < from || movie.getReleaseYear() > to) {
            movieList.remove(movie);
        }
        return movieList;
    }
}
