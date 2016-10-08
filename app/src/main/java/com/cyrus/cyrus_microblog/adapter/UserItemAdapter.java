package com.cyrus.cyrus_microblog.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.model.UserItem;
import com.cyrus.cyrus_microblog.utils.ToastUtils;

import java.util.List;


public class UserItemAdapter extends BaseAdapter {

    private Context context;
    private List<UserItem> datas;

    public UserItemAdapter(Context context, List<UserItem> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public UserItem getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_user, null);
            holder.v_divider = convertView.findViewById(R.id.v_divider);
            holder.ll_content = convertView.findViewById(R.id.ll_content);
            holder.iv_left = (ImageView) convertView.findViewById(R.id.iv_left);
            holder.tv_subhead = (TextView) convertView.findViewById(R.id.tv_subhead);
            holder.tv_caption = (TextView) convertView.findViewById(R.id.tv_caption);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set data
        UserItem item = getItem(position);
        holder.iv_left.setImageResource(item.getLeftImg());
        holder.tv_subhead.setText(item.getSubhead());
        holder.tv_caption.setText(item.getCaption());

        holder.v_divider.setVisibility(item.isShowTopDivider() ?
                View.VISIBLE : View.GONE);
        holder.ll_content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(context, "item click position = " + position, Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        View v_divider;
        View ll_content;
        ImageView iv_left;
        TextView tv_subhead;
        TextView tv_caption;
    }

}