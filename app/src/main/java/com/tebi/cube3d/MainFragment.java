package com.tebi.cube3d;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class MainFragment extends Fragment {

    Button enterBtn;
    ImageView headerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        enterBtn = (Button) rootView.findViewById(R.id.enter);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCubePreview();
            }
        });
        headerView = (ImageView) rootView.findViewById(R.id.header_view);
        refresh();
        return rootView;
    }

    public void refresh() {
        int currentClub = ((Cube3dApplication) getActivity().getApplication()).getClub();
        headerView.setImageResource(getDrawableIdByName("header_" + currentClub));

    }

    private void goToCubePreview() {
        Intent i = new Intent();

        if (Build.VERSION.SDK_INT >= 16) {
            i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(getActivity(), Service.class));
        } else {
            i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        }

        startActivity(i);
    }

    private int getDrawableIdByName(String name) {
        return  getResources().getIdentifier(name, "drawable", getContext().getPackageName());
    }
}
