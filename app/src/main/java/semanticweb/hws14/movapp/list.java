package semanticweb.hws14.movapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

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
        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: edit startIntent in a way that its open the activity result
                startIntent();


            }
        };

        listView.setOnItemClickListener(clickListen);

        for(Movie movie : movieList) {
            Log.d("Title: ",movie.getTitle() + " Rating: " +movie.getImdbRating());
        }
        Collections.sort(movieList,  new MovieComparator());

        for(Movie movie : movieList) {
            Log.d("Title: ",movie.getTitle() + " Rating: " +movie.getImdbRating());
        }

        Movie[] movies = convertToArray(movieList);

        // Convert ArrayList to array
       // HashMap<String, String>[] lv_arr = (HashMap<String,String>[]) result.toArray();
        ArrayAdapter adapter = new ArrayAdapter<Movie>(list.this,android.R.layout.simple_list_item_1, movieList);
        listView.setAdapter(adapter);
        //TODO: Cach the data or just query if the list does not have items
    }

    private void startIntent() {
        Intent intent = new Intent(this,result.class);
    }

    private Movie[] convertToArray(ArrayList<Movie> movieList) {

        Movie[] m = new Movie[movieList.size()];
        for(int index=0;index < movieList.size();index++){
            m[index] = movieList.get(index);
        }
        return m;

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
