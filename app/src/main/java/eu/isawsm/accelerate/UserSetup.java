package eu.isawsm.accelerate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Clazz;
import eu.isawsm.accelerate.Model.Driver;
import eu.isawsm.accelerate.Model.Manufacturer;
import eu.isawsm.accelerate.Model.Model;


public class UserSetup extends Activity {

    private EditText etName;
    private AutoCompleteTextView acTvManufacturer;
    private AutoCompleteTextView acTvModel;
    private AutoCompleteTextView acTvClass;
    private EditText etTransponder;
    private Button bSubmit;
    private TextView tvErrorText;
    private Car car;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);

        etName = (EditText) findViewById(R.id.etName);
        acTvManufacturer = (AutoCompleteTextView) findViewById(R.id.acTvManufacturer);
        acTvModel = (AutoCompleteTextView) findViewById(R.id.acTvModel);
        acTvClass = (AutoCompleteTextView) findViewById(R.id.acTvClass);
        etTransponder = (EditText) findViewById(R.id.etTransponder);
        bSubmit = (Button) findViewById(R.id.bSubmit);
        tvErrorText = (TextView) findViewById(R.id.tvErrorText);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_setup, menu);
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
        try {
            getSocket().disconnect();
            getSocket().off();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onSubmitClick(View view) {
        try {

            //Create Objects
            car = createCarFromUserInputs();

            //Save in Application
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            String carJson = new Gson().toJson(car);
            editor.putString("Car", carJson);
            editor.apply();

            //Send to Server
            if(!getSocket().connected()) getSocket().connect();

            getSocket().on("registerTransponderSuccess", onRegisterTransponderSuccess);
            getSocket().emit("registerNewTransponder", carJson);

        } catch (java.lang.NumberFormatException e){
            e.printStackTrace();
            Toast.makeText(this,"Transponder ID not accepted",Toast.LENGTH_LONG).show();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(this,"Connection Error",Toast.LENGTH_LONG).show();
        }
    }

    private Emitter.Listener onRegisterTransponderSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String data = (String) args[0];
            System.out.println(data);
            if(data.equals(new Gson().toJson(car))) {
                if(Looper.myLooper() == null) Looper.prepare();
                showToast("Transponder Accepted");

                preferences.edit().putString("Car", data.toString());
                preferences.edit().apply();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    private Car createCarFromUserInputs() {
        Driver driver = new Driver(etName.getText().toString(), "", "", null, null);
        Clazz clazz = new Clazz(acTvClass.getText().toString().trim(), "");
        Manufacturer manufacturer = new Manufacturer(acTvManufacturer.getText().toString().trim(), null);
        Model model = new Model(manufacturer, acTvModel.getText().toString().trim(), "", "", "", "");

        long transponderId = Long.parseLong(etTransponder.getText().toString().trim());
        return new Car(driver, model, clazz, transponderId, null);
    }

    private void showToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
    private Socket socket;
    private Socket getSocket() throws URISyntaxException {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(socket == null) socket = IO.socket(preferences.getString("AxServerAddress", "http://192.168.1.7:3000"));
        return socket;
    }
}
