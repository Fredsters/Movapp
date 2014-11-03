package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.request.SparqlListQuery;


public class List extends Activity {

    public static ArrayList<Movie> movieList;
    public static ArrayAdapter mlAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        String actorName = intent.getStringExtra("actorName");

        this.movieList = new ArrayList<Movie>();
        this.mlAdapter = new ArrayAdapter<Movie>(this,android.R.layout.simple_list_item_1, this.movieList);

        //TODO: Remove and make the query async
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ListView listView = (ListView) findViewById(R.id.resultList);
        listView.setAdapter(mlAdapter);

        SparqlListQuery q = new SparqlListQuery();
        q.execute(actorName);



  //      Collections.sort(movieList,  new MovieComparator());

/*        for(Movie movie : movieList) {
            Log.d(" Rating: " +movie.getImdbRating() + "   Title: ",movie.getTitle());
        }*/
        //HttpRequester.addImdbRating(this, this.movieList);

//        ArrayAdapter adapter = new ArrayAdapter<Movie>(list.this,android.R.layout.simple_list_item_1, movieList);
    //    listView.setAdapter(adapter);


        // TODO : Work this shit
        //adds ImdbRating to the movieList. Since the Http Request to get the imdb Rating is
        // asynchronous. The next activity can only started, after the response is received.
        // Therefore the next activity is started within this method
        // It would be much nicer if i could make the Http Request synchronous --> Does not work for now
        // The best would be if everything is async again, but for now its ok like this.
        // The whole damn thing is so extraordinary ugly. It would not get laid.
        // IN SHORT: This methods add ImdbRating and starts next Activity
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
}
