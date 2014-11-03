package semanticweb.hws14.movapp.model;

import java.util.Comparator;

/**
 * Created by Frederik on 30.10.2014.
 */
public class MovieComparator implements Comparator<Movie>{
    @Override
    public int compare(Movie movie1, Movie movie2) {
        return -(movie1.getImdbRating().compareTo(movie2.getImdbRating()));
    }
}

