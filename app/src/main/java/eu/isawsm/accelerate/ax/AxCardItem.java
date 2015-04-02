package eu.isawsm.accelerate.ax;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by ofade_000 on 20.02.2015.
 */
public class AxCardItem<T> implements Parcelable {

    private T t;

    public T get() {
        return t;
    }

    public void set(T object){
        this.t = object;
    }

    public AxCardItem(T t){
        this.t = t;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof  AxCardItem){
            AxCardItem<T> cardItem = (AxCardItem<T>) o;
            return this.get().equals(cardItem.get());
        }
        else
            return super.equals(o);
    }

    @Override
    public int hashCode() {
        return t.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(t);
    }
    public static final Creator<AxCardItem> CREATOR = new Creator<AxCardItem>() {
        @Override
        public AxCardItem createFromParcel(Parcel source) {
            AxCardItem<Object> retVal = new AxCardItem<>(source.readValue(null));
            return retVal;
        }

        @Override
        public AxCardItem[] newArray(int size) {
            return new AxCardItem[size];
        }
    };

    private AxCardItem(Parcel in) {
        super();

        t = (T)in.readValue(null);
    }

}
