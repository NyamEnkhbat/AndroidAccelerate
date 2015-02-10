package eu.isawsm.accelerate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class ServerConfigActivity extends Activity {
    private Button bTestConnection;
    private MultiAutoCompleteTextView mAcTVServerAdress;
    private TextView textView;
    private ProgressBar progressBar;
    private int DEFAULT_PORT = 3000;
    private Socket socket;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_config);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        bTestConnection = (Button) findViewById(R.id.bTestConnection);
        mAcTVServerAdress = (MultiAutoCompleteTextView) findViewById(R.id.etServer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off();
    }

    public void onSubmitClick(View view){


        String message = mAcTVServerAdress.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            textView.setText("Please enter a Valid address");
            return;
        }
        tryConnect(message);
        bTestConnection.setEnabled(false);
        mAcTVServerAdress.setEnabled(false);
        bTestConnection.setText("Connecting...");


    }

    public boolean tryConnect(String address) {

        if(!address.startsWith("http"))
            address = "http://"+ address;


        preferences.edit().putString("AxServerAddress", address).apply();
        try {
            System.out.println(address);
            socket = IO.socket(address);
            socket.connect();

            socket.on("Welcome", onConnectionSuccess);
            socket.on(Socket.EVENT_ERROR, onConnectionError);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionError);

            socket.emit("TestConnection", socket.id());
        } catch (URISyntaxException e) {
            return false;
        }
        return true;

    }
    private Emitter.Listener onConnectionError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bTestConnection.setEnabled(true);
                    bTestConnection.setText("Test Connection");
                    mAcTVServerAdress.setEnabled(true);
                    mAcTVServerAdress.setError("Connection Failed");
                }
            });
        }
    };


    private Emitter.Listener onConnectionSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            if(Looper.myLooper() == null) Looper.prepare();

            showToast("Successfully connected!");
            finish();
        }
    };
    private void showToast(String s) {
        Toast.makeText(this, s,Toast.LENGTH_LONG).show();
    }
}
