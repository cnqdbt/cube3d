package com.tebi.wallpaper3dmilan;

import rajawali.wallpaper.Wallpaper;
import android.content.Context;
import android.content.Intent;

public class Service extends Wallpaper {
	private Renderer mRenderer;
	
	public Engine onCreateEngine() {
		mRenderer = new Renderer(this);
		return new WallpaperEngine(this.getSharedPreferences(SHARED_PREFS_NAME,
				Context.MODE_PRIVATE), getBaseContext(), mRenderer, false);
	}


	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	
}
