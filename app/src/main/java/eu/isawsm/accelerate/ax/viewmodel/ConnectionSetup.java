package eu.isawsm.accelerate.ax.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ofade_000 on 20.02.2015.
 */
public class ConnectionSetup implements Parcelable {
    @Override
    public boolean equals(Object o) {
        return o instanceof ConnectionSetup;
    }


    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    public static final Parcelable.Creator<ConnectionSetup> CREATOR = new Parcelable.Creator<ConnectionSetup>() {
        @Override
        public ConnectionSetup createFromParcel(Parcel source) {
            return  new ConnectionSetup();
        }

        @Override
        public ConnectionSetup[] newArray(int size) {
            return new ConnectionSetup[size];
        }
    };

    public ConnectionSetup(){

    }

    private ConnectionSetup(Parcel in) {
        super();
    }

}
