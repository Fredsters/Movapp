package semanticweb.hws14.movapp.request;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.activities.List;

/**
 * Created by Frederik on 29.10.2014.
 */

public class HttpRequester {
    public static ArrayList<Movie> addImdbRating (final Activity criteriaActivity, final ArrayList<Movie>  movieList) {
        for(final Movie movie : movieList) {
            String url = "";
            String urlTitle = null;
            try {
                urlTitle = URLEncoder.encode(movie.getTitle(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            url ="http://www.omdbapi.com/?t="+urlTitle+"%20&y="+movie.getReleaseYear();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject r) {
                    try {
                        double imdbRating = r.getDouble("imdbRating");

                        movie.setImdbRating(String.valueOf(imdbRating));
                    } catch (JSONException e) {
                        //Really bad coding style :D :D
                        try {
                            if (r.getBoolean("Response")) {
                                movie.setImdbRating("No rating");
                            }
                        } catch (JSONException e1) {
                            Log.e("JSONExcepetion", "No Response --> Movie does not really exist");
                        }

                    }
                    if(null == movie.getImdbRating()){
                        movieList.remove(movie);
                    }
                    if(movieList.size() == movieList.indexOf(movie) + 1) {
                        startActivity(criteriaActivity, movieList);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("JSONExcepetion", "RESPONSE FAILED");
                    if(movieList.size() == movieList.indexOf(movie) + 1) {
                        startActivity(criteriaActivity, movieList);
                    }
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

    public static void startActivity(Activity criteriaActivity, ArrayList<Movie>  movieList) {
        Intent intent = new Intent(criteriaActivity, List.class);
        intent.putParcelableArrayListExtra("movieList", movieList);
        criteriaActivity.startActivity(intent);
    }
}
