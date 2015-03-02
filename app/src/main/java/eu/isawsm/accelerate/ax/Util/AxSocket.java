package eu.isawsm.accelerate.ax.Util;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

/**
 * Created by ofade_000 on 22.02.2015.
 */
public class AxSocket {
    private  Socket socket;

    public  String getLastAddress() {
        return lastAddress;
    }

    public  void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
    }

    private  String lastAddress;

    public  boolean isConnected(){
        return socket != null && socket.connected();
    }

    public  boolean tryConnect(String address, Emitter.Listener onConnectionSuccess, Emitter.Listener onConnectionError, Emitter.Listener onConnectionTimeout) {

        if(!address.startsWith("http"))
            address = "http://"+ address;

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
            onConnectionError.call(e);
            return false;
        }
        return true;

    }


    public  void off() {
        if(socket != null)socket.off();
    }
}
