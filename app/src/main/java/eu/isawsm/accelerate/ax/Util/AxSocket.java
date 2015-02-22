package eu.isawsm.accelerate.ax.Util;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

/**
 * Created by ofade_000 on 22.02.2015.
 */
public class AxSocket {
    private static Socket socket;

    public static String getLastAddress() {
        return lastAddress;
    }

    public static void setLastAddress(String lastAddress) {
        AxSocket.lastAddress = lastAddress;
    }

    private static String lastAddress;

    public static boolean isConnected(){
        return socket != null && socket.connected();
    }

    public static boolean tryConnect(String address, Emitter.Listener onConnectionSuccess, Emitter.Listener onConnectionError, Emitter.Listener onConnectionTimeout) {

        if(!address.startsWith("http"))
            address = "http://"+ address;

        //TODO put that outside this Class
        //AxPreferences.putSharedPreferencesString(context, AxPreferences.AX_SERVER_ADDRESS, address);

        try {
            setLastAddress(address);

            socket = IO.socket(address);
            socket.connect();

            socket.on("Welcome", onConnectionSuccess);
            socket.on(Socket.EVENT_ERROR, onConnectionError);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionTimeout);

            socket.emit("TestConnection", socket.id());
        } catch (URISyntaxException e) {
            return false;
        }
        return true;

    }


    public static void off() {
        socket.off();
    }
}
