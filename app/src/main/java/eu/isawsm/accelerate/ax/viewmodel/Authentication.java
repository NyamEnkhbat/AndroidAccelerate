package eu.isawsm.accelerate.ax.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by olfad on 19.03.2015.
 */
public class Authentication implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    public static final Creator<Authentication> CREATOR = new Creator<Authentication>() {
        @Override
        public Authentication createFromParcel(Parcel source) {
            return  new Authentication();
        }

        @Override
        public Authentication[] newArray(int size) {
            return new Authentication[size];
        }
    };

    public Authentication(){

    }

    private Authentication(Parcel in) {
        super();
    }
}
