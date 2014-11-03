package semanticweb.hws14.movapp.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.model.Movie;

/**
 * Created by Frederik on 01.11.2014.
 */

//TODO Implement an own adapter
public class MovieListAdapter extends BaseAdapter {

    private static ArrayList<Movie> movieList;

    private LayoutInflater mInflater;

    public MovieListAdapter(Context context, ArrayList<Movie> movieList){
        this.movieList = movieList;
        this.mInflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int pos) {
        return movieList.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;

    }
}