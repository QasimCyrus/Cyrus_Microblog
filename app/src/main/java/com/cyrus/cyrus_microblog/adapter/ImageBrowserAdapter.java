package com.cyrus.cyrus_microblog.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.model.PicUrls;
import com.cyrus.cyrus_microblog.utils.DisplayUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class ImageBrowserAdapter extends PagerAdapter {

    private Activity mActivity;
    private ArrayList<PicUrls> mPicUrls;
    private ArrayList<View> mPicViews;
    private ImageLoader mImageLoader;

    public ImageBrowserAdapter(Activity context, ArrayList<PicUrls> picUrls) {
        mActivity = context;
        mPicUrls = picUrls;
        mImageLoader = ImageLoader.getInstance();
        initImgs();
    }

    private void initImgs() {
        mPicViews = new ArrayList<>();

        for (int i = 0; i < mPicUrls.size(); i++) {
            // 填充显示图片的页面布局
            View view = View.inflate(mActivity, R.layout.item_image_browser, null);
            mPicViews.add(view);
        }
    }

    @Override
    public int getCount() {
        return mPicUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = mPicViews.get(position);
        final ImageView ivImageBrowser = (ImageView) view.findViewById(R.id.iv_image_browser);
        PicUrls picUrl = mPicUrls.get(position);

        String url = picUrl.getOriginalPic();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.NONE)
                .build();
        mImageLoader.loadImage(url, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                /*
                 * 设置ImageView的宽度为屏幕宽度，高度根据图片高度来调节，
                 * 如果图片高度不大于屏幕高度，则ImageView的高度使用屏幕高度。
                 *
                 * 图片如果太大，可能会导致卡顿，所以压缩画质
                 * 或者取消硬件加速
                 */
//                if (loadedImage.getHeight() > 800) {
//                    BitmapFactory.Options opt = new BitmapFactory.Options();
//                    opt.inSampleSize = 2;
//                    try {
//                        ByteArrayOutputStream os = new ByteArrayOutputStream();
//                        loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
//                        loadedImage = BitmapFactory.decodeByteArray(
//                                os.toByteArray(), 0, os.toByteArray().length, opt);
//                        os.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }

                float scale = (float) loadedImage.getHeight() / loadedImage.getWidth();
                int screenWidthPixels = DisplayUtils.getScreenWidthPixels(mActivity);
                int screenHeightPixels = DisplayUtils.getScreenHeightPixels(mActivity);
                int height = (int) (screenWidthPixels * scale);

                if (height < screenHeightPixels) {
                    height = screenHeightPixels;
                }

                LayoutParams params = ivImageBrowser.getLayoutParams();
                params.height = height;
                params.width = screenWidthPixels;
                ivImageBrowser.setLayoutParams(params);

                ivImageBrowser.setImageBitmap(loadedImage);
                ivImageBrowser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.finish();
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public PicUrls getPic(int position) {
        return mPicUrls.get(position);
    }

    public Bitmap getBitmap(int position) {
        Bitmap bitmap = null;
        View view = mPicViews.get(position);
        ImageView ivImageBrowser = (ImageView) view.findViewById(R.id.iv_image_browser);
        Drawable drawable = ivImageBrowser.getDrawable();

        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            bitmap = bd.getBitmap();
        }

        return bitmap;
    }

}