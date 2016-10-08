package com.cyrus.cyrus_microblog.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.utils.DateUtils;
import com.cyrus.cyrus_microblog.utils.ImageOptHelper;
import com.cyrus.cyrus_microblog.utils.StringUtils;
import com.cyrus.cyrus_microblog.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

public class StatusCommentAdapter extends BaseAdapter {

    private Context mContext;
    private List<Comment> mComments;
    private ImageLoader mImageLoader;

    public StatusCommentAdapter(Context context, List<Comment> comments) {
        this.mContext = context;
        this.mComments = comments;
        this.mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Comment getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderList holder;
        if (convertView == null) {
            holder = new ViewHolderList();
            convertView = View.inflate(mContext, R.layout.item_comment, null);
            holder.ll_comments = (LinearLayout) convertView
                    .findViewById(R.id.ll_comments);
            holder.iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            holder.tv_subhead = (TextView) convertView
                    .findViewById(R.id.tv_subhead);
            holder.tv_caption = (TextView) convertView
                    .findViewById(R.id.tv_caption);
            holder.tv_comment = (TextView) convertView
                    .findViewById(R.id.tv_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderList) convertView.getTag();
        }

        Comment comment = getItem(position);
        User user = comment.user;

        mImageLoader.displayImage(user.profile_image_url, holder.iv_avatar,
                ImageOptHelper.getAvatarOptions());
        holder.tv_subhead.setText(user.name);
        holder.tv_caption.setText(DateUtils.getShortTime(mContext, comment.created_at));
        SpannableString weiboContent = StringUtils
                .getSpannableString(mContext, holder.tv_comment, comment.text);
        holder.tv_comment.setText(weiboContent);

        holder.ll_comments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "回复评论", Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
    }

    private static class ViewHolderList {
        LinearLayout ll_comments;
        ImageView iv_avatar;
        TextView tv_subhead;
        TextView tv_caption;
        TextView tv_comment;
    }

}
