package com.tebi.cube3d;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.tendcloud.tenddata.TCAgent;

import rajawali.wallpaper.Wallpaper;

/**
 * Created by bite on 17-10-19.
 */

public class Cube3dApplication extends Application {

    protected SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(Wallpaper.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);

        TCAgent.init(getApplicationContext());
        TCAgent.setReportUncaughtExceptions(true);
    }


    public void setClubInPref(int c) {
        preferences.edit().putInt("current_club", c).commit();
    }

    public int getClubInPref() {
        return preferences.getInt("current_club", 0);
    }

}
