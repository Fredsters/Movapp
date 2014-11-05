package semanticweb.hws14.movapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Frederik on 29.10.2014.
 */
    //TODO Genre is einfach kacke, weil die Daten scheise sind (vielleicht über omdb holen)
    //TODO Wenn nur Genre als einziges Criterium angegeben ist dann nutze SPARQL für einschränkung, wenn ein zweites criterium angegeben ist dann mache es bei sparql optional und hle zusätzlich daten über omdb
    //TODO: Nutze MOVIE Resource statt einzelnen Variablen beim sparql
    //TODO: Date criteria komplett einbauen und testen

    //Todo Title?

    //TODO Check if one input is empty and then dont use it maybe with switch?
    //TODO What to do when no criteria


public class Movie implements Parcelable {
    private String title;
    private int mdbId;
    private String imdbRating;
    private int releaseYear;
    private String imdbId;
    private String genre;

    /* Constructors */

    public Movie(String title, int mdbId, int releaseYear, String imdbId, String genre) {
        this.title = title;
        this.mdbId = mdbId;
        this.releaseYear = releaseYear;
        this.imdbId = imdbId;
        this.imdbRating = "0";
        this.genre = genre;
    }

    public Movie(String title, int mdbId, int releaseYear, String genre) {
        this.title = title;
        this.mdbId = mdbId;
        this.releaseYear = releaseYear;
        this.imdbId = "0";
        this.imdbRating = "0";
        this.genre = genre;
    }

    public Movie(String title, int releaseYear, String genre) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.imdbId = "0";
        this.mdbId = 0;
        this.imdbRating = "0";
        this.genre = genre;
    }

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeInt(mdbId);
        out.writeString(imdbRating);
        out.writeInt(releaseYear);
        out.writeString(imdbId);
        out.writeString(genre);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Movie(Parcel in) {
        title = in.readString();
        mdbId = in.readInt();
        imdbRating = in.readString();
        releaseYear = in.readInt();
        imdbId = in.readString();
        genre = in.readString();
    }

    /* Getter and Setter */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public int getMdbId() {
        return mdbId;
    }

    public void setMdbId(int mdbId) {
        this.mdbId = mdbId;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString(){

        String rating = "" + imdbRating;
        String out = this.title.toString() + "\nImdb-Rating: " + rating.toString();
        return out;
    }
}
