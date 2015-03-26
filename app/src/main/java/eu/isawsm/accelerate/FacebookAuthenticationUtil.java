package eu.isawsm.accelerate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

import eu.isawsm.accelerate.Model.Driver;
import eu.isawsm.accelerate.ax.MainActivity;

/**
 * Created by olfad on 23.03.2015.
 */
public class FacebookAuthenticationUtil implements IAuthenticator, View.OnClickListener, Session.StatusCallback  {

    private MainActivity mContext;
    private static final String TAG = "FacebookAuthentication";
    public FacebookAuthenticationUtil(MainActivity mContext) {
        this.mContext = mContext;
        Driver.get(mContext).setAuthenticator(this, mContext);

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

                            new MyAsyncTask().execute(user);
                    }
                }
            }).executeAsync();

        }
    }

    private class MyAsyncTask extends AsyncTask<GraphUser, Void, Void>
    {

        ProgressDialog mProgressDialog;
        @Override
        protected void onPostExecute(Void result) {
            mContext.onLoginSuccess();
            mProgressDialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext,
                    "Loading...", "Data is Loading...");
        }

        @Override
        protected Void doInBackground(GraphUser... params) {
            GraphUser user = params[0];
            Driver.get(mContext).setMail(URI.create(user.asMap().get("email").toString()), mContext);
            Driver.get(mContext).setFirstname(user.getFirstName() + " " + user.getLastName(), mContext);
            URL img_value = null;
            try {
                img_value = new URL("http://graph.facebook.com/"+user.getId()+"/picture?type=large");
                Driver.get(mContext).setImage(BitmapFactory.decodeStream(img_value.openConnection().getInputStream()),mContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void logoff() {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {
            session = new Session(mContext);
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
            //clear your preferences if saved
        }
    }

    public boolean isSignedIn(){
        return Session.getActiveSession() !=null;
    }

}
