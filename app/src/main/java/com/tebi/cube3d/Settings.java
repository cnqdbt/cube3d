package com.tebi.cube3d;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;

import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;

import rajawali.wallpaper.Wallpaper;

public class Settings extends AppCompatActivity implements
	SeekBar.OnSeekBarChangeListener {
	
	private ListView mainListView;
	private ArrayList<Article> articles = new ArrayList<Article>();
	private ArticleArrayAdapter adapter;
	private SeekBar mScaleSeekBar;
	private SeekBar mSpeedSeekBar;
	private ImageView mAutoRotateImgView;
	private boolean mAutoRotate;
    private int mCurrentClub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_single);

        initArticles();
        initToolbar();

        SharedPreferences prefs = getSharedPreferences(Wallpaper.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int checkedPosition = prefs.getInt("checked", 0);
        mAutoRotate = prefs.getBoolean("auto_rotate", false);
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
                TCAgent.onEvent(getApplication(), "on_background_select");

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
                TCAgent.onEvent(getApplication(), "auto_rotate_changed");
            }
        });

        mScaleSeekBar = (SeekBar) findViewById(R.id.scale_seekbar);
        mScaleSeekBar.setOnSeekBarChangeListener(this);
        mScaleSeekBar.setProgress(prefs.getInt("scale", 90));

        mSpeedSeekBar = (SeekBar) findViewById(R.id.speed_seekbar);
        mSpeedSeekBar.setOnSeekBarChangeListener(this);
        mSpeedSeekBar.setProgress(prefs.getInt("speed", 50));

        Button setButton = (Button) findViewById(R.id.set_button);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initArticles() {
        mCurrentClub = ((Cube3dApplication)getApplication()).getClubInPref();
        articles.add(new Article(getResources().getString(R.string.bg_1), getDrawableIdByName("scene_thumb_" + mCurrentClub)));
        articles.add(new Article(getResources().getString(R.string.bg_2), getDrawableIdByName("scene2_thumb_" + mCurrentClub)));
    }

    private int getDrawableIdByName(String name) {
        return  getResources().getIdentifier(name, "drawable", getPackageName());
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


}