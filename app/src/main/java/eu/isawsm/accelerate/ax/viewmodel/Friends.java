package eu.isawsm.accelerate.ax.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by olfad on 23.02.2015.
 */
public class Friends implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    public static final Parcelable.Creator<Friends> CREATOR = new Parcelable.Creator<Friends>() {
        @Override
        public Friends createFromParcel(Parcel source) {
            return  new Friends();
        }

        @Override
        public Friends[] newArray(int size) {
            return new Friends[size];
        }
    };

    public Friends(){

    }

    private Friends(Parcel in) {
        super();
    }
}
