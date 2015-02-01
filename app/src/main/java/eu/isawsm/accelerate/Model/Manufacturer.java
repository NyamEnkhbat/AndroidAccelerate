package eu.isawsm.accelerate.Model;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by Oliver on 31.01.2015.
 */
public class Manufacturer {
    private String name;
    private Bitmap image;

    public Manufacturer(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
