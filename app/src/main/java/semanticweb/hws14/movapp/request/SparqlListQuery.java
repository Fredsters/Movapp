package semanticweb.hws14.movapp.request;

import android.os.AsyncTask;
import android.widget.ListView;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.activities.List;

/**
 * Created by Frederik on 23.10.2014.
 */
public class SparqlListQuery extends AsyncTask<String, Void, ArrayList<Movie>>{
    //TODO: Input bereinigen
    //TODO: AsyncTask

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        String actorName = params[0];
        ArrayList<Movie> movieList = new ArrayList<Movie>();


        /* LMDB */
        String LMDBsparqlQueryString = LMDBQuery(actorName);
        Query query = QueryFactory.create(LMDBsparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
        ResultSet results = qexec.execSelect();

        for (; results.hasNext(); ) {
            QuerySolution soln = results.nextSolution();
            Literal title = soln.getLiteral("t");
            Literal movieId = soln.getLiteral("i");
            Literal releaseYearLiteral = soln.getLiteral("y");
            String releaseYearString = releaseYearLiteral.toString();
            Pattern p = Pattern.compile("\\d\\d\\d\\d");
            Matcher m = p.matcher(releaseYearString);
            if(m.find()) {
                releaseYearString = m.group();
            }
            int releaseYear = Integer.parseInt(releaseYearString);

            Movie movie = new Movie(title.getString(), movieId.getInt(), releaseYear);
            movieList.add(movie);
        }
        qexec.close();


        /* DPBEDIA */
        String dbPediaSparqlQueryString = DBPEDIAQuery(actorName);
        query = QueryFactory.create(dbPediaSparqlQueryString);
        qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        results = qexec.execSelect();

        for (; results.hasNext(); ) {
            QuerySolution soln = results.nextSolution();
            Literal title = soln.getLiteral("t");
            Movie movie = new Movie(title.getString(),1999);
            movieList.add(movie);
        }
        qexec.close();

        /* Put the Lists together */
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

    protected void onPostExecute(ArrayList<Movie> movieList) {
        List.movieList = movieList;
        List.mlAdapter.notifyDataSetChanged();
    }

//TODO start buffer gif or soemthing
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
//TODO & and and bereinigen
}
