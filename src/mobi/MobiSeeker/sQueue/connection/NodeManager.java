package mobi.MobiSeeker.sQueue.connection;

import android.util.Log;

import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;
import com.samsung.chord.IChordChannelListener;

import java.util.List;

public class NodeManager {

    public static String CHORD_API_CHANNEL ="com.mobiseeker.squeue"; 
    	//	"mobi.MobiSeeker.sQueue";

    private static final String TAG = "[Chord][ApiService]";

    private static final String TAGClass = "NodeManager : ";

    protected ChordManager chordManager = null;

    protected IChordChannelListener chordChannelListener = null;

    protected ChannelInformation channelInformation = null;

    public NodeManager(ChordManager chordManager, IChordChannelListener chordChannelListener, ChannelInformation channelInformation) {
        this.chordManager = chordManager;

        this.chordChannelListener = chordChannelListener;

        this.channelInformation = channelInformation;
    }

    // Requests for nodes on the channel.
    public List<String> getJoinedNodeList(String channelName) {
        Log.d(TAG, TAGClass + "getJoinedNodeList()");

        if (this.chordManager == null) {
            Log.d(TAG, TAGClass + "getJoinedNodeList : chordManager is null.");
            return null;
        }

        // Request the channel interface for the specific channel name.
        IChordChannel channel = this.chordManager.getJoinedChannel(channelName);
        if (null == channel) {
            Log.e(TAG, TAGClass + "getJoinedNodeList : invalid channel instance-" + channelName);
            return null;
        }

        return channel.getJoinedNodeList();
    }

    /*
     * Set a keep-alive timeout. Node has keep-alive timeout. The timeoutMsec
     * determines the maximum keep-alive time to wait to leave when there is no
     * data from the nodes. Default time is 15000 millisecond.
     */
    public void setNodeKeepAliveTimeout(long timeoutMsec) {
        Log.d(TAG, TAGClass + "setNodeKeepAliveTimeout()");

        if (this.chordManager == null) {
            Log.d(TAG, TAGClass + "setNodeKeepAliveTimeout : chordManager is null.");
            return;
        }

        this.chordManager.setNodeKeepAliveTimeout(timeoutMsec);
    }

    // Get an IPv4 address that the node has.
    public String getNodeIpAddress(String channelName, String nodeName) {
        Log.d(TAG, TAGClass + "getNodeIpAddress() channelName : " + channelName + ", nodeName : "
                + nodeName);

        if (this.chordManager == null) {
            Log.d(TAG, TAGClass + "getNodeIpAddress : chordManager is null.");
            return null;
        }

        IChordChannel channel = this.chordManager.getJoinedChannel(channelName);
        if (null == channel){
            Log.e(TAG, TAGClass + "getNodeIpAddress : invalid channel instance");
            return null;
        }

        /*
         * @param nodeName The node name to find IPv4 address.
         * @return Returns an IPv4 Address.When there is not the node name in
         * the channel, null is returned.
         */
        return channel.getNodeIpAddress(nodeName);
    }

    // Request for joined channel interfaces.
    public List<IChordChannel> getJoinedChannelList() {
        Log.d(TAG, TAGClass + "getJoinedChannelList()");

        if (this.chordManager == null) {
            Log.d(TAG, TAGClass + "getJoinedChannelList : chordManager is null.");
            return null;
        }


        // @return Returns a list of handle for joined channel. It returns an
        // empty list, there is no joined channel.
        return this.chordManager.getJoinedChannelList();
    }

    public IChordChannel joinChannel(String channelName) {
        Log.d(TAG, TAGClass + "joinChannel()" + channelName);

        if (this.chordManager == null) {
            Log.d(TAG, TAGClass + "joinChannel : chordManager is null.");
            return null;
        }

        this.setChannelName(channelName);
        
        return this.chordManager.joinChannel(this.channelInformation.getPrivateChannel(), this.chordChannelListener);
    }

    // Leave a given channel.
    public void leaveChannel() {
        Log.d(TAG, TAGClass + "leaveChannel()");

        if (this.chordManager == null) {
            Log.d(TAG, TAGClass + "leaveChannel : chordManager is null.");
            return;
        }

        this.chordManager.leaveChannel(this.channelInformation.getPrivateChannel());

        this.channelInformation.setPrivateChannel("");
    }

    private void setChannelName(String channelName) {
        if (channelName == null || channelName.isEmpty()) {

            Log.e(TAG, TAGClass + "joinChannel > " + channelName
                    + " is invalid! Default private channel join");

            this.channelInformation.setPrivateChannel(CHORD_API_CHANNEL);
        } else {
            this.channelInformation.setPrivateChannel(channelName);
        }
    }

}
