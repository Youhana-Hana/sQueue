package mobi.MobiSeeker.sQueue.connection;

import android.util.Log;

import com.samsung.chord.ChordManager;
import com.samsung.chord.IChordChannel;

public class FileHandler {

    private static final String TAG = "[Chord][ApiService]";

    private static final String TAGClass = "FileHandler : ";

    protected static final String MESSAGE_TYPE_FILE_NOTIFICATION = "FILE_NOTIFICATION_V2";

    protected static final long SHARE_FILE_TIMEOUT_MILISECONDS = 1000 * 60 * 5;

    protected static final long chunkTimeoutMsec = 30*1000;

    protected static final int chunkRetries = 2;

    protected static final long chunkSize = 300 * 1024;

    protected ChordManager chordManager;

    public FileHandler(ChordManager chordManager) throws IllegalArgumentException{

        if (chordManager == null) {
            Log.e(TAG, TAGClass + "FileHandler() Invalid ChordManager. null value");
            throw new IllegalArgumentException("Invalid ChordManager. null value");
        }

        this.chordManager = chordManager;
    }

    public String sendFile(String toChannel, String strFilePath, String toNode) {
        Log.d(TAG, TAGClass + "sendFile() ");

        IChordChannel channel = this.chordManager.getJoinedChannel(toChannel);
        if (null == channel) {
            Log.e(TAG, TAGClass + "sendFile() : invalid channel instance");
            return null;
        }

        return channel.sendFile(toNode, MESSAGE_TYPE_FILE_NOTIFICATION, strFilePath,
                SHARE_FILE_TIMEOUT_MILISECONDS);
    }

    public boolean acceptFile(String fromChannel, String exchangeId) {
        Log.d(TAG, TAGClass + "acceptFile()");

        IChordChannel channel = this.chordManager.getJoinedChannel(fromChannel);
        if (null == channel) {
            Log.e(TAG, TAGClass + "acceptFile() : invalid channel instance");
            return false;
        }

        return channel.acceptFile(exchangeId, FileHandler.chunkTimeoutMsec, FileHandler.chunkRetries, FileHandler.chunkSize);
    }

    // Cancel file transfer while it is in progress.
    public boolean cancelFile(String channelName, String exchangeId) {
        Log.d(TAG, TAGClass + "cancelFile()");
        // Request the channel interface for the specific channel name.
        IChordChannel channel = this.chordManager.getJoinedChannel(channelName);
        if (null == channel) {
            Log.e(TAG, TAGClass + "cancelFile() : invalid channel instance");
            return false;
        }

        return channel.cancelFile(exchangeId);
    }

    // Reject to receive file.
    public boolean rejectFile(String fromChannel, String coreTransactionId) {
        Log.d(TAG, TAGClass + "rejectFile()");
        // Request the channel interface for the specific channel name.
        IChordChannel channel = this.chordManager.getJoinedChannel(fromChannel);
        if (null == channel) {
            Log.e(TAG, TAGClass + "cancelFile() : invalid channel instance");
            return false;
        }

       return channel.rejectFile(coreTransactionId);
    }
}
