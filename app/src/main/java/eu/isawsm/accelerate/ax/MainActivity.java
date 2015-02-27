package eu.isawsm.accelerate.ax;



import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import java.net.URI;
import java.util.ArrayList;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Clazz;
import eu.isawsm.accelerate.Model.Club;
import eu.isawsm.accelerate.Model.Driver;
import eu.isawsm.accelerate.Model.Manufacturer;
import eu.isawsm.accelerate.Model.Model;
import eu.isawsm.accelerate.R;
import eu.isawsm.accelerate.UserSetupDialogFragment;
import eu.isawsm.accelerate.ax.Util.AxPreferences;
import eu.isawsm.accelerate.ax.Util.AxSocket;
import eu.isawsm.accelerate.ax.viewholders.AxViewHolder;
import eu.isawsm.accelerate.ax.viewmodel.AxDataset;
import eu.isawsm.accelerate.ax.viewmodel.CarSetup;
import eu.isawsm.accelerate.ax.viewmodel.ConnectionSetup;
import eu.isawsm.accelerate.ax.viewmodel.Friends;

public class MainActivity extends ActionBarActivity  implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private AxAdapter axAdapter;
    private SwipeRefreshLayout swipeLayout;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ax_recycler);

        setSystemBarColor();
        setToolBar();
        initSwipeRefrehLayout();
        initRecyclerView();

        Cursor c = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        TextView tvStatus = (TextView) findViewById(R.id.tfStatus);
        tvStatus.setText(c.getString(c.getColumnIndex("display_name")));
        c.close();
        String Username = AxPreferences.getDriverName(this);
        if(Username == null){

            UserSetupDialogFragment nstantiate = (UserSetupDialogFragment) DialogFragment.instantiate(this, UserSetupDialogFragment.class.getName(), new Bundle());
            nstantiate.show(getFragmentManager(),"");
            nstantiate.setCancelable(false);
            Username = AxPreferences.getDriverName(this);

        }

        Driver driver = new Driver(Username,"","",null,null);
        tvStatus.setText(driver.getFirstname());

        if(!AxSocket.isConnected()) {
            String address = AxPreferences.getSharedPreferencesString(this, AxPreferences.AX_SERVER_ADDRESS,"");
            AxSocket.tryConnect(address, onConnectionSuccess, onConnectionError, onConnectionError);
        }
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.ax_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(true);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);

        axAdapter = new AxAdapter(this);
        axAdapter.setDataset(getCarsFromPreferences(axAdapter));


        mRecyclerView.setAdapter(axAdapter);


        mRecyclerView.setAdapter(axAdapter);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewWidth = mRecyclerView.getMeasuredWidth();
                        float cardViewWidth = getResources().getDimension(R.dimen.cardview_layout_width);
                        int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                        mLayoutManager.setSpanCount(newSpanCount);
                        mLayoutManager.requestLayout();
                    }
                });
    }

    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
    }

    private void initSwipeRefrehLayout() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setSystemBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AxAdapter.refresh();
                swipeLayout.setRefreshing(false);

            }
        });
    }

    private Emitter.Listener onConnectionError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            AxSocket.off();
            if (Looper.myLooper() == null) Looper.prepare();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showConnectionSetup();
                }
            });

        }
    };

    private Emitter.Listener onConnectionSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Todo Test Club Card
            if (Looper.myLooper() == null) Looper.prepare();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AxPreferences.putSharedPreferencesString(getApplicationContext(), AxPreferences.AX_SERVER_ADDRESS, AxSocket.getLastAddress());
                    AxCardItem clubCard = new AxCardItem<>(new Club("RCC Graphenwörth", URI.create("rcc.com"), null));
                    axAdapter.getDataset().add(0,clubCard);
                    mRecyclerView.scrollToPosition(0);
                }
            });

        }
    };
    private void showConnectionSetup() {
        axAdapter.getDataset().add(0, new AxCardItem<>(new ConnectionSetup()));
    }

    private AxDataset<AxCardItem> getCarsFromPreferences(AxAdapter adapter){
        AxDataset<AxCardItem> retVal = new AxDataset<>(adapter);

        for(Car car : AxPreferences.getSharedPreferencesCars(this)){
            retVal.add(new AxCardItem<>(car));
        }
        return retVal;
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
                axAdapter.addCarSetup();
                return true;
            case R.id.action_show_profile:
                axAdapter.showProfile();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
