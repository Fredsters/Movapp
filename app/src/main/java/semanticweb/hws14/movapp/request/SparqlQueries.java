package semanticweb.hws14.movapp.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import semanticweb.hws14.movapp.model.ActorDet;
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

    public SparqlQueries(){

    }

    public String LMDBQuery() {
        String queryString =
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
            "SELECT distinct ?t ?i ?y ?p";
            if((Boolean)criteria.get("isGenre") && ((Boolean)criteria.get("isActor") || (Boolean)criteria.get("isDirector"))) {
                queryString += "?gn ";
            }
            queryString +="WHERE { ";
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

            if((Boolean)criteria.get("isGenre") && !(Boolean)criteria.get("isActor") && !(Boolean)criteria.get("isDirector")) {
                queryString +=
                "?g movie:film_genre_name '"+ criteria.get("genreName")+"'. "+
                        //Removed the filter, because it was too slowly
               // "FILTER(regex(?gn, '"+ criteria.get("genreName")+"','i')) " +
                "?m movie:genre ?g. ";
            } else if((Boolean)criteria.get("isGenre")){
                queryString +=
                "OPTIONAL {?g movie:film_genre_name ?gn." +
                "FILTER(regex(?gn, '"+ criteria.get("genreName")+"','i')) " +
                "?m movie:genre ?g.}" ;
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
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
            "PREFIX dbpprop: <http://dbpedia.org/property/> "+
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
            "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "+
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
            "SELECT distinct ?t ?y ";
            if((Boolean)criteria.get("isGenre") && ((Boolean)criteria.get("isActor") || (Boolean)criteria.get("isDirector"))) {
                queryString += "?gn ";
            }
            queryString += "WHERE { ?m rdf:type <http://schema.org/Movie>. ";
            if((Boolean)criteria.get("isActor")) {
                queryString += "?actor rdfs:label '"+criteria.get("actorName")+"'@en. "+
                "?m dbpedia-owl:starring ?actor. ";
            }
            if((Boolean)criteria.get("isDirector")) {
                queryString += "?d rdfs:label '"+criteria.get("directorName")+"'@en. "+
                "?m dbpedia-owl:director ?d. ";
            }
            if((Boolean)criteria.get("isGenre") && !(Boolean)criteria.get("isActor") && !(Boolean)criteria.get("isDirector")) {
                queryString +=
                "?g rdfs:label '"+criteria.get("genreName")+"' "+
                "FILTER(langMatches(lang(?gn), 'EN')) "+
                        //Removed the filter, because it was too slowly
     //           "FILTER(regex(?gn, '"+criteria.get("genreName")+"', 'i')) "+
                "?m dbpprop:genre ?g.";
            } else if((Boolean)criteria.get("isGenre")) {
                queryString +=
                "OPTIONAL { ?g rdfs:label ?gn. "+
                "FILTER(langMatches(lang(?gn), 'EN')) "+
                "FILTER(regex(?gn, '"+criteria.get("genreName")+"', 'i')) "+
                "?m dbpprop:genre ?g. }";
            }
            queryString+= "?m foaf:name ?t.";

            if((Boolean)criteria.get("isTime") && !(Boolean)criteria.get("isActor") && !(Boolean)criteria.get("isDirector")) {
                queryString+=
                "?m dbpprop:released ?y. " +
                "FILTER(?y >= \""+((TimePeriod) criteria.get("timePeriod")).getFrom()+"-01-01\"^^xsd:date && ?y <= \""+((TimePeriod) criteria.get("timePeriod")).getTo()+"-12-31\"^^xsd:date) ";
            } else {
                queryString+=
                "OPTIONAL{?m dbpprop:released ?y.}";
            }
            queryString+=
            "} LIMIT 100";

        return queryString;
    }

    public static boolean filterReleaseDate(Movie movie) {
        int from = ((TimePeriod) criteria.get("timePeriod")).getFrom();
        int to = ((TimePeriod) criteria.get("timePeriod")).getTo();
        if(movie.getReleaseYear() < from || movie.getReleaseYear() > to) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean filterGenre(Movie movie) {
        String genre = movie.getGenre();
        String genreNameFilter = (String) criteria.get("genreName");
        Pattern p = Pattern.compile(genreNameFilter, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(genre);
        if(!m.find()) {
            return true;
        }
        return false;
    }

    public String LMDBDetailQuery(Movie movie) {
        String queryString=
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
                "SELECT ?r ?aN ?dN ?wN ?gN WHERE { ";
                if(movie.getLMDBmovieId() != 0) {
                    queryString+= "?m movie:filmid '"+movie.getLMDBmovieId()+"'^^xsd:int. ";
                } else {
                    queryString+= "?m rdfs:label '"+movie.getTitle()+"'. ";
                }
                queryString+=
                "OPTIONAL {?m movie:actor ?a. "+
                "?a movie:actor_name ?aN.} "+
                "OPTIONAL {?m movie:director ?d. "+
                "?d movie:director_name ?dN.} "+
                "OPTIONAL {?m movie:writer ?w. "+
                "?w movie:writer_name ?wN.} "+
                "OPTIONAL {?m movie:genre ?g. "+
                "?g movie:film_genre_name ?gN.} "+
                "OPTIONAL {?m movie:runtime ?r.}} ";
        return queryString;
    }

    public String DBPEDIADetailQuery(Movie movie) {
        String queryString=
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "+
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
                "select ?abs ?bu where { " +
                "?m rdf:type dbpedia-owl:Film; "+
                "rdfs:label '"+movie.getTitle()+"'@en. "+
                "OPTIONAL {?m dbpedia-owl:abstract ?abs . " +
                "FILTER(langMatches(lang(?abs ), 'EN')) " +
                "} " +
                "OPTIONAL {?m dbpedia-owl:budget ?bu .} " +
                "} ";
        return queryString;
    }

    public String DBPEDIAActorDetailQuery(ActorDet actorDet) {
        String queryString =
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "+
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
                "PREFIX dbpprop: <http://dbpedia.org/property/> "+
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
                "PREFIX yago: <http://dbpedia.org/class/yago/> " +
                "select distinct ?wA ?birthN ?birthD ?birthP ?citS ?natN ?childC ?oC ?picLink ?yearA ?hp ?partnerN ?parentN ?mN " +
                "where {" +
                "{?ac rdfs:label '"+actorDet.getName()+"'@en; " +
                "rdf:type yago:Actor109765278. " +
                "OPTIONAL{?ac dbpedia-owl:abstract ?wA. FILTER(langMatches(lang(?wA), 'EN'))} " +
                "OPTIONAL{?ac dbpedia-owl:birthName ?birthN.} " +
                "OPTIONAL{?ac dbpedia-owl:birthDate ?birthD.} " +
                "OPTIONAL{?ac dbpprop:birthPlace ?birthP.} " +
                "OPTIONAL{?ac dbpprop:citizenship ?citS.} " +
                "OPTIONAL{?ac dbpedia-owl:nationality ?n. ?n rdfs:label ?natN.} " +
                "OPTIONAL{?ac dbpprop:occupation ?oC.} " +
                "OPTIONAL{?ac dbpedia-owl:thumbnail ?picLink.} " +
                "OPTIONAL{?ac dbpprop:children ?childC.} " +
                "OPTIONAL{?ac dbpprop:yearsActive ?yearA.} " +
                "OPTIONAL{?ac foaf:homepage ?hp.} " +
                "OPTIONAL{?partner dbpedia-owl:partner ?ac; rdfs:label ?partnerN. FILTER(langMatches(lang(?partnerN), 'EN'))} " +
                "OPTIONAL{?parent dbpedia-owl:parent ?ac; rdfs:label ?parentN. FILTER(langMatches(lang(?parentN), 'EN'))} " +
                "} UNION " +
                "{?ac rdfs:label '"+actorDet.getName()+"'@en; rdf:type yago:Actor109765278. " +
                "OPTIONAL{?m dbpedia-owl:starring ?ac; rdfs:label ?mN. " +
                "FILTER(langMatches(lang(?mN), 'EN'))} " +
                "} " +
                "} LIMIT 100";
        return queryString;
    }

}
