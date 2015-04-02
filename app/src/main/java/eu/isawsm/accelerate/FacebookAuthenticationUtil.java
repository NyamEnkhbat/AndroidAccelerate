package eu.isawsm.accelerate;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

import eu.isawsm.accelerate.Model.AxUser;
import eu.isawsm.accelerate.ax.MainActivity;

/**
 * Created by olfad on 23.03.2015.
 */
public class FacebookAuthenticationUtil extends BroadcastReceiver implements IAuthenticator, View.OnClickListener, Session.StatusCallback {

    private static final String TAG = "FacebookAuthentication";
    private MainActivity mContext;
    public FacebookAuthenticationUtil(MainActivity mContext) {
        this.mContext = mContext;
        LocalBroadcastManager.getInstance(mContext).registerReceiver(this,
                new IntentFilter("View"));
    }


    public FacebookAuthenticationUtil() {
        LocalBroadcastManager.getInstance(mContext).registerReceiver(this,
                new IntentFilter("View"));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() != R.id.sign_in_button_fb) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(this);
            return;
        }
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
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(this);
            Log.i(TAG, "Logged out...");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == GoogleAuthenticationUtil.REQUEST_CODE_RESOLVE_ERR) return;

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

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link android.content.Context#registerReceiver(android.content.BroadcastReceiver,
     * android.content.IntentFilter, String, android.os.Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p/>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * If you wish to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p/>
     * <p>The Intent filters used in {@link android.content.Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(android.content.Context, android.content.Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        switch (message) {
            case "onActivityResult":
                int requestCode = intent.getIntExtra("requestCode", -1);
                int resultCode = intent.getIntExtra("resultCode", -1);
                Intent data = intent.getParcelableExtra("data");
                onActivityResult(requestCode, resultCode, data);
                break;
            case "logoff":
                logoff();
                break;
            case "onCreate":

                break;
            case "onStart":

                break;
            case "onStop":

                break;
            case "revokeAccess":

                break;
            default:
                Log.e(TAG, "unexpected Message: " + message);
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

    public boolean isSignedIn() {
        return Session.getActiveSession() != null;
    }

    private class MyAsyncTask extends AsyncTask<GraphUser, Void, AxUser> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPostExecute(AxUser result) {
            //mContext.onLoginSuccess();
            Intent intent = new Intent("Authentication");
            intent.putExtra("Message", "Login Success");
            intent.putExtra("AxUser", result);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            mProgressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext,
                    "Loading...", "Data is Loading...");
        }

        @Override
        protected AxUser doInBackground(GraphUser... params) {
            try {
                GraphUser user = params[0];

                URI email = URI.create(user.asMap().get("email").toString());
                String name = user.getFirstName() + " " + user.getLastName();

                URL imgURL = new URL("http://graph.facebook.com/" + user.getId() + "/picture?type=large");
                Bitmap image = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());

                return new AxUser(name, image, email);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
