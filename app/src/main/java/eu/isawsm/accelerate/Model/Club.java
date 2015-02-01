package eu.isawsm.accelerate.Model;

import android.graphics.Bitmap;
import android.media.Image;

import java.net.URI;
import java.net.URI;

/**
 * Created by Oliver on 31.01.2015.
 */
public class Club {
    private String name;
    private URI url;
    private Bitmap image;

    public Club(String name, URI url, Bitmap image) {
        this.name = name;
        this.url = url;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
