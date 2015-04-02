package eu.isawsm.accelerate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.*;
import com.google.android.gms.common.GooglePlayServicesClient.*;
import com.google.android.gms.plus.PlusClient;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import eu.isawsm.accelerate.Model.AxUser;
import eu.isawsm.accelerate.ax.MainActivity;

public class GoogleAuthenticationUtil extends BroadcastReceiver implements IAuthenticator, View.OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "ExampleActivity";
    public static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    private MainActivity mContext;

    public GoogleAuthenticationUtil(MainActivity context) {
        mContext = context;
        LocalBroadcastManager.getInstance(mContext).registerReceiver(this,
                new IntentFilter("View"));
        mPlusClient = new PlusClient.Builder(mContext, this, this)
                .setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
                .build();

        // Anzuzeigende Statusmeldung, wenn der Verbindungsfehler nicht behoben ist
        mConnectionProgressDialog = new ProgressDialog(mContext);
        mConnectionProgressDialog.setMessage("Signing in...");
    }

    public GoogleAuthenticationUtil() {

    }

    private void onStart() {
        mPlusClient.connect();
    }

    private void onStop() {
        mPlusClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(mContext, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                mPlusClient.connect();
            }
        }
        // Speichern Sie das Ergebnis und beheben Sie den Verbindungsfehler bei einem Klick des Nutzers.
        mConnectionResult = result;
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == Activity.RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public boolean isSignedIn() {
        return mPlusClient.isConnected();
    }

    @Override
    public void logoff() {
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.disconnect();
            mPlusClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        new MyAsyncTask().execute(mPlusClient);

        mConnectionProgressDialog.dismiss();
        Toast.makeText(mContext, "User is connected!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button_g && !mPlusClient.isConnected()) {
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
                mPlusClient.connect();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(mContext, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // Versuchen Sie erneut, die Verbindung herzustellen.
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
            }
        } else {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(this);
        }
    }

    public void revokeAccess(){
        // Führen Sie vor dem Trennen der Verbindung clearDefaultAccount() aus.
        mPlusClient.clearDefaultAccount();

        mPlusClient.revokeAccessAndDisconnect(new PlusClient.OnAccessRevokedListener() {
            @Override
            public void onAccessRevoked(ConnectionResult status) {
                // mPlusClient ist jetzt getrennt und der Zugriff wurde widerrufen.
                // Lösen Sie App-Logik aus, um den Entwicklerrichtlinien zu entsprechen.
            }
        });
    }

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
                onStart();
                break;
            case "onStop":
                onStop();
                break;
            case "revokeAccess":
                revokeAccess();
                break;
            default:
                Log.e(TAG, "unexpected Message: " + message);
        }
    }

    private class MyAsyncTask extends AsyncTask<PlusClient, Void, AxUser> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPostExecute(AxUser result) {
            //mContext.onLoginSuccess();
            Intent intent = new Intent("Authentication");
            intent.putExtra("Message", "Login Success");
            intent.putExtra("AxUser", result);

            Log.d(TAG,result.getName());
            Log.d(TAG,result.getMail().toString());

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            mProgressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext,
                    "Loading...", "Data is Loading...");
        }

        @Override
        protected AxUser doInBackground(PlusClient... params) {
            try {
                PlusClient user = params[0];

                URI email = URI.create(user.getAccountName());
                String name = user.getCurrentPerson().getDisplayName();

                URL imgURL = new URL(user.getCurrentPerson().getImage().getUrl());
                Bitmap image = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());

                return new AxUser(name, image, email);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}