package mobi.MobiSeeker.sQueue.connection;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.samsung.chord.IChordChannel;
import com.samsung.chord.IChordChannelListener;
import com.samsung.chord.IChordManagerListener;
import com.samsung.chord.ChordManager;

public class ChordApiService extends Service {
    private static final String TAG = "[Chord][ApiService]";

    private static final String TAGClass = "ChordApiService : ";

    protected ChordManager chordManager = null;

    protected IChordServiceListener listener = null;

    protected IChordChannelListener channelListener = null;

    protected FileHandler fileHandler = null;

    protected DataHandler dataHandler = null;

    protected NodeManager nodeManager = null;

    protected WakeLockManager wakeLockManager = null;

    protected ChannelInformation channelInformation = null;

    protected NetworkListener networkListener = null;

    protected ChordManagerService chordManagerService = null;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAGClass + "onBind()");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, TAGClass + "onCreate()");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, TAGClass + "onDestroy()");
        super.onDestroy();
        try {
            release();
        } catch (Exception e) {
            Log.e(TAG, TAGClass, e);
        }
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, TAGClass + "onRebind()");
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAGClass + "onStartCommand()");
        return super.onStartCommand(intent, START_NOT_STICKY, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, TAGClass + "onUnbind()");
        return super.onUnbind(intent);
    }

    public class ChordServiceBinder extends Binder {
        public ChordApiService getService() {
            return ChordApiService.this;
        }
    }

    private final IBinder mBinder = new ChordServiceBinder();

    // Initialize chord
    public void initialize(IChordServiceListener listener) throws Exception {
        if (chordManager != null) {
            return;
        }

        // #1. GetInstance
        chordManager = ChordManager.getInstance(this);
        Log.d(TAG, TAGClass + "[Initialize] Chord Initialized");

        this.listener = listener;
        this.chordManagerService = new ChordManagerService(this.chordManager);

        this.channelListener = new ChordChannelListener(this.listener, this.fileHandler, this.chordManagerService);

        this.channelInformation = new ChannelInformation(chordManager);

        this.fileHandler = new FileHandler(this.chordManager);

        this.dataHandler = new DataHandler(this.chordManager);

        this.wakeLockManager = new WakeLockManager(this);

        this.networkListener = new NetworkListener(this.listener);

        this.chordManager.setNetworkListener(this.networkListener);


        this.nodeManager = new NodeManager(this.chordManager, channelListener, this.channelInformation);

        
        // #2. set some values before start
        chordManager.setTempDirectory(this.chordManagerService.getChordFilePath());
        chordManager.setHandleEventLooper(getMainLooper());
    }

    // Start chord
    public int start(int interfaceType) {

        this.wakeLockManager.acquire();
        // #3. set some values before start
        return chordManager.start(interfaceType, new IChordManagerListener() {
            @Override
            public void onStarted(String name, int reason) {
                Log.d(TAG, TAGClass + "onStarted chord");

                if (null != listener)
                    listener.onUpdateNodeInfo(name, chordManager.getIp());

                if (STARTED_BY_RECONNECTION == reason) {
                    Log.e(TAG, TAGClass + "STARTED_BY_RECONNECTION");
                    return;
                }
                // if(STARTED_BY_USER == reason) : Returns result of calling
                // start

                // #4.(optional) listen for public channel
                IChordChannel channel = chordManager.joinChannel(NodeManager.CHORD_API_CHANNEL,
                        channelListener);
                

                if (null == channel) {
                    Log.e(TAG, TAGClass + "fail to join public");
                }
            }

            @Override
            public void onNetworkDisconnected() {
                Log.e(TAG, TAGClass + "onNetworkDisconnected()");
                if (null != listener)
                    listener.onNetworkDisconnected();
            }

            @Override
            public void onError(int error) {
                // TODO Auto-generated method stub
            }
        });
    }

    // Stop chord
    public void stop() {
        Log.d(TAG, TAGClass + "stop()");
        this.wakeLockManager.releaseWakeLock();
        if (chordManager != null) {
            chordManager.stop();
        }
    }

    // Release chord
    public void release() throws Exception {
        if (chordManager != null) {
            chordManager.stop();
            chordManager.setNetworkListener(null);
            chordManager = null;
            Log.d(TAG, "[UNREGISTER] Chord unregistered");
        }

    }


    public String getPublicChannel() {
        return this.channelInformation.getPublicChannel();
    }

    public String getPrivateChannel() {
        return this.channelInformation.getPrivateChannel();
    }

    public String sendFile(String toChannel, String strFilePath, String toNode) {
        return fileHandler.sendFile(toChannel, strFilePath, toNode);
    }

    public boolean acceptFile(String fromChannel, String exchangeId) {
        return fileHandler.acceptFile(fromChannel, exchangeId);
    }

    public boolean cancelFile(String channelName, String exchangeId) {
        return fileHandler.cancelFile(channelName, exchangeId);
    }

    public boolean rejectFile(String fromChannel, String coreTransactionId) {
        return fileHandler.rejectFile(fromChannel, coreTransactionId);
    }

    public boolean sendData(String toChannel, byte[] buf, String nodeName,String messageType) {
        return this.dataHandler.sendData(toChannel, buf, nodeName,messageType);
    }

    public boolean sendDataToAll(String toChannel, byte[] buf,String messageType) {
        return this.dataHandler.sendDataToAll(toChannel, buf,messageType);
    }

    public List<String> getJoinedNodeList(String channelName) {
        return this.nodeManager.getJoinedNodeList(channelName);
    }

    public void setNodeKeepAliveTimeout(long timeoutMsec) {
        this.nodeManager.setNodeKeepAliveTimeout(timeoutMsec);
    }

    public String getNodeIpAddress(String channelName, String nodeName) {
        return this.nodeManager.getNodeIpAddress(channelName, nodeName);
    }

    public List<IChordChannel> getJoinedChannelList() {
        return this.nodeManager.getJoinedChannelList();
    }

    public IChordChannel joinChannel(String channelName) {
        return this.nodeManager.joinChannel(channelName);
    }

    public void leaveChannel() {
        this.nodeManager.leaveChannel();
    }

    public List<Integer> getAvailableInterfaceTypes() {
        return chordManagerService.getAvailableInterfaceTypes();
    }
}