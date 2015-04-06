package eu.isawsm.accelerate.ax.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Clazz;
import eu.isawsm.accelerate.Model.Manufacturer;
import eu.isawsm.accelerate.Model.Model;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.AxAdapter;
import eu.isawsm.accelerate.ax.AxCardItem;
import eu.isawsm.accelerate.ax.MainActivity;

/**
 * Created by ofade_000 on 21.02.2015.
 */
public class CarSettingsViewHolder extends AxViewHolder {
    public AutoCompleteTextView tvManufacturer;
    public AutoCompleteTextView tvModel;
    public AutoCompleteTextView tvClass;
    public EditText etTransponder;
    public Button bSubmit;
    private Car car;

    public CarSettingsViewHolder(View v, AxAdapter axAdapter, MainActivity context) {
        super(v, axAdapter, context);
        tvManufacturer = (AutoCompleteTextView) v.findViewById(R.id.acTvManufacturer);
        tvModel = (AutoCompleteTextView) v.findViewById(R.id.acTvModel);
        tvClass = (AutoCompleteTextView) v.findViewById(R.id.acTvClass);
        etTransponder = (EditText)v.findViewById(R.id.etTransponder);
        bSubmit = (Button) v.findViewById(R.id.bSubmit);
    }

    public CarSettingsViewHolder(View v, AxAdapter axAdapter, MainActivity context, Car car) {
        super(v, axAdapter, context);
        tvManufacturer = (AutoCompleteTextView) v.findViewById(R.id.acTvManufacturer);
        tvModel = (AutoCompleteTextView) v.findViewById(R.id.acTvModel);
        tvClass = (AutoCompleteTextView) v.findViewById(R.id.acTvClass);
        etTransponder = (EditText)v.findViewById(R.id.etTransponder);
        bSubmit = (Button) v.findViewById(R.id.bSubmit);
        this.car = car;
    }

    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position, AxCardItem axCardItem) {
        tvManufacturer.setText(car == null ? "" : car.getModel().getManufacturer().getName());
        tvModel.setText(car == null ? "" : car.getModel().getName());
        tvClass.setText(car == null ? "" : car.getClass().getName());
        etTransponder.setText(car == null ? "" : car.getTransponderID()+"");
        openKeyboard(tvManufacturer);
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

    private Car createCar(){
        Manufacturer manufacturer = new Manufacturer(tvManufacturer.getText().toString(), null);
        Model model = new Model(manufacturer,tvModel.getText().toString(), "", "", "", "");
        Clazz clazz = new Clazz(tvClass.getText().toString(), "");
        long transponderID = Long.parseLong(etTransponder.getText().toString());
        Bitmap picture = null;

        return new Car(model, clazz, transponderID, picture);
    }

    public Car tryGetCar() {
        EditText[] inputs = {tvManufacturer,tvModel,tvClass,etTransponder};
        for(EditText et : inputs){
            if(!checkInput(et)) return null;
        }
        return createCar();
    }
}
