package eu.isawsm.accelerate;


import android.content.Intent;

/**
 *
 * Created by olfad on 25.03.2015.
 */
public interface IAuthenticator {
    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void logoff();
}
