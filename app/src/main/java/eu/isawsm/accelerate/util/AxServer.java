package eu.isawsm.accelerate.util;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by olfad on 05.02.2015.
 */
public class AxServer {

    private static AxServer instance;
    private static int DEFAULT_PORT = 3000;
    private static Socket socket;

    private AxServer () {}

    public static AxServer getInstance () {
        if (AxServer.instance == null) {
            AxServer.instance = new AxServer ();
        }
        return AxServer.instance;
    }

    public static boolean tryConnect(String address){

        if(!address.startsWith("http"))
            address = "http://"+ address;

        if(!address.contains(":"))
            address += ":"+DEFAULT_PORT;

        try {
            socket = IO.socket(address);
        } catch (URISyntaxException e) {
            return false;
        }
        return true;

    }


}
