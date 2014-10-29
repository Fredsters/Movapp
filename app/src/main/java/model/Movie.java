package model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Frederik on 29.10.2014.
 */
public class Movie implements Parcelable {
    private String title;
    private int mdbId;
    private String imdbFilmId;
    private float imdbRating;

    /* Constructors */
    public Movie(String title, int mdbId, String imdbFilmId, float imdbRating) {
        this.title = title;
        this.mdbId = mdbId;
        this.imdbFilmId = imdbFilmId;
        this.imdbRating = imdbRating;
    }

    public Movie(String title, int mdbId , String imdbFilmId) {
        this.title = title;
        this.mdbId = mdbId;
        this.imdbFilmId = imdbFilmId;
    }

    public Movie(String title) {
        this.title = title;
    }

    public Movie() {
    }

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeInt(mdbId);
        out.writeString(imdbFilmId);
        out.writeFloat(imdbRating);
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
        imdbFilmId = in.readString();
        imdbRating = in.readFloat();
    }


    public String getImdbFilmId() {
        return imdbFilmId;
    }

    public void setImdbFilmId(String imdbFilmId) {
        this.imdbFilmId = imdbFilmId;
    }

    /* Getter and Setter */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public int getMdbId() {
        return mdbId;
    }

    public void setMdbId(int mdbId) {
        this.mdbId = mdbId;
    }
}
