package com.cyrus.cyrus_microblog.model;

public class UserItem {

	public UserItem(boolean isShowTopDivider, int leftImg, String subhead, String caption) {
		mIsShowTopDivider = isShowTopDivider;
		mLeftImg = leftImg;
		mSubhead = subhead;
		mCaption = caption;
	}

	private boolean mIsShowTopDivider;
	private int mLeftImg;
	private String mSubhead;
	private String mCaption;

	public boolean isShowTopDivider() {
		return mIsShowTopDivider;
	}

	public void setShowTopDivider(boolean isShowTopDivider) {
		mIsShowTopDivider = isShowTopDivider;
	}

	public int getLeftImg() {
		return mLeftImg;
	}

	public void setLeftImg(int leftImg) {
		this.mLeftImg = leftImg;
	}

	public String getSubhead() {
		return mSubhead;
	}

	public void setSubhead(String subhead) {
		this.mSubhead = subhead;
	}

	public String getCaption() {
		return mCaption;
	}

	public void setCaption(String caption) {
		this.mCaption = caption;
	}

}
