package mobi.MobiSeeker.sQueue.connection;

import android.os.Environment;

import com.samsung.chord.ChordManager;

import java.util.List;

public class ChordManagerService {

    protected ChordManager chordManager = null;

    public ChordManagerService(ChordManager chordManager) {
        this.chordManager = chordManager;
    }

    public List<Integer> getAvailableInterfaceTypes() {
        return this.chordManager.getAvailableInterfaceTypes();
    }

    public String getChordFilePath() {
        return Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Prescription";
    }
}
