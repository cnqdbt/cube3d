package com.tebi.wallpaper3dmilan;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
	private List<Article> articles = new ArrayList<Article>();
	private Context context;
	private final static String TAG = "ArticleArrayAdapter";

	public ArticleArrayAdapter(Context context, int textViewResourceId,
			List<Article> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.articles = objects;
	}

	public int getCount() {
		return this.articles.size();
	}

	public Article getItem(int index) {
		return this.articles.get(index);
	}

	public View getView(int position, View articleView, ViewGroup parent) {
		if (articleView == null) {
			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			articleView = inflater.inflate(R.layout.list_item, null);
		} 
		// Get item
		Article item = getItem(position);
		
		
		ImageView teaserImgView = (ImageView) articleView.findViewById(R.id.image_teaser);
		TextView titleView = (TextView) articleView.findViewById(R.id.title);
		ImageView checkboxImgView = (ImageView) articleView.findViewById(R.id.checkbox);
		
		titleView.setText(item.getTitle());
		teaserImgView.setImageResource(item.getTeaser());
		if (item.getIsChecked()) {
			checkboxImgView.setImageResource(R.drawable.btn_check_on_holo_light);
		} else {
			checkboxImgView.setImageResource(R.drawable.btn_check_off_holo_light);
		}
		

		return articleView;
	}

}
