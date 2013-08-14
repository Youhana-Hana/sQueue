package mobi.MobiSeeker.sQueue.connection;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class WakeLockManager {
    private static final String TAG = "[Chord][ApiService]";

    private static final String TAGClass = "WakeLockManager : ";

    protected PowerManager.WakeLock mWakeLock = null;

    protected Context context = null;

    public WakeLockManager(Context context) {
        this.context = context;
    }

    public void acquire() {
        if(null == mWakeLock) {
            PowerManager powerMgr = (PowerManager) this.context.getSystemService(Context.POWER_SERVICE);

            mWakeLock = powerMgr.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Chord Api Lock");
            Log.d(TAG, TAGClass + "acquire : new");
        }
        else if (mWakeLock.isHeld()) {
           Log.w(TAG, TAGClass + "acquire : already acquire");
                mWakeLock.release();
            }

        Log.d(TAG, TAGClass + "acquire : acquire");
        mWakeLock.acquire();
    }

    public void releaseWakeLock(){
        if(null != mWakeLock && mWakeLock.isHeld()){
            Log.d(TAG, TAGClass + "releaseWakeLock");
            mWakeLock.release();
        }
    }
}
