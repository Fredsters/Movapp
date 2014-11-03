package semanticweb.hws14.movapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Frederik on 29.10.2014.
 */
//TODO: Should ImdbRating be String or float?
public class Movie implements Parcelable {
    private String title;
    private int mdbId;
    private String imdbRating;

    /* Constructors */

    public Movie(String title, int mdbId) {
        this.title = title;
        this.mdbId = mdbId;
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
        //       out.writeString(imdbFilmId);
        out.writeString(imdbRating);
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
        //      imdbFilmId = in.readString();
        imdbRating = in.readString();
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
}
