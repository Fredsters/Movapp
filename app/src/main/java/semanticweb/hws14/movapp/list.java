package semanticweb.hws14.movapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import model.Movie;
import model.MovieComparator;


public class list extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();


        ArrayList<Movie> movieList = intent.getParcelableArrayListExtra("movieList");


        ListView listView = (ListView) findViewById(R.id.resultList);

        for(Movie movie : movieList) {
            Log.d("Title: ",movie.getTitle() + " Rating: " +movie.getImdbRating());
        }
        Collections.sort(movieList,  new MovieComparator());

        for(Movie movie : movieList) {
            Log.d("Title: ",movie.getTitle() + " Rating: " +movie.getImdbRating());
        }


        // Convert ArrayList to array
       // HashMap<String, String>[] lv_arr = (HashMap<String,String>[]) result.toArray();
        ArrayAdapter adapter = new ArrayAdapter<Movie>(list.this,android.R.layout.simple_list_item_1, movieList);
        listView.setAdapter(adapter);
        //TODO: Cach the data or just query if the list does not have items
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
