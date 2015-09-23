package eu.isawsm.accelerate.ax;



import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.InetAddress;

import javax.jmdns.ServiceEvent;

import Shared.Car;
import Shared.Club;
import Shared.Driver;
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

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, TextToSpeech.OnInitListener {

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
    private Emitter.Listener onDriverRegistered = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("AxMainActivity", "Driver Registered " +  args[0].toString());
                        JSONObject jsonObject = (JSONObject) args[0];
            Driver driver = mGson.fromJson(jsonObject.toString(), Driver.class);



            if(driver.getCars() != null) {
                DumpDriver(driver);
                mUser.setCars(driver.getCars());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
            else {
                Log.e("AxMainActivity", "Merged Drivers cars are null!");
            }
        }
    };

    private void DumpDriver(Driver driver) {
        Log.d("AxMainActivity", "Got back driver " + driver);

        for(Car c : driver.getCars()){
            Log.d("AxMainActivity", c +"");
        }
    }

    private Emitter.Listener onConnectionSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("AxMainActivity","Connected to AxServer");
            JSONObject jsonObject = (JSONObject) args[0];
            final Club club = mGson.fromJson(jsonObject.toString(), Club.class);

            if (Looper.myLooper() == null) Looper.prepare();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AxPreferences.putServerAddress(getApplicationContext(), mSocket.getLastAddress());
                    AxCardItem clubCard = new AxCardItem<>(club);
                    if (mAdapter.getConnectionViewHolder() != null)
                        mDataset.remove(mAdapter.getConnectionViewHolder().getAdapterPosition());
                    mDataset.add(clubCard);
                    mSwipeLayout.setRefreshing(false);
                    updateCars();
               //     mSocket.registerDriver(mUser);

                }
            });
        }
    };
    private Emitter.Listener onConnectionError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("AxMainActivity", "Lost Connection to AxServer " + args[0]);
            mSocket.off();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initSocket();
                    ClubViewHolder clubVieHolder = mAdapter.getClubVieHolder();
                    if (clubVieHolder != null)
                        mDataset.remove(mAdapter.getClubVieHolder().getAdapterPosition());
                    mSwipeLayout.setRefreshing(false);
                }
            });
        }
    };
    private boolean isVoiceEnabled = false;
    private Emitter.Listener onConnectionTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("AxMainActivity","Connection Timeout to AxServer...");
            onConnectionError.call(args);
        }
    };



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ax_recycler);
        setSystemBarColor();
        setToolBar();
        initSwipeRefreshLayout();
        initRecyclerView();
        tts = new TextToSpeech(this, this);
        mUser = AxPreferences.getAxIUser(this) != null ? AxPreferences.getAxIUser(this) : new AxUser();

        if(mUser.getName() == null || mUser.getName().isEmpty()){
            Log.i("AxMainActivity", "Username Null or Empty, trying to get one from Device");
            mUser.setName(getUserName());
        }
        initSocket();
        updateCars();
    }

    private String getUserName(){
        Cursor c = null;
        try {
            c = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            c.moveToFirst();
            return (c.getString(c.getColumnIndex("display_name")));
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void initSocket() {
        initSocket(null);
        new ServerDetector(this).execute();
    }

    public void onServerDetected(InetAddress a, ServiceEvent ev)
    {
        Log.i("AxMainActivity","Server detected at " +a.getHostAddress() + ":" + ev.getInfo().getPort());
        if (mAdapter.getConnectionViewHolder() != null)
            mDataset.remove(mAdapter.getConnectionViewHolder().getLayoutPosition());
        initSocket(a.getHostAddress() + ":" + ev.getInfo().getPort());
    }

    public void initSocket(String address) {

        Log.i("AxMainActivity","Initializing Socket: " +address);
        if (address == null || address.isEmpty()) {
            Log.i("AxMainActivity", "Invalid Address, showing Connection Card...");
            showConnectionSetup();
            return;
        }

        mSocket = new AxSocket();

        if (!mSocket.isConnected()) {
            //Workaround to show the refreshing indicator
            TypedValue typed_value = new TypedValue();
            getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
            mSwipeLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
            mSwipeLayout.setRefreshing(true);

            mSocket.tryConnect(address, onConnectionSuccess, onConnectionError, onConnectionTimeout, onDriverRegistered);
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

    private void initSwipeRefreshLayout() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeLayout.setOnRefreshListener(this);
    }

    private void setSystemBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Real Material Design is only possible in Lollipop and later
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
                Log.i("AxMainActivity","Refreshing...");
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

    public void onCarSetupSubmit(View view) {
        CarSettingsViewHolder carSettingsViewHolder = mAdapter.getCarSettingsViewHolder();
        final Car car = carSettingsViewHolder.tryGetCar();
        if (car != null) {
            mDataset.remove(carSettingsViewHolder.getAdapterPosition());
            mDataset.add(new AxCardItem<>(car));
            mUser.addCar(car);
            AxPreferences.setAxIUser(this, mUser);
            subscribeToCar(car);
        }
    }


    private void subscribeToCar(final Car car) {
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
                        mAdapter.notifyItemChanged(mAdapter.getCarViewHolder(car).getAdapterPosition());
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
        if (address == null || address.isEmpty()) {
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
                if (isVoiceEnabled) item.setIcon(R.drawable.ic_lock_ringer_on_alpha);
                else item.setIcon(R.drawable.ic_lock_ringer_off_alpha);
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
        //That one is Deprecated but the new Method is not available in API 14
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
