package semanticweb.hws14.movapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.Movie;

/**
 * Created by Frederik on 25.11.2014.
 */

//This is the adapter for the movielist
//It is mainly used for customized layout and styling
public class MovieListAdapter extends ArrayAdapter<Movie> {
    Context context;
    int layoutResourceId;
    ArrayList<Movie> data;

    public MovieListAdapter(Context context, int layoutResourceId, ArrayList<Movie> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MovieHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MovieHolder();
            holder.movieTitle = (TextView)row.findViewById(R.id.listItemTitle);
            holder.movieRating = (TextView)row.findViewById(R.id.listItemImdB);
            row.setTag(holder);
        } else {
            holder = (MovieHolder)row.getTag();
        }

        Movie movie = data.get(position);
        String imdbRating = movie.getImdbRating();
        if(imdbRating.startsWith("0")) {
            imdbRating = imdbRating.substring(2);
        }
        holder.movieTitle.setText(movie.getTitle());
        holder.movieRating.setText(imdbRating);

        LinearLayout p = (LinearLayout) holder.movieTitle.getParent();

        if(position % 2 == 1) {
            p.setBackground(context.getResources().getDrawable(R.drawable.list_item_1));
        } else {
            p.setBackground(context.getResources().getDrawable(R.drawable.list_item_2));
        }
        return row;
    }

    static class MovieHolder {
        TextView movieTitle;
        TextView movieRating;
    }
}
