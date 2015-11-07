package bwagjor.microscope;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by benja_000 on 21/08/2015.
 */
public class MyContext extends ContextWrapper {
    public MyContext(Context base) {
        super(base);
    }
    public String getIP(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return SP.getString("prefIP", "NA");
    }
}
