package semanticweb.hws14.movapp.model;

/**
 * Created by Frederik on 11.11.2014.
 */
//Handler for movies, needed for callback, after response is there
public interface EventListener {

    public void onFinished(MovieDet movie);
}
