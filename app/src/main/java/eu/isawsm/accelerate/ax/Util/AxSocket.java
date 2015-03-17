package eu.isawsm.accelerate.ax.Util;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;

import eu.isawsm.accelerate.Model.Car;
import eu.isawsm.accelerate.Model.Driver;

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

            socket.once("Welcome", onConnectionSuccess);
            socket.once(Socket.EVENT_ERROR, onConnectionError);
            socket.once(Socket.EVENT_CONNECT_ERROR, onConnectionError);
            socket.once(Socket.EVENT_CONNECT_TIMEOUT, onConnectionTimeout);

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

    public void disconnect() {
        socket.disconnect();
    }

    public void registerDriver(Driver driver, Emitter.Listener callback) {
        socket.once("registerDriverSuccess", callback);
        socket.emit("registerDriver",  new Gson().toJson(driver));
    }

    //public void registerCar(Car car) {
    //    socket.emit("registerNewTransponder", car);
    //}
}
