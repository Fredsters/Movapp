package semanticweb.hws14.movapp.model;

import java.util.ArrayList;

/**
 * Created by Frederik on 07.11.2014.
 */
public class MovieDetail extends Movie{

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


    //Noch nicht
    //distributor 20th centruy fox //DBP or LMDB
    //Based near  //LMDB or DBP

    public MovieDetail(String title, int mdbId, int releaseYear, String imdbId, String genre ) {
        super(title, mdbId, releaseYear, imdbId, genre);
    }

    public MovieDetail(String title, int mdbId, int releaseYear, String genre) {
        super(title, mdbId, releaseYear, genre);
    }

    public MovieDetail(String title, int releaseYear, String genre) {
        super(title, releaseYear, genre);
    }

    public MovieDetail(Movie movie) {
        super(movie.getTitle(), movie.getLMDBmovieId(), movie.getReleaseYear(), movie.getImdbId(), movie.getGenre(), movie.getImdbRating());
        this.runtime = "";
        this.awards = "";
        this.poster = "";
        this.plot = "";
        this.rated = "";
        this.wikiAbstract = "";
        this.voteCount = "";
        this.metaScore = 0;
        this.budget = "";

        this.actors = new ArrayList<String>();
        this.directors = new ArrayList<String>();
        this.writers = new ArrayList<String>();
        this.genres = new ArrayList<String>();
    }

    public boolean addActor (String actor) {
        if(this.actors.contains(actor)) {
            return false;
        } else {
            this.actors.add(actor);
            return true;
        }
    }

    public boolean addDirector (String director) {
        if(this.directors.contains(director)) {
            return false;
        } else {
            this.directors.add(director);
            return true;
        }
    }

    public boolean addWriter (String writer) {
        if(this.writers.contains(writer)) {
            return false;
        } else {
            this.writers.add(writer);
            return true;
        }
    }

    public boolean addGenre (String genre) {
        if(this.genres.contains(genre)) {
            return false;
        } else {
            this.genres.add(genre);
            return true;
        }
    }

    public MovieDetail updateUiComponents(){
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
}
