package eu.isawsm.accelerate.ax;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.R;

public class AxAdapter extends RecyclerView.Adapter<AxAdapter.AxViewHolder> {
    private ArrayList<Car> mDataset;

    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AxAdapter(ArrayList<Car> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position){
        return R.layout.ax_car_cardview;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AxAdapter.AxViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if(viewType == R.layout.ax_car_cardview) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ax_car_cardview, parent, false);
            // set the view's size, margins, paddings and layout parameters
            //...
            CarViewHolder vh = new CarViewHolder(v);
            return vh;
        } else {
            throw new RuntimeException("Cardview not supported");
        }
    }

    @Override
    public void onBindViewHolder(AxViewHolder holder, int position) {
        holder.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public abstract class AxViewHolder extends AxAdapter.ViewHolder{

        public AxViewHolder(View v) {
            super(v);
        }

        public abstract void onBindViewHolder(AxAdapter.ViewHolder holder, int position);
    }

    public class CarViewHolder extends AxViewHolder{

        public CarViewHolder(View v) {
            super(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView tfCarName = (TextView) holder.mView.findViewById(R.id.tfCarName);
            TextView tfConsistencyValue = (TextView) holder.mView.findViewById(R.id.tfConsistencyValue);

            TextView tfAvg = (TextView) holder.mView.findViewById(R.id.tfAvg);
            TextView tfBest = (TextView) holder.mView.findViewById(R.id.tfBest);
            TextView tfLaps = (TextView) holder.mView.findViewById(R.id.tfLaps);
            LineGraph lgGraph = (LineGraph) holder.mView.findViewById(R.id.linegraph);

            fillLineGraph(lgGraph);

            Car car = mDataset.get(position);

            tfCarName.setText(car.getFullName());
        }

        private void fillLineGraph(LineGraph lgGraph) {
            Line l = new Line();
            l.setUsingDips(true);
            LinePoint p = new LinePoint();
            p.setX(0);
            p.setY(5);
            p.setColor(context.getResources().getColor(R.color.colorPrimary));
            l.addPoint(p);
            p = new LinePoint();
            p.setX(8);
            p.setY(8);
            p.setColor(context.getResources().getColor(R.color.colorPrimary));
            l.addPoint(p);
            p = new LinePoint();
            p.setX(10);
            p.setY(4);
            p.setColor(context.getResources().getColor(R.color.colorPrimary));
            l.addPoint(p);

            l.setColor(context.getResources().getColor(R.color.colorPrimary));

            lgGraph.setUsingDips(true);
            lgGraph.addLine(l);
            lgGraph.setRangeY(0, 10);
            lgGraph.setLineToFill(0);
        }
    }

    public class ConnectionViewHolder extends  AxViewHolder {
        private SharedPreferences preferences;
        private Button bTestConnection;
        private  MultiAutoCompleteTextView mAcTVServerAdress;

        public ConnectionViewHolder(View v) {
            super(v);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);

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
            String message = mAcTVServerAdress.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                mAcTVServerAdress.setText("Please enter a Valid address");
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
                Socket socket = IO.socket(address);
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
                bTestConnection.setEnabled(true);
                bTestConnection.setText("Test Connection");
                mAcTVServerAdress.setEnabled(true);
                mAcTVServerAdress.setError("Connection Failed");
            }
        };


        private Emitter.Listener onConnectionSuccess = new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                //TODO Hide Card and Show the club cardview
            }
        };
        private void showToast(String s) {
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }
    }

}