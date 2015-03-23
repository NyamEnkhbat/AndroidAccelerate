package eu.isawsm.accelerate;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;

import com.facebook.Session;
import com.facebook.SessionState;

import java.util.Arrays;

import eu.isawsm.accelerate.ax.MainActivity;

/**
 * Created by olfad on 23.03.2015.
 */
public class FacebookAuthenticationUtil implements View.OnClickListener, Session.StatusCallback {

    private MainActivity mContext;
    private static final String TAG = "FacebookAuthenticationUtil";
    public FacebookAuthenticationUtil(MainActivity mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v) {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(mContext).setPermissions(Arrays.asList("public_profile")).setCallback(this));
            } else {
                Session.openActiveSession(mContext, true, this);
            }
        }
    }

    @Override
    public void call(Session session, SessionState sessionState, Exception e) {

        if (sessionState.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (sessionState.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }
}
