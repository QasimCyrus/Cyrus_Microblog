package com.cyrus.cyrus_microblog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.utils.ToastUtils;
import com.cyrus.cyrus_microblog.widget.WrapHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 多图情况下的微博图片适配器
 */
public class StatusGridImgsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mPicUrls;
    private ImageLoader mImageLoader;

    public StatusGridImgsAdapter(Context context, ArrayList<String> picUrls) {
        this.mContext = context;
        this.mPicUrls = picUrls;
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mPicUrls.size();
    }

    @Override
    public String getItem(int position) {
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
            //设置图片的点击事件
            holder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast(mContext, "you click image", Toast.LENGTH_SHORT);
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WrapHeightGridView gv = (WrapHeightGridView) parent;
        int horizontalSpacing = gv.getHorizontalSpacing();
        int numColumns = gv.getNumColumns();
        int itemWidth = (gv.getWidth() - (numColumns - 1) * horizontalSpacing
                - gv.getPaddingLeft() - gv.getPaddingRight()) / numColumns;

        LayoutParams params = new LayoutParams(itemWidth, itemWidth);
        holder.iv_image.setLayoutParams(params);

        String url = getItem(position);
        mImageLoader.displayImage(url, holder.iv_image);

        return convertView;
    }

    public static class ViewHolder {
        public ImageView iv_image;
    }

}
