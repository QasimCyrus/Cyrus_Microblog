package com.cyrus.cyrus_microblog.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 存放微博图片的url地址
 */
public class PicUrls implements Serializable {
	// 中等质量图片url前缀
	private static final String BMIDDLE_URL = "http://ww3.sinaimg.cn/bmiddle/";
	// 原质量图片url前缀
	private static final String ORIGINAL_URL = "http://ww3.sinaimg.cn/large/";
	
	private String mThumbnailPic;
	private String mBmiddlePic;
	private String mOriginalPic;

	public PicUrls(String urlStr) {
		mThumbnailPic = urlStr;
	}

	/**
	 * 从缩略图url中截取末尾的图片id,用于和拼接成其他质量图片url
	 */
	public String getImageId() {
		int indexOf = mThumbnailPic.lastIndexOf("/") + 1;
		return mThumbnailPic.substring(indexOf);
	}
	
	public String getThumbnailPic() {
		return mThumbnailPic;
	}

	public void setThumbnailPic(String thumbnailPic) {
		this.mThumbnailPic = thumbnailPic;
	}

	public String getBmiddlePic() {
		return TextUtils.isEmpty(mBmiddlePic) ? BMIDDLE_URL + getImageId() : mBmiddlePic;
	}

	public void setBmiddlePic(String bmiddlePic) {
		this.mBmiddlePic = bmiddlePic;
	}

	public String getOriginalPic() {
		return TextUtils.isEmpty(mOriginalPic) ? ORIGINAL_URL + getImageId() : mOriginalPic;
	}

	public void setOriginalPic(String originalPic) {
		this.mOriginalPic = originalPic;
	}

}