package eu.isawsm.accelerate.ax.viewholders;

import android.app.Activity;
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
import eu.isawsm.accelerate.ax.Util.AxPreferences;

/**
 * Created by ofade_000 on 21.02.2015.
 */
public class CarSettingsViewHolder extends AxViewHolder {
    private AutoCompleteTextView tvManufacturer;
    private AutoCompleteTextView tvModel;
    private AutoCompleteTextView tvClass;
    private EditText etTransponder;
    private Button bSubmit;

    public CarSettingsViewHolder(View v, AxAdapter axAdapter, Activity context) {
        super(v, axAdapter, context);
    }

    @Override
    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position) {
        tvManufacturer = (AutoCompleteTextView) holder.mView.findViewById(R.id.acTvManufacturer);
        tvModel = (AutoCompleteTextView) holder.mView.findViewById(R.id.acTvModel);
        tvClass = (AutoCompleteTextView) holder.mView.findViewById(R.id.acTvClass);
        etTransponder = (EditText) holder.mView.findViewById(R.id.etTransponder);
        bSubmit = (Button) holder.mView.findViewById(R.id.bSubmit);

        etTransponder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onSubmit();
                }
                return false;
            }
        });

        startUserInput();

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
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
        ArrayList<AxCardItem> retVal = new ArrayList<>();

        Driver driver = new Driver("DriverName", "", "", null, null);
        Manufacturer manufacturer = new Manufacturer(tvManufacturer.getText().toString(), null);
        Model model = new Model(manufacturer,tvModel.getText().toString(), "4WD", "17.5", "Touring Car", "1:10");
        Clazz clazz = new Clazz(tvClass.getText().toString(), "");
        long transponderID = Long.parseLong(etTransponder.getText().toString());
        Bitmap picture = null;

        Car car = new Car(driver, model, clazz, transponderID, picture);


        String carJson =  new Gson().toJson(car);

        AxPreferences.putSharedPreferencesCar(context, car);

        axAdapter.removeCard(getPosition());
        axAdapter.getDataset().add(getPosition(),new AxCardItem<>(car));

        axAdapter.notifyItemChanged(getPosition());

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
