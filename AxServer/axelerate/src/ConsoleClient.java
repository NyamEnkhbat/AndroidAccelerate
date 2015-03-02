import com.skoky.P3tools.MsgProcessor;
import com.skoky.Ptools.Message;


/**
 * Created by Oliver on 06.02.2015.
 */
public class ConsoleClient {
    public static void main (String[] args){
        byte[] mb = Hex.decodeHex("8e:00:1b:00:99:35:00:00:12:00:01:00:02:00:03:00:04:00:05:00:81:04:75:0e:04:00:8f".replaceAll(":", "").toCharArray());
        MsgProcessor mp = new MsgProcessor(true);
        Message m = mp.parse(mb);
        System.out.println("Message type:"+m.getType().getName());
        System.out.println("Message JSON:"+m.toString());yte[] mb = Hex.decodeHex("8e:00:1b:00:99:35:00:00:12:00:01:00:02:00:03:00:04:00:05:00:81:04:75:0e:04:00:8f".replaceAll(":", "").toCharArray());
        MsgProcessor mp = new MsgProcessor(true);
        Message m = mp.parse(mb);
        System.out.println("Message type:"+m.getType().getName());
        System.out.println("Message JSON:"+m.toString());
    }
}
