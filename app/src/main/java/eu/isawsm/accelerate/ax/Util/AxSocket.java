package eu.isawsm.accelerate.ax.Util;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import Shared.Car;
import Shared.Driver;

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

    private List<Car> subscribedCars;


    public AxSocket(){
        subscribedCars = new ArrayList<>();
    }

    public  boolean isConnected(){
        return socket != null && socket.connected();
    }

    public  boolean tryConnect(String address, Emitter.Listener onConnectionSuccess, Emitter.Listener onConnectionError, Emitter.Listener onConnectionTimeout) {

        if(address.isEmpty() ){
            onConnectionError.call(address);
            return false;
        }
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
            e.printStackTrace();
            onConnectionError.call(e);
            return false;
        }
        return true;

    }

    public  void off() {
        if(socket != null)socket.off();
    }

    public void disconnect() {
        if(socket != null)
            socket.disconnect();
    }

    public void registerDriver(Driver driver) {
        socket.emit("registerDriver",  driver);
    }

    public void subscribeTo(Car car, Emitter.Listener callback) {
        socket.on("LapCompleted:"+car.getTransponderID(), callback);
        subscribedCars.add(car);
    }

    public void subscribeTo(Driver driver, Emitter.Listener callback){
        for(Car c : driver.getCars()){
            subscribeTo(c, callback);
        }
    }

    public void unsubscribe(Car car){
        socket.off("LapCompleted:"+car.getTransponderID());
        subscribedCars.remove(car);
    }

    public void unsubscribe(Driver driver){
        for(Car c: driver.getCars()){
            unsubscribe(c);
        }
    }

}
