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
import eu.isawsm.accelerate.ax.MainActivity;
import eu.isawsm.accelerate.ax.Util.AxPreferences;
import eu.isawsm.accelerate.ax.Util.AxSocket;
import eu.isawsm.accelerate.ax.viewmodel.ConnectionSetup;

/**
 * Created by ofade_000 on 21.02.2015.
 */
public class ConnectionViewHolder extends AxViewHolder {

    public static ConnectionSetup connectionSetup;
    Button bTestConnection;
    public MultiAutoCompleteTextView mAcTVServerAdress;

    public ConnectionViewHolder(View v, AxAdapter axAdapter, MainActivity context) {
        super(v, axAdapter, context);
        bTestConnection = (Button) v.findViewById(R.id.bTestConnection);
        mAcTVServerAdress = (MultiAutoCompleteTextView) v.findViewById(R.id.etServer);
    }


    public void onBindViewHolder(AxAdapter.ViewHolder holder, int position) {
        bTestConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTestConnectionClick(v);
            }
        });
        connectionSetup = (ConnectionSetup) axAdapter.getDataset().get(position).get();
    }

    public void onTestConnectionClick(View view) {
        String address = mAcTVServerAdress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            mAcTVServerAdress.setError(context.getResources().getString(R.string.invalid_address_error));
            mAcTVServerAdress.requestFocus();
            return;
        }
        context.initSocket(address);


    }

//    private Emitter.Listener onConnectionError = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            if (Looper.myLooper() == null) Looper.prepare();
//            context.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mAcTVServerAdress.setError(context.getResources().getString(R.string.could_not_connect_error));
//                    mAcTVServerAdress.requestFocus();
//                }
//            });
//        }
//    };
//
//    private Emitter.Listener onConnectionSuccess = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            showToast(context.getString(R.string.connectionsuccessful));
//            //Todo Test Club Card
//            AxPreferences.putServerAddress(context, AxSocket.getLastAddress());
//            AxCardItem clubCard = new AxCardItem<>(new Club("RCC Graphenwörth", URI.create("rcc.com"), null));
//
//            axAdapter.getDataset().add(0, clubCard);
//            axAdapter.getDataset().remove(getPosition());
//        }
//    };

}
