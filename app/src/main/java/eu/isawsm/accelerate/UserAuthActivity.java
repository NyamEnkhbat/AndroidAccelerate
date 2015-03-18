package eu.isawsm.accelerate;/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
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
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class UserAuthActivity implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG  = "SignInActivity";

    private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

    private TextView mSignInStatus;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;
    private Toolbar mToolbar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ax_user_auth);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, Plus.PlusOptions.builder()
                        .addActivityTypes("http://schemas.google.com/AddActivity").build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mSignInStatus = (TextView) findViewById(R.id.status);
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button_g);
        mSignInButton.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.sign_in_button_g:
                if (!mGoogleApiClient.isConnecting()) {
                    int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                    if (available != ConnectionResult.SUCCESS) {
                        showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
                        return;
                    }

                    mSignInClicked = true;
                    mSignInStatus.setText(getString(R.string.signing_in_status));
                    resolveSignInError();
                }
                break;
//            case R.id.sign_out_button:
//                if (mGoogleApiClient.isConnected()) {
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//                    mGoogleApiClient.reconnect();
//                }
//                break;
//            case R.id.revoke_access_button:
//                if (mGoogleApiClient.isConnected()) {
//                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient).setResultCallback(
//                            new ResultCallback<Status>() {
//                                @Override
//                                public void onResult(Status status) {
//                                    if (status.isSuccess()) {
//                                        mSignInStatus.setText(R.string.revoke_access_status);
//                                    } else {
//                                        mSignInStatus.setText(R.string.revoke_access_error_status);
//                                    }
//                                    mGoogleApiClient.reconnect();
//                                }
//                            }
//                    );
//                    updateButtons(false /* isSignedIn */);
//                }
//                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
            return super.onCreateDialog(id);
        }

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
        }
        return new AlertDialog.Builder(this)
                .setMessage(R.string.plus_generic_error)
                .setCancelable(true)
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SIGN_IN
                || requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
            mIntentInProgress = false; //Previous resolution intent no longer in progress.

            if (resultCode == RESULT_OK) {
                if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                    // Resolved a recoverable error, now try connect() again.
                    mGoogleApiClient.connect();
                }
            } else {
                mSignInClicked = false; // No longer in the middle of resolving sign-in errors.

                if (resultCode == RESULT_CANCELED) {
                    mSignInStatus.setText(getString(R.string.signed_out_status));
                } else {
                    mSignInStatus.setText(getString(R.string.sign_in_error_status));
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
                : getString(R.string.unknown_person);
        mSignInStatus.setText(getString(R.string.signed_in_status, currentPersonName));
        updateButtons(true /* isSignedIn */);
        mSignInClicked = false;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mSignInStatus.setText(R.string.loading_status);
        mGoogleApiClient.connect();
        updateButtons(false /* isSignedIn */);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
            updateButtons(false /* isSignedIn */);
        }
    }

    private void updateButtons(boolean isSignedIn) {
        if (isSignedIn) {
            mSignInButton.setVisibility(View.INVISIBLE);
        } else {
            if (mConnectionResult == null) {
                // Disable the sign-in button until onConnectionFailed is called with result.
                mSignInButton.setVisibility(View.INVISIBLE);
                mSignInStatus.setText(getString(R.string.loading_status));
            } else {
                // Enable the sign-in button since a connection result is available.
                mSignInButton.setVisibility(View.VISIBLE);
                mSignInStatus.setText(getString(R.string.signed_out_status));
            }
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
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
