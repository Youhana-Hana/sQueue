package mobi.MobiSeeker.sQueue.connection;

import com.samsung.chord.ChordManager;

public class NetworkListener implements ChordManager.INetworkListener{

    protected IChordServiceListener listener;

    public NetworkListener(IChordServiceListener listener) {
        this.listener = listener;
    }

    @Override
    public void onConnected(int interfaceType) {
        if (null == this.listener) {
            return;
        }

        this.listener.onConnectivityChanged();
    }

    @Override
    public void onDisconnected(int interfaceType) {
        if (null == this.listener) {
            return;
        }

        this.listener.onConnectivityChanged();
    }
}
