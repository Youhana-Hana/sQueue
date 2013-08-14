package mobi.MobiSeeker.sQueue.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Settings {

    private Context context = null;

    public Settings(Context context) {
        this.context = context;
    }

    public String getUserName() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.context);

        return sharedPrefs.getString("prefUsername", "Guest");
    }

    public boolean isVibrate() {
        return this.getBooleanValue("prefVibrate", false);
    }

    public boolean isTextOnly() {
        return this.getBooleanValue("prefTextOnly", false);
    }
    
    public String getLogo() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.context);
        
        return sharedPrefs.getString("logo", null);
      }
    
    public void setLogo(String uri) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.context);

        Editor editor = sharedPrefs.edit();
        editor.putString("logo", uri);
        editor.commit();
    }

    
    private boolean getBooleanValue(String key, boolean defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.context);

        return sharedPrefs.getBoolean(key, defaultValue);
    }




}
