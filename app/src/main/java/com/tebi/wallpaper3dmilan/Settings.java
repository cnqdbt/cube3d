package com.tebi.wallpaper3dmilan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import rajawali.wallpaper.Wallpaper;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;

public class Settings extends Activity implements
	SeekBar.OnSeekBarChangeListener {
	
	private ListView mainListView;
	private ArrayList<Article> articles = new ArrayList<Article>();
	private ArticleArrayAdapter adapter;
	private SeekBar mScaleSeekBar;
	private SeekBar mSpeedSeekBar;
	private ImageView mAutoRotateImgView;
	private ImageView mFixCubeImgView;
	private boolean mAutoRotate;
	private boolean mFixCube;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_single);
        initArticles();


        SharedPreferences prefs = getSharedPreferences(Wallpaper.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int checkedPosition = prefs.getInt("checked", 0);
        mAutoRotate = prefs.getBoolean("auto_rotate", true);
        mFixCube = prefs.getBoolean("fix_cube", false);
        articles.get(checkedPosition).setIsChecked(true);
        mainListView = (ListView) findViewById(R.id.bg_list);
        adapter = new ArticleArrayAdapter(Settings.this, R.layout.list_item, articles);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener( new OnItemClickListener() {
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                for (Article a : articles) {
                    a.setIsChecked(false);
                }
                articles.get(position).setIsChecked(true);
                adapter.notifyDataSetChanged();
                SharedPreferences prefs = getSharedPreferences(Wallpaper.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putInt("checked", position).commit();

            }
        });

        mAutoRotateImgView = (ImageView) findViewById(R.id.auto_rotate_checkbox);
        mAutoRotateImgView.setImageResource(mAutoRotate ? R.drawable.btn_check_on_holo_light : R.drawable.btn_check_off_holo_light);
        mAutoRotateImgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAutoRotate = !mAutoRotate;
                SharedPreferences prefs = getSharedPreferences(Wallpaper.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putBoolean("auto_rotate", mAutoRotate).commit();
                mAutoRotateImgView.setImageResource(mAutoRotate ? R.drawable.btn_check_on_holo_light : R.drawable.btn_check_off_holo_light);

                if (mAutoRotate) {
                    mFixCube = false;
                    prefs.edit().putBoolean("fix_cube", mFixCube).commit();
                    mFixCubeImgView.setImageResource(R.drawable.btn_check_off_holo_light);
                }
            }
        });

        mFixCubeImgView = (ImageView) findViewById(R.id.fix_cube_checkbox);
        mFixCubeImgView.setImageResource(mFixCube ? R.drawable.btn_check_on_holo_light : R.drawable.btn_check_off_holo_light);
        mFixCubeImgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mFixCube = !mFixCube;
                SharedPreferences prefs = getSharedPreferences(Wallpaper.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putBoolean("fix_cube", mFixCube).commit();
                mFixCubeImgView.setImageResource(mFixCube ? R.drawable.btn_check_on_holo_light : R.drawable.btn_check_off_holo_light);

                if (mFixCube) {
                    mAutoRotate = false;
                    prefs.edit().putBoolean("auto_rotate", mAutoRotate).commit();
                    mAutoRotateImgView.setImageResource(R.drawable.btn_check_off_holo_light);
                }

            }
        });


        mScaleSeekBar = (SeekBar) findViewById(R.id.scale_seekbar);
        mScaleSeekBar.setOnSeekBarChangeListener(this);
        mScaleSeekBar.setProgress(prefs.getInt("scale", 80));

        mSpeedSeekBar = (SeekBar) findViewById(R.id.speed_seekbar);
        mSpeedSeekBar.setOnSeekBarChangeListener(this);
        mSpeedSeekBar.setProgress(prefs.getInt("speed", 50));

        Button setButton = (Button) findViewById(R.id.set_button);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setWallPaper();
                } catch (Exception e) {

                }
            }
        });

        Button shareButton = (Button) findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

    }

    private void initArticles() {
        articles.add(new Article(getResources().getString(R.string.bg_1), R.drawable.scene_thumb));
        articles.add(new Article(getResources().getString(R.string.bg_2), R.drawable.scene2_thumb));
    }

    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        SharedPreferences prefs = getSharedPreferences(Wallpaper.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        if (seekBar.getId() == R.id.scale_seekbar) {
            prefs.edit().putInt("scale", progress).commit();
        } else if (seekBar.getId() == R.id.speed_seekbar) {
            prefs.edit().putInt("speed", progress).commit();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setWallPaper() {
        Intent i = new Intent();

        if (Build.VERSION.SDK_INT >= 16) {
            i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, Service.class));
        } else {
            i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        }

        startActivity(i);
        this.finish();
    }

    private void share() {
//        String rootDir = null;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            // SD-card available
//            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/"
//                    + Settings.this.getPackageName() + "/cache";
//        } else {
//            File internalCacheDir = Settings.this.getCacheDir();
//            // apparently on some configurations this can come back as null
//            if (internalCacheDir != null) {
//                rootDir = internalCacheDir.getAbsolutePath();
//            }
//        }
//
//        File bitmapFile = null;
//
//        if (rootDir != null) {
//            File outFile = new File(rootDir);
//            outFile.mkdirs();
//            if (outFile.exists()) {
//                File nomedia = new File(rootDir, ".nomedia");
//                bitmapFile = new File(rootDir, "share.png");
//                try {
//                    if (!nomedia.exists()) {
//                        nomedia.createNewFile();
//                    }
//
//                    if (!bitmapFile.exists()) {
//                        bitmapFile.createNewFile();
//                        FileOutputStream bitmapWtriter = new FileOutputStream(
//                                bitmapFile);
//                        Bitmap bmp = BitmapFactory.decodeResource(
//                                getResources(), R.drawable.share);
//                        bmp.compress(Bitmap.CompressFormat.PNG, 100,
//                                bitmapWtriter);
//                        bitmapWtriter.flush();
//                        bitmapWtriter.close();
//                    }
//                } catch (IOException e) {
//                }
//            }
//        }
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        // shareIntent.putExtra(Intent.EXTRA_SUBJECT, "通过海信备忘录分享");
//        shareIntent.putExtra(Intent.EXTRA_TEXT,
//                getResources().getString(R.string.share_content));
//        if (bitmapFile != null) {
//            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(bitmapFile));
//            shareIntent.setType("image/*");
//        }
//        startActivity(Intent.createChooser(shareIntent, getResources()
//                .getString(R.string.share)));

    }



}