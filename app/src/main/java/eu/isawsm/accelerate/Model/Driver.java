package eu.isawsm.accelerate.Model;

import android.graphics.Bitmap;
import android.media.Image;

import java.net.URI;
import java.net.URL;


/**
 * Created by olfad on 29.01.2015.
 */
public class Driver {
    private String firstname;
    private String lastname;
    private String acronym;
    private Bitmap image;
    private URI mail;
    private String password;
    private String Salt;

    public Driver(String firstname, String lastname, String acronym, Bitmap image, URI mail) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.acronym = acronym;
        this.image = image;
        this.mail = mail;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public URI getMail() {
        return mail;
    }

    public void setMail(URI mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return Salt;
    }

    public void setSalt(String salt) {
        Salt = salt;
    }
}
