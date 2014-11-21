package semanticweb.hws14.movapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Frederik on 29.10.2014.
 */


public class Movie implements Parcelable {
    private String title;
    private int releaseYear;
    private String genre;
    private String imdbId;
    private String LMDBmovieResource;
    private String DBPmovieResource;
    private String imdbRating;

    private EventListener eListener = null;

    /* Constructors */

    public Movie(String title, int releaseYear, String genre) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.imdbId = "";
        this.LMDBmovieResource = "";
        this.DBPmovieResource = "";
        this.imdbRating = "";
    }


    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeInt(releaseYear);
        out.writeString(genre);
        out.writeString(imdbId);
        out.writeString(LMDBmovieResource);
        out.writeString(DBPmovieResource);
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
        releaseYear = in.readInt();
        genre = in.readString();
        imdbId = in.readString();
        LMDBmovieResource = in.readString();
        DBPmovieResource = in.readString();
        imdbRating = in.readString();
    }

    /* Getter and Setter */

    public EventListener geteListener() {
        return eListener;
    }

    public void setOnFinishedEventListener(EventListener listener) {
        eListener = listener;
    }

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
        this.genre += " "+genre;
    }

    public String getLMDBmovieResource() {
        return LMDBmovieResource;
    }

    public void setLMDBmovieResource(String LMDBmovieResource) {
        this.LMDBmovieResource = LMDBmovieResource;
    }

    public String getDBPmovieResource() {
        return DBPmovieResource;
    }

    public void setDBPmovieResource(String DBPmovieResource) {
        this.DBPmovieResource = DBPmovieResource;
    }

    @Override
    public String toString(){

        String rating = "" + imdbRating;
        String out = this.title.toString() + "\nImdb-Rating: " + rating.toString();
        return out;
    }
/*
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Movie movie = (Movie) obj;
        return (title == movie.getTitle() || (title != null && title.equals(movie.getTitle())));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((title == null) ? 0 : title.hashCode());
        return result;
    }*/


}
