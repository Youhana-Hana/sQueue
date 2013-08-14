package mobi.MobiSeeker.sQueue.connection;

public interface IChordServiceListener {
    public static final int SENT = 0;

    public static final int RECEIVED = 1;

    public static final int CANCELLED = 2;

    public static final int REJECTED = 3;

    public static final int FAILED = 4;
    
    


    void onReceiveMessage(String node, String channel, String message,String MessageType);

    void onFileWillReceive(String node, String channel, String fileName, String exchangeId);

    void onFileProgress(boolean bSend, String node, String channel, int progress,
                        String exchangeId);

    void onFileCompleted(int reason, String node, String channel, String exchangeId,
                         String fileName);

    void onNodeEvent(String node, String channel, boolean bJoined);

    void onNetworkDisconnected();

    void onUpdateNodeInfo(String nodeName, String ipAddress);

    void onConnectivityChanged();
}
