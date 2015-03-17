package eu.isawsm.accelerate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import eu.isawsm.accelerate.Model.Driver;
import eu.isawsm.accelerate.ax.MainActivity;
import eu.isawsm.accelerate.ax.Util.AxPreferences;

/**
 * Created by olfad on 26.02.2015.
 */
public class UserSetupDialogFragment extends DialogFragment {

    public UserSetupDialogFragment(){
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        setRetainInstance(true);
        View inflate = inflater.inflate(R.layout.dialog_drivername, null);
        final EditText etDrivername = (EditText) inflate.findViewById(R.id.username);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflate)
                // Add action buttons
                .setPositiveButton(getActivity().getString(R.string.drivernamedialogsubmit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(etDrivername.getText().toString().trim().isEmpty()){
                            etDrivername.setError(getActivity().getString(R.string.please_enter_your_full_name));
                            return;
                        }
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.setDriver(new Driver(etDrivername.getText().toString().trim(),"","",null,null, getActivity()));
                    }
                });

        return builder.create();
    }
}
