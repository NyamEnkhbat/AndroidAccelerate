package eu.isawsm.accelerate;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class GoogleAuthenticationUtil implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG  = "SignInActivity";

    private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

    public static final int STATUS_SIGNED_OUT = 10;
    public static final int STATUS_SING_IN_ERROR = 11;
    public static final int STATUS_SIGNING_IN = 12;
    public static final int STATUS_REVOKE_ACCESS = 13;
    public static final int STATUS_REVOKE_ACCESS_ERROR = 14;
    public static final int STATUS_SIGNED_IN = 15;
    public static final int STATUS_LOADING = 16;

    public static final int GOOGLE_PLUS_LOGIN_BUTTON_TAG = 21;
    public static final int GOOGLE_PLUS_LOGOUT_BUTTON_TAG = 22;
    public static final int GOOGLE_PLUS_REVOKE_BUTTON_TAG = 23;

    private GoogleApiClient mGoogleApiClient;

    public int currentStatus;

    /*
     * Stores the connection result from onConnectionFailed callbacks so that we can resolve them
     * when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;
    /*
     * Tracks whether the sign-in button has been clicked so that we know to resolve all issues
     * preventing sign-in without waiting.
     */
    private boolean mSignInClicked;
    /*
     * Tracks whether a resolution Intent is in progress.
     */
    private boolean mIntentInProgress;
    private Activity mContext;

    public GoogleAuthenticationUtil (Activity context) {
        mContext = context;

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Plus.API, Plus.PlusOptions.builder()
                        .addActivityTypes("http://schemas.google.com/AddActivity").build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        connect();
    }
    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View view) {
        switch((int)view.getTag()) {
            case GOOGLE_PLUS_LOGIN_BUTTON_TAG:
                if (!mGoogleApiClient.isConnecting()) {
                    int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
                    if (available != ConnectionResult.SUCCESS) {
                        createGoogleDialog().show();
                        return;
                    }

                    mSignInClicked = true;
                    currentStatus = STATUS_SIGNING_IN;
                    resolveSignInError();
                }
                break;
            case GOOGLE_PLUS_LOGOUT_BUTTON_TAG:
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.reconnect();
                }
                break;
            case GOOGLE_PLUS_REVOKE_BUTTON_TAG:
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    if (status.isSuccess()) {
                                        currentStatus = STATUS_REVOKE_ACCESS;
                                    } else {
                                        currentStatus = STATUS_REVOKE_ACCESS_ERROR;
                                    }
                                    mGoogleApiClient.reconnect();
                                }
                            }
                    );
                }
                break;
        }
    }


    protected Dialog createGoogleDialog() {

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, mContext, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
        }
        return new AlertDialog.Builder(mContext)
                .setMessage(R.string.plus_generic_error)
                .setCancelable(true)
                .create();
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CODE_SIGN_IN
                || requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
            mIntentInProgress = false; //Previous resolution intent no longer in progress.

            if (resultCode == Activity.RESULT_OK) {
                if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                    // Resolved a recoverable error, now try connect() again.
                    mGoogleApiClient.connect();
                }
            } else {
                mSignInClicked = false; // No longer in the middle of resolving sign-in errors.

                if (resultCode == Activity.RESULT_CANCELED) {
                    currentStatus = STATUS_SIGNED_OUT;
                } else {
                    currentStatus = STATUS_SING_IN_ERROR;
                    Log.w(TAG, "Error during resolving recoverable error.");
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String currentPersonName = person != null
                ? person.getDisplayName()
                : mContext.getString(R.string.unknown_person);
        currentStatus = STATUS_SIGNED_IN;
        //TODO save the person

    }

    @Override
    public void onConnectionSuspended(int cause) {
        currentStatus = STATUS_LOADING;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(mContext, REQUEST_CODE_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default state and
                // attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
                Log.w(TAG, "Error sending the resolution Intent, connect() again.");
            }
        }
    }
}
