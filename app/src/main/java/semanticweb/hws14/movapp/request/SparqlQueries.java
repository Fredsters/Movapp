package semanticweb.hws14.movapp.request;

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
            "SELECT distinct ?m ?t ?y ?p ";
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
            if((Boolean)criteria.get("isGenre") && ((Boolean)criteria.get("isActor") || (Boolean)criteria.get("isDirector"))) {
                queryString +=
                "OPTIONAL {?g movie:film_genre_name ?gn. " +
                "?m movie:genre ?g.}" ;
            } else if((Boolean)criteria.get("isGenre")){
                queryString +=
                "?g movie:film_genre_name '"+ criteria.get("genreName")+"'. "+
                "?m movie:genre ?g. ";
            }

        queryString +=
            "?m movie:filmid ?i; " +
            "rdfs:label ?t. " +
            "OPTIONAL {?m movie:initial_release_date ?y.} "+
            "OPTIONAL {?m foaf:page ?p. FILTER (REGEX(STR(?p), 'imdb.com/title'))}" +
            "} LIMIT 200";

        return queryString;
    }

    public String DBPEDIAQuery() {
        String queryString =
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "PREFIX dbpprop: <http://dbpedia.org/property/> " +
                    "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                    "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " +
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                    "PREFIX category: <http://dbpedia.org/resource/Category:> "+
                    "PREFIX dcterms: <http://purl.org/dc/terms/> "+
                    "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
                    "SELECT distinct ?m ?t ?y ";
            if ((Boolean) criteria.get("isGenre") && ((Boolean) criteria.get("isActor") || (Boolean) criteria.get("isDirector"))) {
                queryString += "?gn ";
            }
            queryString += "WHERE { ?m rdf:type <http://schema.org/Movie>. ";
            if ((Boolean) criteria.get("isActor")) {
                queryString += "?actor rdfs:label '" + criteria.get("actorName") + "'@en. " +
                        "?m dbpedia-owl:starring ?actor. ";
            }
            if ((Boolean) criteria.get("isDirector")) {
                queryString += "?d rdfs:label '" + criteria.get("directorName") + "'@en. " +
                        "?m dbpedia-owl:director ?d. ";
            }
            if ((Boolean) criteria.get("isGenre") && ((Boolean) criteria.get("isActor") || (Boolean) criteria.get("isDirector"))) {
                queryString +=
                "OPTIONAL {?m dbpprop:genre ?g. ?g rdfs:label ?gn. }";
            } else if ((Boolean) criteria.get("isGenre")) {
                queryString +=
                "?g rdfs:label '" + criteria.get("genreName") + "'. " +
                "?m dbpprop:genre ?g.";
            }
            if((Boolean) criteria.get("isCity")) {
                queryString +=
                "?m dcterms:subject category:Films_"+criteria.get("regionKind")+"_in_"+criteria.get("city")+". ";
            } else if((Boolean) criteria.get("isState")) {
                queryString += "{?m dcterms:subject category:Films_"+criteria.get("regionKind")+"_in_"+criteria.get("state")+".} " +
                        "UNION {category:Films_"+criteria.get("regionKind")+"_in_"+criteria.get("state")+"_by_state skos:broader ?states. ?m dcterms:subject ?states } " +
                        "UNION {?cities skos:broader  category:Films_"+criteria.get("regionKind")+"_in_"+criteria.get("state")+"_by_city . ?m dcterms:subject ?cities} ";
             }
            if ((Boolean) criteria.get("isTime") && !(Boolean) criteria.get("isActor") && !(Boolean) criteria.get("isDirector")) {
                queryString +=
                        "?m dbpprop:released ?y. " +
                                "FILTER(?y >= \"" + ((TimePeriod) criteria.get("timePeriod")).getFrom() + "-01-01\"^^xsd:date && ?y <= \"" + ((TimePeriod) criteria.get("timePeriod")).getTo() + "-12-31\"^^xsd:date) ";
            } else {
                queryString +=
                        "OPTIONAL{?m dbpprop:released ?y.}";
            }
            queryString += "?m foaf:name ?t.";
            queryString +=
                    "} LIMIT 400";
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
        String movieResource = movie.getLMDBmovieResource();
        String queryString ="";
        if("".equals(movieResource)) {
            queryString =
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
                "SELECT ?r ?aN ?dN ?wN ?gN WHERE { "+
                "?m rdfs:label '"+movie.getTitle()+"'. "+
                "OPTIONAL {?m movie:actor ?a. "+
                "?a movie:actor_name ?aN.} "+
                "OPTIONAL {?m movie:director ?d. "+
                "?d movie:director_name ?dN.} "+
                "OPTIONAL {?m movie:writer ?w. "+
                "?w movie:writer_name ?wN.} "+
                "OPTIONAL {?m movie:genre ?g. "+
                "?g movie:film_genre_name ?gN.} "+
                "OPTIONAL {?m movie:runtime ?r.}} ";
        } else {
            queryString=
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX movie: <http://data.linkedmdb.org/resource/movie/> " +
                "SELECT ?r ?aN ?dN ?wN ?gN WHERE { "+
                "OPTIONAL {"+movieResource+" movie:actor ?a. "+
                "?a movie:actor_name ?aN.} "+
                "OPTIONAL {"+movieResource+" movie:director ?d. "+
                "?d movie:director_name ?dN.} "+
                "OPTIONAL {"+movieResource+" movie:writer ?w. "+
                "?w movie:writer_name ?wN.} "+
                "OPTIONAL {"+movieResource+" movie:genre ?g. "+
                "?g movie:film_genre_name ?gN.} "+
                "OPTIONAL {"+movieResource+" movie:runtime ?r.}} ";
        }

        return queryString;
    }

    public String DBPEDIADetailQuery(Movie movie) {
        String movieResource = movie.getDBPmovieResource();
        String queryString ="";
        if("".equals(movieResource)) {
            queryString =
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "+
                "PREFIX dbpprop: <http://dbpedia.org/property/> "+
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
                "select ?abs ?bu ?r ?aN ?dN ?wN where { " +
                "?m rdf:type <http://schema.org/Movie>; "+
                "foaf:name '"+movie.getTitle()+"'@en. "+
                "OPTIONAL {?m dbpedia-owl:abstract ?abs . FILTER(langMatches(lang(?abs ), 'EN'))} " +
                "OPTIONAL {?m dbpedia-owl:budget ?bu .} " +
                "OPTIONAL {?m dbpprop:runtime ?r.} "+
                "OPTIONAL {?m dbpedia-owl:starring ?a. ?a rdfs:label ?aN. FILTER(langMatches(lang(?aN ), 'EN'))} "+
                "OPTIONAL {?m dbpedia-owl:director ?d. ?d rdfs:label ?dN. FILTER(langMatches(lang(?dN), 'EN'))} "+
                "OPTIONAL {?m dbpedia-owl:writer ?w. ?w rdfs:label ?wN. FILTER(langMatches(lang(?wN), 'EN'))} "+
                "OPTIONAL {?m dbpprop:genre ?g. ?g rdfs:label ?gN. FILTER(langMatches(lang(?gN), 'EN'))} "+
                "} ";
        } else {
            queryString=
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
                "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "+
                "PREFIX dbpprop: <http://dbpedia.org/property/> "+
                "select ?abs ?bu ?r ?aN ?dN ?wN where { " +
                "OPTIONAL {"+movieResource+" dbpedia-owl:abstract ?abs . FILTER(langMatches(lang(?abs ), 'EN'))} " +
                "OPTIONAL {"+movieResource+" dbpedia-owl:budget ?bu .} " +
                "OPTIONAL {"+movieResource+" dbpprop:runtime ?r.} "+
                "OPTIONAL {"+movieResource+" dbpedia-owl:starring ?a. ?a rdfs:label ?aN. FILTER(langMatches(lang(?aN ), 'EN'))} "+
                "OPTIONAL {"+movieResource+" dbpedia-owl:director ?d. ?d rdfs:label ?dN. FILTER(langMatches(lang(?dN), 'EN'))} "+
                "OPTIONAL {"+movieResource+" dbpedia-owl:writer ?w. ?w rdfs:label ?wN. FILTER(langMatches(lang(?wN), 'EN'))} "+
                "OPTIONAL {"+movieResource+" dbpprop:genre ?g. ?g rdfs:label ?gN. FILTER(langMatches(lang(?gN), 'EN'))} "+
                "} ";
        }

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
                "}";
        return queryString;
    }

}
