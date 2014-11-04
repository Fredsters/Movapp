package semanticweb.hws14.movapp.request;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;

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
    public static ArrayList<Movie> addImdbRating (final Activity criteriaActivity, final ArrayList<Movie>  movieList, final ArrayAdapter<Movie> mlAdapter) {
        for(final Movie movie : movieList) {
            String url = "";
            String urlTitle = null;
            try {
                urlTitle = URLEncoder.encode(movie.getTitle(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //&plot=short&r=json
            //TODO Check every film from Jim Carrey
            if(!"0".equals(movie.getImdbId()) && null != movie.getImdbId()) {
                url = "http://www.omdbapi.com/?i=" + movie.getImdbId();
            } else if(movie.getReleaseYear() != 0) {
                url = "http://www.omdbapi.com/?t=" + urlTitle + "%20&y=" + movie.getReleaseYear();
            } else {
                url = "http://www.omdbapi.com/?t=" + urlTitle;
            }
            url+="&plot=short&r=json";
            //http://www.omdbapi.com/?t=Lemony+Snicket's&y=&plot=short&r=json
            //TODO get correct data

            //TODO much wrong data with year?

            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject r) {
                    try {
                        if("0".equals(movie.getImdbId())) {
                            String imdbID = r.getString("imdbID");
                            movie.setImdbId(imdbID);
                        }

                        double imdbRating = r.getDouble("imdbRating");
                        movie.setImdbRating(String.valueOf(imdbRating));

                    } catch (JSONException e) {
                        movie.setImdbRating("0");
                    }
                    boolean isLastMovie = false;
                    if("0".equals(movie.getImdbId()) || null == movie.getImdbId()){
                        if(movieList.size() == movieList.indexOf(movie) + 1) {
                            isLastMovie = true;
                        }
                        movieList.remove(movie);


                    } else if(null == movie.getImdbRating()) {
                        movie.setImdbRating("0");
                    }
                    if(isLastMovie) {
                        Collections.sort(movieList, new MovieComparator());
                        mlAdapter.clear();
                        mlAdapter.addAll(movieList);
                        mlAdapter.notifyDataSetChanged();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("JSONExcepetion", "RESPONSE FAILED");
                }
            });

            HttpRequestQueueSingleton.getInstance(criteriaActivity).addToRequestQueue(jsObjRequest);

/* This should be synchronous, but it does not work
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);

            // Add the request to the RequestQueue.
            HttpRequestQueueSingleton.getInstance(criteriaActivity).addToRequestQueue(request);

            try {
                JSONObject response = future.get();
                Log.d("response", response.toString());
                // do something with response
            } catch (InterruptedException e) {
                // handle the error
            } catch (ExecutionException e) {
                // handle the error
            } */
        }
        return movieList;
    }
}
