package com.cyrus.cyrus_microblog.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.activity.StatusDetailActivity;
import com.cyrus.cyrus_microblog.activity.WriteCommentActivity;
import com.cyrus.cyrus_microblog.utils.DateUtils;
import com.cyrus.cyrus_microblog.utils.StringUtils;
import com.cyrus.cyrus_microblog.utils.ToastUtils;
import com.cyrus.cyrus_microblog.widget.WrapHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 微博列表适配器
 */
public class StatusAdapter extends BaseAdapter {

    private Context mContext;
    private List<Status> mStatuses;
    private ImageLoader mImageLoader;

    public StatusAdapter(Context context, List<Status> statuses) {
        mContext = context;
        mStatuses = statuses;
        mImageLoader = ImageLoader.getInstance();
    }

    public void setStatuses(List<Status> statuses) {
        mStatuses = statuses;
    }

    @Override
    public int getCount() {
        return mStatuses.size();
    }

    @Override
    public Status getItem(int position) {
        return mStatuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_status, parent, false);

            holder.ll_card_content = (LinearLayout) convertView
                    .findViewById(R.id.ll_card_content);
            holder.iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            holder.rl_content = (RelativeLayout) convertView
                    .findViewById(R.id.rl_content);
            holder.tv_subhead = (TextView) convertView
                    .findViewById(R.id.tv_subhead);
            holder.tv_caption = (TextView) convertView
                    .findViewById(R.id.tv_caption);

            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_content);
            holder.include_status_image = (FrameLayout) convertView
                    .findViewById(R.id.include_status_image);
            holder.gv_images = (WrapHeightGridView) holder.include_status_image
                    .findViewById(R.id.gv_images);
            holder.iv_image = (ImageView) holder.include_status_image
                    .findViewById(R.id.iv_image);

            holder.include_retweeted_status = (LinearLayout) convertView
                    .findViewById(R.id.include_retweeted_status);
            holder.tv_retweeted_content = (TextView) holder
                    .include_retweeted_status.findViewById(R.id.tv_retweeted_content);
            holder.include_retweeted_status_image = (FrameLayout) holder
                    .include_retweeted_status.findViewById(R.id.include_status_image);
            holder.gv_retweeted_images = (WrapHeightGridView) holder
                    .include_retweeted_status_image.findViewById(R.id.gv_images);
            holder.iv_retweeted_image = (ImageView) holder
                    .include_retweeted_status_image.findViewById(R.id.iv_image);

            holder.ll_share_bottom = (LinearLayout) convertView
                    .findViewById(R.id.ll_share_bottom);
            holder.iv_share_bottom = (ImageView) convertView
                    .findViewById(R.id.iv_share_bottom);
            holder.tv_share_bottom = (TextView) convertView
                    .findViewById(R.id.tv_share_bottom);
            holder.ll_comment_bottom = (LinearLayout) convertView
                    .findViewById(R.id.ll_comment_bottom);
            holder.iv_comment_bottom = (ImageView) convertView
                    .findViewById(R.id.iv_comment_bottom);
            holder.tv_comment_bottom = (TextView) convertView
                    .findViewById(R.id.tv_comment_bottom);
            holder.ll_like_bottom = (LinearLayout) convertView
                    .findViewById(R.id.ll_like_bottom);
            holder.iv_like_bottom = (ImageView) convertView
                    .findViewById(R.id.iv_like_bottom);
            holder.tv_like_bottom = (TextView) convertView
                    .findViewById(R.id.tv_like_bottom);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 绑定数据
        final Status status = getItem(position);
        User user = status.user;

        mImageLoader.displayImage(user.profile_image_url, holder.iv_avatar);
        holder.tv_subhead.setText(user.name);
        String sourceStr = String.format("%s 来自 %s",//日期、来源
                DateUtils.getShortTime(mContext, status.created_at),
                Html.fromHtml(status.source));
        holder.tv_caption.setText(sourceStr);
        holder.tv_content.setText(StringUtils.getSpannableString(mContext,
                holder.tv_content, status.text));

        setImages(status, holder.include_status_image,
                holder.gv_images, holder.iv_image);

        final Status retweeted_status = status.retweeted_status;
        if (retweeted_status != null) {
            holder.include_retweeted_status.setVisibility(View.VISIBLE);
            User retUser = retweeted_status.user;
            if (retUser != null) {
                String retweetContent = String.format("@%s：%s", retUser.name, retweeted_status.text);
                holder.tv_retweeted_content.setText(StringUtils.getSpannableString(mContext,
                        holder.tv_retweeted_content, retweetContent));

                setImages(retweeted_status, holder.include_retweeted_status_image,
                        holder.gv_retweeted_images, holder.iv_retweeted_image);
            } else {
                String contentStr = String.format("%s", retweeted_status.text);
                holder.tv_retweeted_content.setText(StringUtils.getSpannableString(mContext,
                        holder.tv_retweeted_content, contentStr));

                setImages(retweeted_status, holder.include_retweeted_status_image,
                        holder.gv_retweeted_images, holder.iv_retweeted_image);
            }
        } else {
            holder.include_retweeted_status.setVisibility(View.GONE);
        }

        //设置转发按钮的文字
        holder.tv_share_bottom.setText(status.reposts_count == 0 ?
                mContext.getString(R.string.item_report) :
                String.valueOf(status.reposts_count));

        //设置评论按钮的文字
        holder.tv_comment_bottom.setText(status.comments_count == 0 ?
                mContext.getString(R.string.item_comment) :
                String.valueOf(status.comments_count));

        //设置点赞按钮的文字
        holder.tv_like_bottom.setText(status.attitudes_count == 0 ?
                mContext.getString(R.string.item_attitude) :
                String.valueOf(status.attitudes_count));

        //设置GridView为失焦，否则会导致ListView点击事件没有响应
        holder.gv_images.setFocusable(false);
        holder.gv_retweeted_images.setFocusable(false);

        //设置图片的点击事件
        holder.iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "Image", Toast.LENGTH_SHORT);
            }
        });

        holder.iv_retweeted_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "RetweetedImage", Toast.LENGTH_SHORT);
            }
        });

        //设置进入微博的点击事件，点击多图的空白处也会进入页面
        holder.ll_card_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2StatusDetailActivity(status);
            }
        });

        holder.gv_images.setOnTouchInvalidPositionListener(
                new WrapHeightGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        if (motionEvent == MotionEvent.ACTION_UP) {
                            intent2StatusDetailActivity(status);
                        }
                        return false;
                    }
                });

        //设置进入被转发微博的点击事件，点击多图的空白处也会进入页面
        holder.include_retweeted_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2StatusDetailActivity(retweeted_status);
            }
        });

        holder.gv_retweeted_images.setOnTouchInvalidPositionListener(
                new WrapHeightGridView.OnTouchInvalidPositionListener() {
                    @Override
                    public boolean onTouchInvalidPosition(int motionEvent) {
                        if (motionEvent == MotionEvent.ACTION_UP) {
                            intent2StatusDetailActivity(retweeted_status);
                        }
                        return false;
                    }
                }
        );

        //设置分享的点击事件
        holder.ll_share_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "转个发~", Toast.LENGTH_SHORT);
            }
        });

        //设置评论的点击事件
        //当微博评论数不为零则进入微博详情页面的评论处
        //当微博评论数为零则进入微博评论界面
        holder.ll_comment_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.comments_count > 0) {
                    intent2StatusDetailActivity2Comment(status);
                } else {
                    intent2WriteCommentActivity(status);
                }
            }
        });

        //设置点赞的点击事件
        holder.ll_like_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "点个赞~", Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }

    private void intent2WriteCommentActivity(Status status) {
        Intent intent = new Intent(mContext, WriteCommentActivity.class);
        intent.putExtra("mStatus", status);
        mContext.startActivity(intent);
    }

    private void intent2StatusDetailActivity2Comment(Status status) {
        Intent intent = new Intent(mContext, StatusDetailActivity.class);
        intent.putExtra("mStatus", status);
        intent.putExtra("mScroll2Comment", true);
        mContext.startActivity(intent);
    }

    private void intent2StatusDetailActivity(Status status) {
        Intent intent = new Intent(mContext, StatusDetailActivity.class);
        intent.putExtra("mStatus", status);
        mContext.startActivity(intent);
    }

    private void setImages(Status status, FrameLayout imgContainer,
                           WrapHeightGridView gv_images, ImageView iv_image) {
        ArrayList<String> pic_urls = status.pic_urls;
        String thumbnail_pic = status.thumbnail_pic;

        if (pic_urls != null && pic_urls.size() > 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.VISIBLE);
            iv_image.setVisibility(View.GONE);

            StatusGridImgsAdapter gvAdapter = new StatusGridImgsAdapter(mContext, pic_urls);
            gv_images.setAdapter(gvAdapter);
        } else if (pic_urls != null && pic_urls.size() == 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.GONE);
            iv_image.setVisibility(View.VISIBLE);

            mImageLoader.displayImage(thumbnail_pic, iv_image);
        } else {
            imgContainer.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder {
        public LinearLayout ll_card_content;
        public ImageView iv_avatar;
        public RelativeLayout rl_content;
        public TextView tv_subhead;
        public TextView tv_caption;

        public TextView tv_content;
        public FrameLayout include_status_image;
        public WrapHeightGridView gv_images;
        public ImageView iv_image;

        public LinearLayout include_retweeted_status;
        public TextView tv_retweeted_content;
        public FrameLayout include_retweeted_status_image;
        public WrapHeightGridView gv_retweeted_images;
        public ImageView iv_retweeted_image;

        public LinearLayout ll_share_bottom;
        public ImageView iv_share_bottom;
        public TextView tv_share_bottom;
        public LinearLayout ll_comment_bottom;
        public ImageView iv_comment_bottom;
        public TextView tv_comment_bottom;
        public LinearLayout ll_like_bottom;
        public ImageView iv_like_bottom;
        public TextView tv_like_bottom;
    }

}