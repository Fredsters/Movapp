package semanticweb.hws14.movapp.request;

import android.app.Activity;
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

import semanticweb.hws14.movapp.activities.MovieList;
import semanticweb.hws14.movapp.model.Movie;
import semanticweb.hws14.movapp.model.MovieComparator;
import semanticweb.hws14.movapp.model.MovieDet;

/**
 * Created by Frederik on 29.10.2014.
 */

public class HttpRequester {
    public static void addOmdbData(final Activity listActivity, final ArrayList<Movie> movieList, final ArrayAdapter<Movie> mlAdapter, final boolean isTime, final boolean isGenre, final boolean isActor, final boolean isDirector) {
        for(final Movie movie : movieList) {

            String url = prepareURL(movie, false);

            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject r) {
                    boolean response = false;
                    boolean lastMovie = false;
                    try {
                        response = r.getBoolean("Response");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(response) {

                        //TIME
                        try {
                            if (isTime && (isActor || isDirector ||isGenre)) {
                                if (0 == movie.getReleaseYear()) {
                                    int releaseYear = r.getInt("Year");
                                    movie.setReleaseYear(releaseYear);
                                }
                                if (SparqlQueries.filterReleaseDate(movie)) {
                                    if (movieList.size() <= movieList.indexOf(movie) + 1) {
                                        lastMovie = true;
                                    }
                                    movieList.remove(movie);
                                }
                            }
                        } catch (JSONException e) {
                            if (movieList.size() <= movieList.indexOf(movie) + 1) {
                                lastMovie = true;
                            }
                            movieList.remove(movie);
                        }

                        if (!(movieList.indexOf(movie) == -1)) {
                            //Genre
                            try {
                                if (isGenre && (isActor || isDirector)) {
                                    String genreName = r.getString("Genre");
                                    movie.setGenre(genreName);
                                    if (SparqlQueries.filterGenre(movie)) {
                                        if (movieList.size() <= movieList.indexOf(movie) + 1) {
                                            lastMovie = true;
                                        }
                                        movieList.remove(movie);
                                    }
                                }
                            } catch (JSONException e) {
                                if (movieList.size() <= movieList.indexOf(movie) + 1) {
                                    lastMovie = true;
                                }
                                movieList.remove(movie);
                            }
                            if (!(movieList.indexOf(movie) == -1)) {
                                //IMDB ID

                                try {
                                    if ("0".equals(movie.getImdbId())) {
                                        String imdbID = r.getString("imdbID");
                                        movie.setImdbId(imdbID);
                                    }
                                } catch (JSONException e) {
                                    movie.setImdbId("0");
                                }

                                //IMDB RATING
                                try {
                                    double imdbRating = r.getDouble("imdbRating");
                                    movie.setImdbRating(String.valueOf(imdbRating));
                                } catch (JSONException e) {
                                    movie.setImdbRating("0 No Rating");
                                }
                            }
                        }
                    } else {
                        if (movieList.size() <= movieList.indexOf(movie) + 1) {
                            lastMovie = true;
                        }

                        if (!isActor && !isDirector) {
                            movieList.remove(movie);
                        }

                    }

                    if (lastMovie || movieList.size() <= movieList.indexOf(movie) + 1) {

                        mlAdapter.clear();
                        Collections.sort(movieList, new MovieComparator());
                        mlAdapter.addAll(movieList);
                        listActivity.setProgressBarIndeterminateVisibility(false);
                        MovieList.staticMovieList = movieList;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("JSONException", "RESPONSE FAILED");
                }
            });

            HttpRequestQueueSingleton.getInstance(listActivity).addToRequestQueue(jsObjRequest);
        }
    }

    public static void loadWebServiceData (final Activity detailActivity, final MovieDet movie) {
        String url = prepareURL(movie, true);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject r) {
                boolean response = false;
                try {
                    response = r.getBoolean("Response");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(response) {
                    try {
                        String rated = r.getString("Rated");
                        movie.setRated(rated);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String genre = r.getString("Genre");
                        String[] genreArray = (genre.split(", "));
                        ArrayList<String> newGenres = new ArrayList<String>();
                        for(int i=0; i< genreArray.length; i++) {
                            newGenres.add(new String(genreArray[i]).trim());

                        }
                        for(int i =0; i < newGenres.size(); i++) {
                            if(!movie.getGenres().contains(newGenres.get(i))) {
                                movie.addGenre(genreArray[i]);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String plot = r.getString("Plot");
                        movie.setPlot(plot);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String awards = r.getString("Awards");
                        movie.setAwards(awards);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String posterUrl = r.getString("Poster");
                        movie.setPoster(posterUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        int metascore = r.getInt("Metascore");
                        movie.setMetaScore(metascore);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String voteCount = r.getString("imdbVotes");
                        movie.setVoteCount(voteCount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        int releaseYear = r.getInt("Year");
                        movie.setReleaseYear(releaseYear);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if("".equals(movie.getRuntime())) {
                        try {
                            String runtime = r.getString("Runtime");
                            movie.setRuntime(runtime);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    movie.geteListener().onFinished(movie);

                } else {
                    Log.e("FUCK", "THE SYSTEM");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("JSONException", "RESPONSE FAILED");
            }
        });

        HttpRequestQueueSingleton.getInstance(detailActivity).addToRequestQueue(jsObjRequest);
    }

    private static String prepareURL(Movie movie, boolean detail) {
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
        if(detail) {
            url+="&plot=full";
        } else {
            url+="&plot=short";
        }
        return url;
    }
}
