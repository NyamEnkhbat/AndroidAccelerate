package eu.isawsm.accelerate.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.util.List;

/**
 * Created by Oliver on 31.01.2015.
 */
public class Club implements IClub {
    private String name;
    private URI url;
    private Bitmap image;
    private List<Track> tracks;

    public Club(String name, URI url, Bitmap image, List<Track> tracks) {
        this.name = name;
        this.url = url;
        this.image = image;
        this.tracks = tracks;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public URI getUrl() {
        return url;
    }

    @Override
    public void setUrl(URI url) {
        this.url = url;
    }

    @Override
    public Bitmap getImage() {
        return image;
    }

    @Override
    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public List<Track> getTracks() {
        return tracks;
    }

    @Override
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
