package com.cyrus.cyrus_microblog.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cyrus.cyrus_microblog.R;

import java.util.ArrayList;

public class WriteStatusGridImgsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Uri> mUris;
    private GridView mGridView;

    public WriteStatusGridImgsAdapter(Context context, ArrayList<Uri> uris, GridView gridView) {
        this.mContext = context;
        this.mUris = uris;
        this.mGridView = gridView;
    }

    @Override
    public int getCount() {
        return mUris.size() > 0 ? mUris.size() + 1 : 0;
    }

    @Override
    public Uri getItem(int position) {
        return mUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_grid_image, null);
            holder.mIvImage = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.mIvDeleteImage = (ImageView) convertView.findViewById(R.id.iv_delete_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int horizontalSpacing = mGridView.getHorizontalSpacing();
        int width = (mGridView.getWidth() - horizontalSpacing * 2
                - mGridView.getPaddingLeft() - mGridView.getPaddingRight()) / 3;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        holder.mIvImage.setLayoutParams(params);

        if (position < getCount() - 1) {
            // set data
            Uri item = getItem(position);
            holder.mIvImage.setImageURI(item);

            holder.mIvDeleteImage.setVisibility(View.VISIBLE);
            holder.mIvDeleteImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUris.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.mIvImage.setImageResource(R.drawable.compose_pic_add_more);
            holder.mIvDeleteImage.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView mIvImage;
        ImageView mIvDeleteImage;
    }

}
