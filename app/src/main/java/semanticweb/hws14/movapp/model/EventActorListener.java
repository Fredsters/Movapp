package semanticweb.hws14.movapp.model;

/**
 * Created by Frederik on 14.11.2014.
 */
//Handler for Actor, needed for callback, after response is there
public interface EventActorListener {

    public void onFinished(ActorDet actor);
}
