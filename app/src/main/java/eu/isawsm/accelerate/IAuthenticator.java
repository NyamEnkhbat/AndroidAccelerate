package eu.isawsm.accelerate;


import android.content.BroadcastReceiver;
import android.content.Intent;

/**
 * Created by olfad on 25.03.2015.
 */
public interface IAuthenticator {
    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public boolean isSignedIn();

    public void logoff();
}
