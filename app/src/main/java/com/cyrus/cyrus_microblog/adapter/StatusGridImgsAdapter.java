package com.cyrus.cyrus_microblog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.model.PicUrls;
import com.cyrus.cyrus_microblog.utils.ImageOptHelper;
import com.cyrus.cyrus_microblog.widget.WrapHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 多图情况下的微博图片适配器
 */
public class StatusGridImgsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mStrPicUrls;
    private ArrayList<PicUrls> mPicUrls;
    private ImageLoader mImageLoader;

    public StatusGridImgsAdapter(Context context, ArrayList<String> strPicUrls) {
        mContext = context;
        mStrPicUrls = strPicUrls;
        mImageLoader = ImageLoader.getInstance();
        mPicUrls = new ArrayList<>();
        for (String s : mStrPicUrls) {
            PicUrls picUrls = new PicUrls(s);
            mPicUrls.add(picUrls);
        }
    }

    @Override
    public int getCount() {
        return mPicUrls.size();
    }

    @Override
    public PicUrls getItem(int position) {
        return mPicUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_grid_image, parent, false);
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //得到每个图片的尺寸
        WrapHeightGridView gv = (WrapHeightGridView) parent;
        int horizontalSpacing = gv.getHorizontalSpacing();
        int numColumns = gv.getNumColumns();
        int itemWidth = (gv.getWidth() - (numColumns - 1) * horizontalSpacing
                - gv.getPaddingLeft() - gv.getPaddingRight()) / numColumns;

        LayoutParams params = new LayoutParams(itemWidth, itemWidth);
        holder.iv_image.setLayoutParams(params);

        String url = getItem(position).getBmiddlePic();
        mImageLoader.displayImage(url, holder.iv_image, ImageOptHelper.getImgOptions());

        return convertView;
    }

    private static class ViewHolder {
        public ImageView iv_image;
    }

}
