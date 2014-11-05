package semanticweb.hws14.movapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Frederik on 29.10.2014.
 */

    //Todo Title?
    //TODO budget?
    //TODO Check if one input is empty and then dont use it
    //TODO What to do when no criteria

    //TODO busyindicator in action bar and show results after they are there

    //TODO get Genre via REGEX and improve genre dropdown data
    //TODO: Date criteria
public class Movie implements Parcelable {
    private String title;
    private int mdbId;
    private String imdbRating;
    private int releaseYear;
    private String imdbId;

    /* Constructors */

    public Movie(String title, int mdbId, int releaseYear, String imdbId) {
        this.title = title;
        this.mdbId = mdbId;
        this.releaseYear = releaseYear;
        this.imdbId = imdbId;
        this.imdbRating = "0";
    }

    public Movie(String title, int mdbId, int releaseYear) {
        this.title = title;
        this.mdbId = mdbId;
        this.releaseYear = releaseYear;
        this.imdbId = "0";
        this.imdbRating = "0";
    }

    public Movie(String title, int releaseYear) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.imdbId = "0";
        this.mdbId = 0;
        this.imdbRating = "0";
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

    @Override
    public String toString(){

        String rating = "" + imdbRating;
        String out = this.title.toString() + "\nImdb-Rating: " + rating.toString();
        return out;
    }
}
