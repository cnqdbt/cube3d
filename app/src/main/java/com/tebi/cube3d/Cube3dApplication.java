package com.tebi.cube3d;

import android.app.Application;

/**
 * Created by bite on 17-10-19.
 */

public class Cube3dApplication extends Application {

    private int mCurrentClub;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public void setClub(int c) {
        this.mCurrentClub = c;
    }

    public int getClub() {
        return mCurrentClub;
    }

}
