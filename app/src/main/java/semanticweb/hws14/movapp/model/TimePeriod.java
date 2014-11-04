package semanticweb.hws14.movapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Frederik on 04.11.2014.
 */
public class TimePeriod implements Parcelable {
    private int from;
    private int to;

    public TimePeriod(int from, int to) {
        this.from = from;
        this.to = to;
    }

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(from);
        out.writeInt(to);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<TimePeriod> CREATOR = new Parcelable.Creator<TimePeriod>() {
        public TimePeriod createFromParcel(Parcel in) {
            return new TimePeriod(in);
        }
        public TimePeriod[] newArray(int size) {
            return new TimePeriod[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private TimePeriod(Parcel in) {
        from = in.readInt();
        to = in.readInt();
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
