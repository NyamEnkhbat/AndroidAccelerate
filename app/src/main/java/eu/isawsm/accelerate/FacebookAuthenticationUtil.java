package eu.isawsm.accelerate;

import android.app.DownloadManager;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.net.URI;
import java.util.Arrays;

import eu.isawsm.accelerate.Model.Driver;
import eu.isawsm.accelerate.ax.MainActivity;

/**
 * Created by olfad on 23.03.2015.
 */
public class FacebookAuthenticationUtil implements IAuthenticator, View.OnClickListener, Session.StatusCallback {

    private MainActivity mContext;
    private static final String TAG = "FacebookAuthenticationUtil";
    public FacebookAuthenticationUtil(MainActivity mContext) {
        this.mContext = mContext;

    }



    @Override
    public void onClick(View v) {
        this.mContext.setAuthenticator(this);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Session.getActiveSession().onActivityResult(mContext, requestCode, resultCode, data);

        if (Session.getActiveSession().isOpened())
        {
            // Request user data and show the results
            Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback()
            {
                @Override
                public void onCompleted(GraphUser user, Response response)
                {
                    if (null != user)
                    {
                        // Display the parsed user info
                        Log.v(TAG, "Response : " + response);
                        Log.v(TAG, "UserID : " + user.getId());
                        Log.v(TAG, "User FirstName : " + user.getFirstName());
                        Driver.get(mContext).setMail(URI.create(user.asMap().get("email").toString()), mContext);
                        Driver.get(mContext).setFirstname(user.getFirstName() + " " + user.getLastName(), mContext);
                        
                    }
                }
            }).executeAsync();
        }
    }

}
