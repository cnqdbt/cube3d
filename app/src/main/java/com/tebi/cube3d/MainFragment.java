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
import android.widget.TextView;

import com.tendcloud.tenddata.TCAgent;


public class MainFragment extends Fragment {

    Button enterBtn;
    ImageView headerView;
    TextView tvTitle;
    TextView tvContent;
    String[] titles;
    String[] contents;
    int mCurrentClub;

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
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        tvContent = (TextView) rootView.findViewById(R.id.tv_content);
        titles = getResources().getStringArray(R.array.titles);
        contents = getResources().getStringArray(R.array.contents);

        refresh();
        return rootView;
    }

    public void setCurrentClub(int c) {
        mCurrentClub = c;
    }

    public void refresh() {
        headerView.setImageResource(getDrawableIdByName("header_" + mCurrentClub));
        tvTitle.setText(titles[mCurrentClub]);
        tvContent.setText(contents[mCurrentClub]);
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

        ((Cube3dApplication)(getActivity().getApplication())).setClubInPref(mCurrentClub);
        TCAgent.onEvent(getContext(), "enter_fame_hall", mCurrentClub + "");
    }

    private int getDrawableIdByName(String name) {
        return  getResources().getIdentifier(name, "drawable", getContext().getPackageName());
    }

}
