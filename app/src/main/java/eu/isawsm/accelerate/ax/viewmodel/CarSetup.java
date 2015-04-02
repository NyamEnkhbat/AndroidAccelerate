package eu.isawsm.accelerate.ax.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ofade_000 on 21.02.2015.
 */
public class CarSetup implements Parcelable{
    @Override
    public boolean equals(Object o) {
        return o instanceof CarSetup;
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
    public static final Parcelable.Creator<CarSetup> CREATOR = new Parcelable.Creator<CarSetup>() {
        @Override
        public CarSetup createFromParcel(Parcel source) {
            return  new CarSetup();
        }

        @Override
        public CarSetup[] newArray(int size) {
            return new CarSetup[size];
        }
    };

    public CarSetup(){

    }

    private CarSetup(Parcel in) {
        super();
    }
}
