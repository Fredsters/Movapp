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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(from);
        out.writeInt(to);
    }

    public static final Parcelable.Creator<TimePeriod> CREATOR = new Parcelable.Creator<TimePeriod>() {
        public TimePeriod createFromParcel(Parcel in) {
            return new TimePeriod(in);
        }
        public TimePeriod[] newArray(int size) {
            return new TimePeriod[size];
        }
    };

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


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TimePeriod))
            return false;
        if (obj == this)
            return true;
        TimePeriod rhs = (TimePeriod) obj;
        return (from == rhs.getFrom() && to == rhs.getTo());
    }

    public int hashCode() {
        int result = 0;
        result = (int)(from/12) + to;
        return result;
    }

}
