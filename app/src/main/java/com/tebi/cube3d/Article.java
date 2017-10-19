package com.tebi.cube3d;

public class Article {
	private String title;
	private int teaserLink;
	private boolean isChecked = false;

	public Article(String title, int teaserLink) {
		this.title = title;
		this.teaserLink = teaserLink;
	}

	public String getTitle() {
		return title;
	}

	public int getTeaser() {
		return teaserLink;
	}
	
	public boolean getIsChecked() {
		return isChecked;
	}
	
	public void setIsChecked(boolean b) {
		this.isChecked = b;
	}

	@Override
	public String toString() {
		return this.title;
	}

}
