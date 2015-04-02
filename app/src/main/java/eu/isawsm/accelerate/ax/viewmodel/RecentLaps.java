package eu.isawsm.accelerate.ax.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by olfad on 23.02.2015.
 */
public class RecentLaps implements Parcelable  {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    public static final Parcelable.Creator<RecentLaps> CREATOR = new Parcelable.Creator<RecentLaps>() {
        @Override
        public RecentLaps createFromParcel(Parcel source) {
            return  new RecentLaps();
        }

        @Override
        public RecentLaps[] newArray(int size) {
            return new RecentLaps[size];
        }
    };

    public RecentLaps(){

    }

    private RecentLaps(Parcel in) {
        super();
    }
}
