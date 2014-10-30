package model;

import java.util.Comparator;

/**
 * Created by Frederik on 30.10.2014.
 */
public class MovieComparator implements Comparator<Movie>{
    @Override
    public int compare(Movie movie1, Movie movie2) {
        if(movie1.getImdbRating() > movie2.getImdbRating()) {
            return -1;
        } else if (movie1.getImdbRating() < movie2.getImdbRating()) {
            return 1;
        } else {
            return 0;
        }
    }
}
