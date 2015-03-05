package eu.isawsm.accelerate.ax.viewholders;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Clazz;
import eu.isawsm.accelerate.Model.Driver;
import eu.isawsm.accelerate.Model.Manufacturer;
import eu.isawsm.accelerate.Model.Model;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.AxAdapter;
import eu.isawsm.accelerate.ax.AxCardItem;
import eu.isawsm.accelerate.ax.MainActivity;
import eu.isawsm.accelerate.ax.Util.AxPreferences;

/**
 * Created by ofade_000 on 21.02.2015.
 */
public class CarSettingsViewHolder extends AxViewHolder {
    public AutoCompleteTextView tvManufacturer;
    public AutoCompleteTextView tvModel;
    public AutoCompleteTextView tvClass;
    public EditText etTransponder;
    public Button bSubmit;

    public CarSettingsViewHolder(View v, AxAdapter axAdapter, MainActivity context) {
        super(v, axAdapter, context);
        tvManufacturer = (AutoCompleteTextView) v.findViewById(R.id.acTvManufacturer);
        tvModel = (AutoCompleteTextView) v.findViewById(R.id.acTvModel);
        tvClass = (AutoCompleteTextView) v.findViewById(R.id.acTvClass);
        etTransponder = (EditText)v.findViewById(R.id.etTransponder);
        bSubmit = (Button) v.findViewById(R.id.bSubmit);

    }

    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position) {
        EditText[] inputs = {tvManufacturer,tvModel,tvClass,etTransponder};
        for(EditText et : inputs){
            et.setText("");
        }
        startUserInput();
    }


    private void onSubmit() {
        EditText[] inputs = {tvManufacturer,tvModel,tvClass,etTransponder};
        for(EditText et : inputs){
            if(!checkInput(et)) return;
        }
        addCar();
    }

    private void openKeyboard(View view) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, 0);
        }
    }

    private boolean checkInput(EditText v){
        if(v.getText().toString().trim().isEmpty()){
            v.setError("This cant be empty!");
            v.requestFocus();
            return false;
        }
        return  true;
    }

    private void addCar(){
        Driver driver = new Driver("DriverName", "", "", null, null);
        Manufacturer manufacturer = new Manufacturer(tvManufacturer.getText().toString(), null);
        Model model = new Model(manufacturer,tvModel.getText().toString(), "4WD", "17.5", "Touring Car", "1:10");
        Clazz clazz = new Clazz(tvClass.getText().toString(), "");
        long transponderID = Long.parseLong(etTransponder.getText().toString());
        Bitmap picture = null;

        Car car = new Car(driver, model, clazz, transponderID, picture);

        String carJson =  new Gson().toJson(car);

        AxPreferences.putSharedPreferencesCar(context, car);

        //TODO Notify Main Activity to do this
        axAdapter.getDataset().add(new AxCardItem<>(car));
        axAdapter.getDataset().remove(getPosition());

        try {
            //Send to Server
            if(socket == null || !socket.connected()) return;

            socket.emit("registerNewTransponder", carJson);

        } catch (java.lang.NumberFormatException e){
            e.printStackTrace();
            showToast("Transponder already in use.");
        }

    }

    public void startUserInput() {
        openKeyboard(tvManufacturer);
    }
}
