package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.request.HttpRequester;
import semanticweb.hws14.movapp.request.SparqlQueries;


public class List extends Activity {

    protected ArrayAdapter<Movie> mlAdapter;
    private Activity that = this;
    HashMap<String, Object> criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //supportRequestWindowFeature

        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        criteria = (HashMap<String, Object>)intent.getSerializableExtra("criteria");

        final ArrayList<Movie> movieList = new ArrayList<Movie>();
        this.mlAdapter = new ArrayAdapter<Movie>(this,android.R.layout.simple_list_item_1, movieList);

        ListView listView = (ListView) findViewById(R.id.resultList);
        listView.setAdapter(mlAdapter);


        queryForMovies q = new queryForMovies();
        //Executes SPARQL Queries, Private class queryForMovies is called.
        q.execute(criteria);

        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(that, Detail.class);
                Movie movie = movieList.get(position);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(clickListen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
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


    private class queryForMovies extends AsyncTask<HashMap<String, Object>, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(HashMap<String, Object>... criteria) {

            ArrayList<Movie> movieList = new ArrayList<Movie>();

            SparqlQueries sparqler = new SparqlQueries(criteria[0]);

        /* LMDB */
            String LMDBsparqlQueryString = sparqler.LMDBQuery();
            Query query = QueryFactory.create(LMDBsparqlQueryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedmdb.org/sparql", query);
            ResultSet results;
            try {
                results = qexec.execSelect();

                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();


                    String title = InputCleaner.cleanMovieTitle(soln.getLiteral("t"));
                    Literal movieId = soln.getLiteral("i");
                    Literal releaseYearLiteral = soln.getLiteral("y");
                    String imdbId = InputCleaner.cleanImdbId(soln.getResource("p"));
                    String genreName = InputCleaner.cleanGenreName(soln.getLiteral("gn"));

                    Movie movie = new Movie(title, movieId.getInt(), InputCleaner.cleanReleaseYear(releaseYearLiteral),imdbId, genreName);
                    movieList.add(movie);
                }
            }catch (Exception e){
            Log.e("LINKEDMDB", "Failed"+ e.toString());
        }
            qexec.close();


        /* DPBEDIA */

            String dbPediaSparqlQueryString = sparqler.DBPEDIAQuery();
            query = QueryFactory.create(dbPediaSparqlQueryString);
            qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
            try {
                results = qexec.execSelect();
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    String title = InputCleaner.cleanMovieTitle(soln.getLiteral("t"));
                    Literal releaseYearLiteral = soln.getLiteral("y");
                    String genreName = InputCleaner.cleanGenreName(soln.getLiteral("gn"));

                    Movie movie = new Movie(title, InputCleaner.cleanReleaseYear(releaseYearLiteral), genreName);
                    movieList.add(movie);
                }
            }catch (Exception e){
                Log.e("DBPEDIA", "Failed DBPEDIA DOWN"+ e.toString());
            }
            qexec.close();

        /* Put the Lists together */

            ArrayList indexArray = new ArrayList();
            for(int i=0; i<movieList.size();i++) {
                for(int j=i+1; j<movieList.size();j++) {
                    if(movieList.get(i).getTitle().equals(movieList.get(j).getTitle())) {
                        indexArray.add(movieList.get(j));
                    }
                }
            }
            movieList.removeAll(indexArray);

            return movieList;
        }

        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        public void onPostExecute(ArrayList<Movie> movieList) {
           mlAdapter.addAll(movieList);
           HttpRequester.addImdbRating(that, movieList, mlAdapter, (Boolean) criteria.get("isTime"), (Boolean) criteria.get("isGenre"));
        }
    }
}
