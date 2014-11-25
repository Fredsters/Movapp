package semanticweb.hws14.movapp.model;

import java.util.ArrayList;

/**
 * Created by Frederik on 07.11.2014.
 */
public class MovieDet extends Movie{

    //omdb
    String awards; //OMDB
    String poster; //OMDB
    String plot; //OMDB
    String rated; //OMDB
    String wikiAbstract; //DBP

    String voteCount; //OMDB
    int metaScore; //OMDB

    String budget; //DBP

    String runtime; //LMDB //DBP
    ArrayList<String> actors; //LMDB  //DBP
    ArrayList<String> directors; //LMDB //DBP
    ArrayList<String> writers; //LDMB //DBP
    ArrayList<String> genres; //Alle
    ArrayList<String> roles;

    private int actorRoleCount;

    //Noch nicht
    //distributor 20th centruy fox //DBP or LMDB
    //Based near  //LMDB or DBP

    public MovieDet(String title, int releaseYear, String genre) {
        super(title, releaseYear, genre);
    }

    public MovieDet(Movie movie) {
        super(movie.getTitle(), movie.getReleaseYear(), movie.getGenre(), movie.getDBPmovieResource(), movie.getLMDBmovieResource(), movie.getImdbId(), movie.getImdbRating());
        this.runtime = "";
        this.awards = "";
        this.poster = "";
        this.plot = "";
        this.rated = "";
        this.wikiAbstract = "";
        this.voteCount = "";
        this.metaScore = 0;
        this.budget = "";

        this.roles = new ArrayList<String>();
        this.actors = new ArrayList<String>();
        this.directors = new ArrayList<String>();
        this.writers = new ArrayList<String>();
        this.genres = new ArrayList<String>();

        actorRoleCount = 0;
    }

    public void addRole (String role) {
        if(!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public void addActorRole (String actor, String role) {
        if(!actor.contains("actor")) {
            if(!this.actors.contains(actor)) {
                this.actors.add(actorRoleCount, actor);
                if(!"".equals(role)) {
                    this.roles.add(actorRoleCount, role);
                } else {
                    this.roles.add(actorRoleCount, " ");
                }
                actorRoleCount++;
            } else if(!this.roles.contains(role) && !"".equals(role)) {
                int actorIndex = this.actors.indexOf(actor);
                this.actors.remove(actor);
                this.roles.remove(actorIndex);
                this.actors.add(actorIndex, actor);
                this.roles.add(actorIndex, role);
            }
        }
    }

    public void addDirector (String director) {
        if(!this.directors.contains(director)) {
            this.directors.add(director);
        }
    }

    public void addWriter (String writer) {
        if(!this.writers.contains(writer)) {
            this.writers.add(writer);
        }
    }

    public void addGenre (String genre) {
        if(!this.genres.contains(genre)) {
            this.genres.add(genre);
        }
    }

    public MovieDet updateUiComponents(){
        return this;
    }


    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getWikiAbstract() {
        return wikiAbstract;
    }

    public void setWikiAbstract(String wikiAbstract) {
        this.wikiAbstract = wikiAbstract;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public int getMetaScore() {
        return metaScore;
    }

    public void setMetaScore(int metaScore) {
        this.metaScore = metaScore;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public ArrayList<String> getActors() {
        return actors;
    }

    public void setActors(ArrayList<String> actors) {
        this.actors = actors;
    }

    public ArrayList<String> getDirectors() {
        return directors;
    }

    public void setDirectors(ArrayList<String> directors) {
        this.directors = directors;
    }

    public ArrayList<String> getWriters() {
        return writers;
    }

    public void setWriters(ArrayList<String> writers) {
        this.writers = writers;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "MovieDetail{" +
                "awards='" + awards + '\'' +
                ", poster='" + poster + '\'' +
                ", plot='" + plot + '\'' +
                ", rated='" + rated + '\'' +
                ", wikiAbstract='" + wikiAbstract + '\'' +
                ", voteCount=" + voteCount +
                ", metaScore=" + metaScore +
                ", budget='" + budget + '\'' +
                ", runtime='" + runtime + '\'' +
                ", actors=" + actors +
                ", directors=" + directors +
                ", writers=" + writers +
                ", genres=" + genres +
                '}';
    }

    public String createTvOutOfList(ArrayList list){
        ArrayList<String> actors = list;
        String result="";

        for( String actor : actors){

            result = result + actor + "\n";
        }
        return result;
    }
}
