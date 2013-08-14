package mobi.MobiSeeker.sQueue.connection;

import android.util.Log;

import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;

public class DataHandler {

    protected ChordManager chordManager;

    public DataHandler(ChordManager chordManager) {

        if (chordManager == null) {
            Log.e(ConnectionConstant.TAG, ConnectionConstant.TAGClass + "DataHandler() Invalid ChordManager. null value");
            throw new IllegalArgumentException("Invalid ChordManager. null value");
        }

        this.chordManager = chordManager;
    }

    // Send data message to the node.
    public boolean sendData(String toChannel, byte[] buf, String nodeName,String messagetype) {

        if (nodeName == null) {
            Log.v(ConnectionConstant.TAG, "sendData : NODE Name IS NULL !!");
            return false;
        }

        if (nodeName.isEmpty()) {
            Log.v(ConnectionConstant.TAG, "sendData : NODE Name IS empty !!");
            return false;
        }


        if (buf == null) {
            Log.v(ConnectionConstant.TAG, "sendData : buffer IS null !!");
            return false;
        }

        IChordChannel channel = this.chordManager.getJoinedChannel(toChannel);
        if (null == channel) {
            Log.e(ConnectionConstant.TAG, ConnectionConstant.TAGClass + "sendData : invalid channel instance");
            return false;
        }

        byte[][] payload = new byte[1][];
        payload[0] = buf;

        Log.v(ConnectionConstant.TAG, ConnectionConstant.TAGClass + "sendData : " + new String(buf) + ", des : " + nodeName);

        return channel.sendData(nodeName, messagetype, payload);

       }

    // Send data message to the all nodes on the channel.
    public boolean sendDataToAll(String toChannel, byte[] buf,String messageType) {

        if (buf == null) {
            Log.v(ConnectionConstant.TAG, "sendDataToAll : buffer IS null !!");
            return false;
        }

        // Request the channel interface for the specific channel name.
        IChordChannel channel = this.chordManager.getJoinedChannel(toChannel);
        if (null == channel) {
            Log.e(ConnectionConstant.TAG, ConnectionConstant.TAGClass + "sendDataToAll : invalid channel instance");
            return false;
        }

        byte[][] payload = new byte[1][];
        payload[0] = buf;

        Log.v(ConnectionConstant.TAG, ConnectionConstant.TAGClass + "sendDataToAll : " + new String(buf));

        return channel.sendDataToAll(messageType, payload);
    }
}
