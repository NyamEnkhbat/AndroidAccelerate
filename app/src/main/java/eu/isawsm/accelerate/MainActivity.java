package eu.isawsm.accelerate;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import eu.isawsm.accelerate.Model.Driver;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        mSocket.disconnect();
        mSocket.off();
    }

    public void onSubmitClick(View view){
        EditText name = (EditText) findViewById(R.id.etName);
        mSocket.connect();

        String message = name.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        name.setText("");

        mSocket.emit("new message", message);

        mSocket.on("", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });

//       /
    }
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://raspberrypi/axilerate/index.js");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
