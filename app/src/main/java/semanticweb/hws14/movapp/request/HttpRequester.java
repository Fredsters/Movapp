package semanticweb.hws14.movapp.request;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.activities.List;
import semanticweb.hws14.movapp.model.MovieComparator;

/**
 * Created by Frederik on 29.10.2014.
 */

public class HttpRequester {
    public static ArrayList<Movie> addImdbRating (final Activity listActivity, final ArrayList<Movie>  movieList, final ArrayAdapter<Movie> mlAdapter, final boolean isTime, final boolean isGenre) {
        for(final Movie movie : movieList) {
            String url = "";
            String urlTitle = null;
            try {
                urlTitle = URLEncoder.encode(movie.getTitle(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(!"0".equals(movie.getImdbId())) {
                url = "http://www.omdbapi.com/?i=" + movie.getImdbId();
            } else if(movie.getReleaseYear() != 0) {
                url = "http://www.omdbapi.com/?t=" + urlTitle + "%20&y=" + movie.getReleaseYear();
            } else {
                url = "http://www.omdbapi.com/?t=" + urlTitle;
            }
            url+="&plot=short&r=json";

            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject r) {
                    boolean response = false;
                    try {
                        response = r.getBoolean("Response");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(response) {
                        //IMDB ID
                        try {
                            if ("0".equals(movie.getImdbId())) {
                                String imdbID = r.getString("imdbID");
                                movie.setImdbId(imdbID);
                            }
                        } catch (JSONException e) {
                            movie.setImdbId("");
                        }

                        //Genre
                        try {
                            if (isGenre) {
                                if ("".equals(movie.getGenre())) {
                                    String genreName = r.getString("Genre");
                                    movie.setGenre(genreName);
                                }
                                if (SparqlQueries.filterGenre(movieList, movie)) {
                                    movieList.remove(movie);
                                }
                            }
                        } catch (JSONException e) {
                            movie.setGenre("");
                        }
                        //TIME
                        try {
                            if (isTime) {
                                if (0 == movie.getReleaseYear()) {
                                    int releaseYear = r.getInt("Year");
                                    movie.setReleaseYear(releaseYear);
                                }
                                if (SparqlQueries.filterReleaseDate(movieList, movie)) {
                                    movieList.remove(movie);
                                }
                            }
                        } catch (JSONException e) {
                            movie.setReleaseYear(0);
                        }
                        //IMDB RATING
                        try {
                            double imdbRating = r.getDouble("imdbRating");
                            movie.setImdbRating(String.valueOf(imdbRating));
                        } catch (JSONException e) {
                            movie.setImdbRating("0 No Rating");
                        }
                    } else {
                        //TODO Do remove it or don't ????
                        movieList.remove(movie);
                    }

                    if(movieList.size() <= movieList.indexOf(movie) + 1) {

                        Collections.sort(movieList, new MovieComparator());
                        mlAdapter.clear();
                        listActivity.setProgressBarIndeterminateVisibility(false);
                        mlAdapter.addAll(movieList);
                      //  mlAdapter.notifyDataSetChanged();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("JSONExcepetion", "RESPONSE FAILED");
                }
            });

            HttpRequestQueueSingleton.getInstance(listActivity).addToRequestQueue(jsObjRequest);
        }
        return movieList;
    }
}
