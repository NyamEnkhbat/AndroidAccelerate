package eu.isawsm.accelerate.ax;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

/**
 * Created by ofade on 10.09.2015.
 */
public class ServerDetector extends AsyncTask<Void, Void, String> {
    private Exception exception;
    private android.net.wifi.WifiManager.MulticastLock lock;
    private String type = "_AxNetConf._tcp.local.";
    private JmDNS jmdns;
    private ServiceListener listener;

    private MainActivity mainActivity;

    public ServerDetector(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void setUp() { // to be called by onCreate
        android.net.wifi.WifiManager wifi =
                (android.net.wifi.WifiManager)
                        mainActivity.getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("HeeereDnssdLock");
        lock.setReferenceCounted(true);
        lock.acquire();

    }

    protected String doInBackground(Void... urls) {

        final String[] retVal = {""};

        setUp();

        try {

            WifiManager wm = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);
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
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.onServerDetected(a, ev);
                                jmdns.removeServiceListener(type, listener);
                                try {
                                    jmdns.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
