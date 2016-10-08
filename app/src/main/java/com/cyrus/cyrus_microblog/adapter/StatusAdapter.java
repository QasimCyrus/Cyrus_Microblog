package com.cyrus.cyrus_microblog.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.activity.ImageBrowserActivity;
import com.cyrus.cyrus_microblog.activity.StatusDetailActivity;
import com.cyrus.cyrus_microblog.activity.WriteCommentActivity;
import com.cyrus.cyrus_microblog.activity.WriteStatusActivity;
import com.cyrus.cyrus_microblog.api.StatusesApi;
import com.cyrus.cyrus_microblog.api.UsersApi;
import com.cyrus.cyrus_microblog.constants.AccessTokenKeeper;
import com.cyrus.cyrus_microblog.utils.DateUtils;
import com.cyrus.cyrus_microblog.utils.DialogUtils;
import com.cyrus.cyrus_microblog.utils.ImageOptHelper;
import com.cyrus.cyrus_microblog.utils.StringUtils;
import com.cyrus.cyrus_microblog.utils.ToastUtils;
import com.cyrus.cyrus_microblog.widget.WrapHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
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
    private StatusesApi mStatusesApi;

    public StatusAdapter(Context context, List<Status> statuses) {
        mContext = context;
        mStatuses = statuses;
        mImageLoader = ImageLoader.getInstance();
        mStatusesApi = new StatusesApi(mContext);
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

        mImageLoader.displayImage(user.profile_image_url, holder.iv_avatar,
                ImageOptHelper.getAvatarOptions());
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

        //设置微博的长按事件
        holder.ll_card_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showStatusOperationDialog(status);
                return true;
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

        //设置转发的点击事件
        holder.ll_share_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2WriteStatusActivity(status);
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

    private void showStatusOperationDialog(final Status status) {
        new UsersApi(mContext).userShow(Long.parseLong(AccessTokenKeeper
                .readAccessToken(mContext).getUid()), new RequestListener() {
            @Override
            public void onComplete(String s) {
                User currentUser = User.parse(s);
                if (currentUser != null) {
                    if (currentUser.id.equals(status.user.id)) {
                        showSelfStatusOperationDialog(status);
                    } else {
                        showOtherStatusOperationDialog(status);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
            }
        });

    }

    private void showOtherStatusOperationDialog(final Status status) {
        final List<String> list = new ArrayList<>();
        list.add("转发");
        list.add("评论");
        if (status.retweeted_status != null) {
            list.add("转发原微博");
        }
        DialogUtils.showListDialog(mContext, null, list,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                intent2WriteStatusActivity(status);
                                break;
                            case 1:
                                intent2WriteCommentActivity(status);
                                break;
                            case 2:
                                intent2WriteStatusActivity(status.retweeted_status);
                                break;
                        }
                    }
                });
    }

    private void showSelfStatusOperationDialog(final Status status) {
        final List<String> list = new ArrayList<>();
        list.add("转发");
        list.add("评论");
        list.add("删除");
        if (status.retweeted_status != null) {
            list.add("转发原微博");
        }
        DialogUtils.showListDialog(mContext, null, list,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                intent2WriteStatusActivity(status);
                                break;
                            case 1:
                                intent2WriteCommentActivity(status);
                                break;
                            case 2:
                                mStatusesApi.statusDestory(Long.parseLong(status.id),
                                        new RequestListener() {
                                            @Override
                                            public void onComplete(String s) {
                                                mStatuses.remove(status);
                                                notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onWeiboException(WeiboException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                break;
                            case 3:
                                intent2WriteStatusActivity(status.retweeted_status);
                                break;
                        }
                    }
                });
    }

    private void intent2WriteStatusActivity(Status status) {
        Intent intent = new Intent(mContext, WriteStatusActivity.class);
        intent.putExtra("mStatus", status);
        mContext.startActivity(intent);
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

    private void setImages(final Status status, FrameLayout imgContainer,
                           WrapHeightGridView gvImages, ImageView ivImage) {
        ArrayList<String> pic_urls = status.pic_urls;

        if (pic_urls != null && pic_urls.size() > 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gvImages.setVisibility(View.VISIBLE);
            ivImage.setVisibility(View.GONE);

            StatusGridImgsAdapter gvAdapter = new StatusGridImgsAdapter(mContext, pic_urls);
            gvImages.setAdapter(gvAdapter);
            gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                    intent.putExtra("mStatus", status);
                    intent.putExtra("mPosition", position);
                    mContext.startActivity(intent);
                }
            });
            setGridViewHeightBaseOnChildren(gvImages);
        } else if (pic_urls != null && pic_urls.size() == 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gvImages.setVisibility(View.GONE);
            ivImage.setVisibility(View.VISIBLE);

            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                    intent.putExtra("mStatus", status);
                    mContext.startActivity(intent);
                }
            });

            mImageLoader.displayImage(status.bmiddle_pic, ivImage);
        } else {
            imgContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 解决GridView高度显示不全的问题
     *
     * @param gvImages 要调整高度的GridView
     */
    private void setGridViewHeightBaseOnChildren(final WrapHeightGridView gvImages) {
        //必须在View.post()方法中进行，否则会测量不到GridView和Adapter里面Item的高度
        gvImages.post(new Runnable() {
            @Override
            public void run() {
                ListAdapter adapter = gvImages.getAdapter();
                if (adapter == null) {
                    return;
                }

                int totalHeight;
                int itemCount = adapter.getCount();
                int spacingCount;
                View itemView = adapter.getView(0, null, gvImages);
                itemView.measure(0, 0);
                int itemHeight = itemView.getMeasuredHeight();

                if (itemCount <= 3) {
                    totalHeight = itemHeight;
                    spacingCount = 0;
                } else if (itemCount <= 6) {
                    totalHeight = itemHeight * 2;
                    spacingCount = 1;
                } else {
                    totalHeight = itemHeight * 3;
                    spacingCount = 2;
                }

                ViewGroup.LayoutParams params = gvImages.getLayoutParams();
                params.height = totalHeight + gvImages.getHorizontalSpacing() * spacingCount;

                gvImages.setLayoutParams(params);
            }
        });
    }

    private static class ViewHolder {
        LinearLayout ll_card_content;
        ImageView iv_avatar;
        RelativeLayout rl_content;
        TextView tv_subhead;
        TextView tv_caption;

        TextView tv_content;
        FrameLayout include_status_image;
        WrapHeightGridView gv_images;
        ImageView iv_image;

        LinearLayout include_retweeted_status;
        TextView tv_retweeted_content;
        FrameLayout include_retweeted_status_image;
        WrapHeightGridView gv_retweeted_images;
        ImageView iv_retweeted_image;

        LinearLayout ll_share_bottom;
        ImageView iv_share_bottom;
        TextView tv_share_bottom;
        LinearLayout ll_comment_bottom;
        ImageView iv_comment_bottom;
        TextView tv_comment_bottom;
        LinearLayout ll_like_bottom;
        ImageView iv_like_bottom;
        TextView tv_like_bottom;
    }

}