package eu.isawsm.accelerate.Model;

import android.graphics.Bitmap;

import java.net.URI;
import java.util.List;

/**
 * Created by ofade_000 on 04.04.2015.
 */
public interface IClub {
    String getName();

    void setName(String name);

    URI getUrl();

    void setUrl(URI url);

    Bitmap getImage();

    void setImage(Bitmap image);

    List<Track> getTracks();

    void setTracks(List<Track> tracks);
}
