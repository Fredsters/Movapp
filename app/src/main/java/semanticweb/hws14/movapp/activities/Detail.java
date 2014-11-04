package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.Movie;


public class Detail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Movie movie = (Movie)intent.getParcelableExtra("movie");

        TextView movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieTitle.setText(movie.getTitle());

        TextView movieRating = (TextView) findViewById(R.id.movieRating);
        movieRating.setText(movie.getImdbRating());

        TextView releaseYear = (TextView) findViewById(R.id.releaseYear);
        releaseYear.setText(String.valueOf(movie.getReleaseYear()) );

        TextView ImdbId = (TextView) findViewById(R.id.ImdbId);
        ImdbId.setText(String.valueOf(movie.getImdbId()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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
