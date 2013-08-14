package mobi.MobiSeeker.sQueue.connection;

import com.samsung.chord.ChordManager;

public class ChannelInformation {

    protected ChordManager chordManager;

    private String channelName;

    public ChannelInformation(ChordManager chordManager) {
        this.chordManager = chordManager;
    }

    public String getPublicChannel() {
        return NodeManager.CHORD_API_CHANNEL;
    }

    public String getPrivateChannel() {
        return NodeManager.CHORD_API_CHANNEL;
    }

    public void setPrivateChannel(String channelName) {
        this.channelName = channelName;
    }
}
