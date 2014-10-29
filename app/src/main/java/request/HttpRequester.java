package request;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.Movie;

/**
 * Created by Frederik on 29.10.2014.
 */

public class HttpRequester {
    public static ArrayList<Movie> addImdbRating (Activity criteriaActivity, ArrayList<Movie>  movieList) {
        for(final Movie movie : movieList) {


           String url ="http://www.omdbapi.com/?i="+movie.getImdbFilmId();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject r) {
                    try {
                        movie.setImdbRating((float)r.getDouble("imdbRating"));
                        Log.d("ACHTUNG", movie.getTitle()+", Rating: "+ movie.getImdbRating());
                    } catch (JSONException e) {
                        movie.setImdbRating(0.0f);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    movie.setImdbRating(0.0f);
                }
            });
            HttpRequestQueueSingleton.getInstance(criteriaActivity).addToRequestQueue(jsObjRequest);

/*
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
/*
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                response = httpclient.execute(new HttpGet(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                Log.d("ACHTUNG", response.toString());
                //..more logic
            } else{
                Log.d("ACHTUNG", response.toString());
            }*/
            Log.d("MovieList", movie.toString());
        }

        return movieList;
    }
}
