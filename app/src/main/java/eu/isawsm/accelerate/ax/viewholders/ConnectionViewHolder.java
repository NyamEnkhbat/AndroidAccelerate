package eu.isawsm.accelerate.ax.viewholders;

import android.app.Activity;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.github.nkzawa.emitter.Emitter;

import java.net.URI;

import eu.isawsm.accelerate.Model.Club;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.AxAdapter;
import eu.isawsm.accelerate.ax.AxCardItem;
import eu.isawsm.accelerate.ax.Util.AxPreferences;
import eu.isawsm.accelerate.ax.Util.AxSocket;

/**
 * Created by ofade_000 on 21.02.2015.
 */
public class ConnectionViewHolder extends AxViewHolder {


    private Button bTestConnection;
    private MultiAutoCompleteTextView mAcTVServerAdress;

    public ConnectionViewHolder(View v, AxAdapter axAdapter, Activity context) {
        super(v, axAdapter, context);

    }


    @Override
    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position) {
        bTestConnection = (Button) holder.mView.findViewById(R.id.bTestConnection);
        mAcTVServerAdress = (MultiAutoCompleteTextView) holder.mView.findViewById(R.id.etServer);
        bTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTestConnectionClick(v);
            }
        });
    }

    public void onTestConnectionClick(View view) {
        String address = mAcTVServerAdress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            mAcTVServerAdress.setText("Please enter a Valid address");
            return;
        }

        AxSocket.tryConnect(address, onConnectionSuccess, onConnectionError, onConnectionError);
        axAdapter.removeCard(getPosition());
    }

    private Emitter.Listener onConnectionError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (Looper.myLooper() == null) Looper.prepare();
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAcTVServerAdress.setError("Could not connect");
                    mAcTVServerAdress.requestFocus();
                }
            });
        }
    };

    private Emitter.Listener onConnectionSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            showToast(context.getString(R.string.connectionsuccessful));

            //Todo Test Club Card
            AxPreferences.putSharedPreferencesString(context, AxPreferences.AX_SERVER_ADDRESS, AxSocket.getLastAddress());

            AxCardItem clubCard = new AxCardItem<>(new Club("RCC Graphenwörth", URI.create("rcc.com"), null));
            axAdapter.getDataset().add(clubCard);

            axAdapter.removeCard(getPosition());

        }
    };

}
