package eu.isawsm.accelerate.ax;


import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import Shared.Car;
import Shared.Club;
import Shared.Lap;
import eu.isawsm.accelerate.Model.AxUser;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.ax.Util.AxPreferences;
import eu.isawsm.accelerate.ax.Util.AxSocket;
import eu.isawsm.accelerate.ax.viewholders.CarSettingsViewHolder;
import eu.isawsm.accelerate.ax.viewholders.ClubViewHolder;
import eu.isawsm.accelerate.ax.viewholders.ConnectionViewHolder;
import eu.isawsm.accelerate.ax.viewmodel.AxDataset;
import eu.isawsm.accelerate.ax.viewmodel.ConnectionSetup;

public class MainActivity extends ActionBarActivity  implements SwipeRefreshLayout.OnRefreshListener, TextToSpeech.OnInitListener  {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private AxAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private AxSocket mSocket;
    private AxDataset<AxCardItem> mDataset;
    private TextToSpeech tts;
    private Gson mGson = new Gson();
    //TODO for now we use the User just to hold our cars.
    private AxUser mUser;
    private int retryCount = 0;
    private Emitter.Listener onConnectionSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            retryCount = 0;
            JSONObject jsonObject = (JSONObject) args[0];
            final Club club = mGson.fromJson(jsonObject.toString(), Club.class);
            //Do i realy need to run this on UI thread?
            if (Looper.myLooper() == null) Looper.prepare();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AxPreferences.putServerAddress(getApplicationContext(), mSocket.getLastAddress());
                    AxCardItem clubCard = new AxCardItem<>(club);
                    if (mAdapter.getConnectionViewHolder() != null)
                        mDataset.remove(mAdapter.getConnectionViewHolder().getPosition());
                    mDataset.add(clubCard);
                    mSwipeLayout.setRefreshing(false);
                    updateCars();

                }
            });
        }
    };
    private Emitter.Listener onConnectionError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.off();
            if (Looper.myLooper() == null) Looper.prepare();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showConnectionSetup();
                    ClubViewHolder clubVieHolder = mAdapter.getClubVieHolder();
                    if (clubVieHolder != null)
                        mDataset.remove(mAdapter.getClubVieHolder().getPosition());
                    mSwipeLayout.setRefreshing(false);
                }
            });

            if(retryCount < 15) {

                if(Looper.myLooper() == null) Looper.prepare();
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSocket.isConnected()) return;
                        retryCount ++;
                        System.out.println("Connection Error #" + retryCount + "/15 retrying...");
                        mSocket.tryConnect(AxPreferences.getServerAddress(mRecyclerView.getContext()), onConnectionSuccess, onConnectionError, onConnectionTimeout);
                    }
                }, 1000);

            }


        }
    };
    private boolean isVoiceEnabled = false;
    private Emitter.Listener onConnectionTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(retryCount > 15) {
                onConnectionError.call(args);
                return;
            }

            retryCount ++;
            System.out.println("Timeout #" + retryCount + "/15 retrying...");
            mSocket.tryConnect(AxPreferences.getServerAddress(mRecyclerView.getContext()), onConnectionSuccess, onConnectionError, onConnectionTimeout);

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.ax_recycler);
            setSystemBarColor();
            setToolBar();
            initSwipeRefrehLayout();
            initRecyclerView();
            tts = new TextToSpeech(this, this);
            mUser = AxPreferences.getAxIUser(this) != null ? AxPreferences.getAxIUser(this) : new AxUser();

            initSocket();
        updateCars();

    }


    public void initSocket()  {

            new netconfTask().execute();

    }

    private class netconfTask extends AsyncTask<Void, Void, String> {

        private Exception exception;
        private android.net.wifi.WifiManager.MulticastLock lock;
        private String type = "_AxNetConf._tcp.local.";
        private JmDNS jmdns;
        private ServiceListener listener;


        private void setUp() { // to be called by onCreate
            android.net.wifi.WifiManager wifi =
                    (android.net.wifi.WifiManager)
                            getSystemService(android.content.Context.WIFI_SERVICE);
            lock = wifi.createMulticastLock("HeeereDnssdLock");
            lock.setReferenceCounted(true);
            lock.acquire();

        }

        protected String doInBackground(Void... urls) {

            final String[] retVal = {""};

            setUp();

            try {

                WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                InetAddress deviceIpAddress = InetAddress.getByName(ip);

                jmdns = JmDNS.create(deviceIpAddress, ip);

                jmdns.addServiceListener(type, listener = new ServiceListener() {
                    public void serviceResolved(final ServiceEvent ev) {
                        Log.i("AxNetConf", "Service resolved: "
                                + ev.getInfo().getQualifiedName()
                                + " port:" + ev.getInfo().getPort());
                        for (final InetAddress a : ev.getInfo().getInetAddresses()) {
                            Log.i("AxNetConf", a.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSocket(a.getHostAddress() + ":" + ev.getInfo().getPort());
                                }
                            });
                        }
                    }

                    public void serviceRemoved(ServiceEvent ev) {
                        Log.i("AxNetConf", "Service removed: " + ev.getName());
                    }

                    public void serviceAdded(ServiceEvent event) {
                        // Required to force serviceResolved to be called again
                        // (after the first search)
                        jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
                    }

                });

            //    jmdns.removeServiceListener(type, listener);
              //  jmdns.close();

                return retVal[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void ignored) {
            if (lock != null) lock.release();
        }
    }

    public void initSocket(String address) {
        if (address == null ||  address.isEmpty()){
            showConnectionSetup();
            return;
        }

        mSocket = new AxSocket();
        retryCount = 0;

        if (!mSocket.isConnected()) {
            //Workaround to show the refreshing indicator
            TypedValue typed_value = new TypedValue();
            getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
            mSwipeLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
            mSwipeLayout.setRefreshing(true);

            mSocket.tryConnect(address, onConnectionSuccess, onConnectionError, onConnectionTimeout);
        }
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.ax_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AxAdapter(this);
        mDataset = new AxDataset<>(mAdapter);
        mAdapter.setDataset(mDataset);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                        int viewWidth = mRecyclerView.getMeasuredWidth();
                        float cardViewWidth = getResources().getDimension(R.dimen.cardview_layout_width);
                        int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                        mLayoutManager.setSpanCount(newSpanCount);
                        mLayoutManager.requestLayout();
                    }
                });
    }

    private void setToolBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
    }

    private void initSwipeRefrehLayout() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeLayout.setOnRefreshListener(this);
    }

    private void setSystemBarColor() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Real Material Design is only possible in Lollipop and later
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onRefresh() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.refresh();
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    private void showConnectionSetup() {
        mSwipeLayout.setRefreshing(false);
        if (!mDataset.add(new AxCardItem<>(new ConnectionSetup()))) {
            mAdapter.getConnectionViewHolder().mAcTVServerAdress.setError(getResources().getString(R.string.invalid_address_error));
        }
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
        super.onDestroy();

    }

    //TODO Move this out of here
    public void onCarSetupSubmit(View view) {
        CarSettingsViewHolder carSettingsViewHolder = mAdapter.getCarSettingsViewHolder();
        final Car car = carSettingsViewHolder.tryGetCar();
        if (car != null) {
            mDataset.remove(carSettingsViewHolder.getPosition());
            mDataset.add(new AxCardItem<>(car));
            mUser.addCar(car);
            AxPreferences.setAxIUser(this, mUser);
            subscribeToCar(car);
        }
    }


    private void subscribeToCar(final Car car){
        if (mSocket == null || !mSocket.isConnected()) return;
        mSocket.subscribeTo(car, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
              //  JSONObject jsonObject = (JSONObject) args[0];
                final Lap lap = new Gson().fromJson(args[0].toString(), Lap.class);

                if (isVoiceEnabled) {
                    speakTime(lap);
                }
                car.addLap(lap);
                if (Looper.myLooper() == null) Looper.prepare();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemChanged(mAdapter.getCarViewHolder(car).getPosition());
                    }
                });

            }
        });
    }

    private void speakTime(Lap lap) {
        String lapTime = lap.getTime() / 1000d + "";
        while (lapTime.length() < 6) {
            lapTime += "0";
        }
        String[] times = lapTime.split("\\.");

        speakOut(times[0] + "  " + times[1].charAt(0) + " " + times[1].charAt(1));
    }

    public void onTestConnection(View view) {
        ConnectionViewHolder connectionViewHolder = mAdapter.getConnectionViewHolder();
        String address = connectionViewHolder.tryGetAddress();
        if (address == null || address.isEmpty()){
            showConnectionSetup();
        }
        initSocket(address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_car:
                mAdapter.addCarSetup();
                return true;

            case R.id.action_voice:
                item.setChecked(!item.isChecked());
                isVoiceEnabled = !item.isChecked();
                if(isVoiceEnabled)   item.setIcon(R.drawable.ic_lock_ringer_on_alpha);
                else  item.setIcon(R.drawable.ic_lock_ringer_off_alpha);
                return true;

            case R.id.action_settings:
                Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(openSettingsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateCars() {
        //      mAdapter.removeAllCars();
        for (Car c : mUser.getCars()) {
            mDataset.add(new AxCardItem<>(c));
            subscribeToCar(c);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AxPreferences.setAxIUser(this, mUser);
    }

    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
     *
     * @param status {@link android.speech.tts.TextToSpeech#SUCCESS} or {@link android.speech.tts.TextToSpeech#ERROR}.
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(getResources().getConfiguration().locale);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
