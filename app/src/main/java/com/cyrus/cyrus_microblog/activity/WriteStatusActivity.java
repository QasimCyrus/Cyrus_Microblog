package com.cyrus.cyrus_microblog.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyrus.cyrus_microblog.BaseActivity;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.adapter.EmotionGvAdapter;
import com.cyrus.cyrus_microblog.adapter.EmotionPagerAdapter;
import com.cyrus.cyrus_microblog.adapter.WriteStatusGridImgsAdapter;
import com.cyrus.cyrus_microblog.api.SimpleRequestListener;
import com.cyrus.cyrus_microblog.api.StatusesApi;
import com.cyrus.cyrus_microblog.model.Emotion;
import com.cyrus.cyrus_microblog.utils.DialogUtils;
import com.cyrus.cyrus_microblog.utils.DisplayUtils;
import com.cyrus.cyrus_microblog.utils.ImageUtils;
import com.cyrus.cyrus_microblog.utils.StringUtils;
import com.cyrus.cyrus_microblog.utils.TitleBarBuilder;
import com.cyrus.cyrus_microblog.widget.WrapHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

public class WriteStatusActivity extends BaseActivity
        implements OnClickListener, OnItemClickListener {

    /**
     * 要发送图片的列表
     */
    private ArrayList<Uri> mImgUris;
    /**
     * 引用的微博
     */
    private Status mRetweetedStatus;
    /**
     * 显示在页面中,实际需要转发内容的微博
     */
    private Status mCardStatus;
    /**
     * 封装微博结构功能的Api
     */
    private StatusesApi mStatusesApi;

    // 输入框
    private EditText mEtWriteStatus;
    // 添加的九宫格图片和适配器
    private WrapHeightGridView mGvWriteStatus;
    private WriteStatusGridImgsAdapter mStatusGridImgsAdapter;
    // 转发微博内容
    private View mIncludeRetweetedStatusCard;
    private ImageView mIvRstatusImg;
    private TextView mTvRstatusUsername;
    private TextView mTvRstatusContent;
    // 底部添加栏
    private ImageView mIvImage;
    private ImageView mIvAt;
    private ImageView mIvTopic;
    private ImageView mIvEmoji;
    private ImageView mIvAdd;
    // 表情选择面板
    private LinearLayout mLlEmotionDashboard;
    private ViewPager mVpEmotionDashboard;
    private EmotionPagerAdapter mEmotionPagerAdapter;
    // 进度框
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_status);

        mRetweetedStatus = (Status) getIntent().getSerializableExtra("mStatus");
        mStatusesApi = new StatusesApi(this);
        mImgUris = new ArrayList<>();

        initView();
    }

    private void initView() {
        // 标题栏
        new TitleBarBuilder(this)
                .setTitleText("发微博")
                .setLeftText("取消")
                .setLeftOnClickListener(this)
                .setRightText("发送")
                .setRightOnClickListener(this)
                .build();
        // 输入框
        mEtWriteStatus = (EditText) findViewById(R.id.et_write_status);
        // 添加的九宫格图片
        mGvWriteStatus = (WrapHeightGridView) findViewById(R.id.gv_write_status);
        // 转发微博内容
        mIncludeRetweetedStatusCard = findViewById(R.id.include_retweeted_status_card);
        mIvRstatusImg = (ImageView) findViewById(R.id.iv_rstatus_img);
        mTvRstatusUsername = (TextView) findViewById(R.id.tv_rstatus_username);
        mTvRstatusContent = (TextView) findViewById(R.id.tv_rstatus_content);
        // 底部添加栏
        mIvImage = (ImageView) findViewById(R.id.iv_image);
        mIvAt = (ImageView) findViewById(R.id.iv_at);
        mIvTopic = (ImageView) findViewById(R.id.iv_topic);
        mIvEmoji = (ImageView) findViewById(R.id.iv_emoji);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        // 表情选择面板
        mLlEmotionDashboard = (LinearLayout) findViewById(R.id.ll_emotion_dashboard);
        mVpEmotionDashboard = (ViewPager) findViewById(R.id.vp_emotion_dashboard);
        // 进度框
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("微博发布中...");

        mStatusGridImgsAdapter = new WriteStatusGridImgsAdapter(this, mImgUris, mGvWriteStatus);
        mGvWriteStatus.setAdapter(mStatusGridImgsAdapter);
        mGvWriteStatus.setOnItemClickListener(this);

        mIvImage.setOnClickListener(this);
        mIvAt.setOnClickListener(this);
        mIvTopic.setOnClickListener(this);
        mIvEmoji.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);

        initRetweetedStatus();
        initEmotion();
    }

    /**
     * 发送微博
     */
    private void sendStatus() {
        String statusText = mEtWriteStatus.getText().toString();
        if (TextUtils.isEmpty(statusText) && mCardStatus == null) {
            showToast("微博内容不能为空");
            return;
        }

        String imgFilePath = null;
        if (mImgUris.size() > 0) {
            // 微博API中只支持上传一张图片
            Uri uri = mImgUris.get(0);
            imgFilePath = ImageUtils.getImageAbsolutePath(this, uri);
        }

        // 转发微博的id
        long retweetedStatsId = mCardStatus == null ? -1 : Long.parseLong(mCardStatus.id);
        // 上传微博api接口
        mProgressDialog.show();
        mStatusesApi.statusSend(statusText, imgFilePath, retweetedStatsId,
                new SimpleRequestListener(this, mProgressDialog) {
                    @Override
                    public void onComplete(String response) {
                        super.onComplete(response);

                        setResult(RESULT_OK);
                        showToast("微博发送成功");
                        WriteStatusActivity.this.finish();
                    }
                });
    }

    /**
     * 初始化引用微博内容
     */
    private void initRetweetedStatus() {
        // 转发微博特殊处理
        if (mRetweetedStatus != null) {
            // 转发的微博是否包含转发内容
            Status rrStatus = mRetweetedStatus.retweeted_status;
            if (rrStatus != null) {
                String content = "//@" + mRetweetedStatus.user.name
                        + ":" + mRetweetedStatus.text;
                mEtWriteStatus.setText(StringUtils
                        .getSpannableString(this, mEtWriteStatus, content));
                mEtWriteStatus.setSelection(0);
                // 如果引用的为转发微博,则使用它转发的内容
                mCardStatus = rrStatus;
            } else {
                mEtWriteStatus.setText("转发微博");
                // 如果引用的为原创微博,则使用它自己的内容
                mCardStatus = mRetweetedStatus;
            }

            // 设置转发图片内容
            String imgUrl = mCardStatus.thumbnail_pic;
            if (TextUtils.isEmpty(imgUrl)) {
                mIvRstatusImg.setVisibility(View.GONE);
            } else {
                mIvRstatusImg.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(mCardStatus.thumbnail_pic, mIvRstatusImg);
            }
            // 设置转发文字内容
            mTvRstatusUsername.setText(String.format("@%s", mCardStatus.user.name));
            mTvRstatusContent.setText(mCardStatus.text);

            // 转发微博时,不能添加图片
            mIvImage.setVisibility(View.GONE);
            mIncludeRetweetedStatusCard.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化表情面板内容
     */
    private void initEmotion() {
        // 获取屏幕宽度
        int gvWidth = DisplayUtils.getScreenWidthPixels(this);
        // 表情边距
        int spacing = DisplayUtils.dp2px(this, 8);
        // GridView中item的宽度
        int itemWidth = (gvWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<>();
        List<String> emotionNames = new ArrayList<>();
        // 遍历所有的表情名字
        for (String emojiName : Emotion.emojiMap.keySet()) {
            emotionNames.add(emojiName);
            // 每20个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<>();
            }
        }

        // 检查最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

        // 将多个GridView添加显示到ViewPager中
        mEmotionPagerAdapter = new EmotionPagerAdapter(gvs);
        mVpEmotionDashboard.setAdapter(mEmotionPagerAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
        mVpEmotionDashboard.setLayoutParams(params);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth,
                                           int padding, int itemWidth, int gvHeight) {
        // 创建GridView
        GridView gvEmotion = new GridView(this);
        gvEmotion.setBackgroundResource(R.color.bg_gray);
        gvEmotion.setSelector(R.color.transparent);
        gvEmotion.setNumColumns(7);
        gvEmotion.setPadding(padding, padding, padding, padding);
        gvEmotion.setHorizontalSpacing(padding);
        gvEmotion.setVerticalSpacing(padding);
        LayoutParams params = new LayoutParams(gvWidth, gvHeight);
        gvEmotion.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
        gvEmotion.setAdapter(adapter);
        gvEmotion.setOnItemClickListener(this);
        return gvEmotion;
    }

    /**
     * 更新图片显示
     */
    private void updateImgs() {
        if (mImgUris.size() > 0) {
            // 如果有图片则显示GridView,同时更新内容
            mGvWriteStatus.setVisibility(View.VISIBLE);
            mStatusGridImgsAdapter.notifyDataSetChanged();
        } else {
            // 无图则不显示GridView
            mGvWriteStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_titlebar_left:
                finish();
                break;
            case R.id.tv_titlebar_right:
                sendStatus();
                break;
            case R.id.iv_image:
                DialogUtils.showImagePickDialog(this);
                break;
            case R.id.iv_at:
                break;
            case R.id.iv_topic:
                break;
            case R.id.iv_emoji:
                if (mLlEmotionDashboard.getVisibility() == View.VISIBLE) {
                    // 显示表情面板时点击,将按钮图片设为笑脸按钮,同时隐藏面板
                    mIvEmoji.setImageResource(R.drawable.btn_insert_emotion);
                    mLlEmotionDashboard.setVisibility(View.GONE);
                } else {
                    // 未显示表情面板时点击,将按钮图片设为键盘,同时显示面板
                    mIvEmoji.setImageResource(R.drawable.btn_insert_keyboard);
                    mLlEmotionDashboard.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_add:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdapter = parent.getAdapter();

        if (itemAdapter instanceof WriteStatusGridImgsAdapter) {
            // 点击的是添加的图片
            if (position == mStatusGridImgsAdapter.getCount() - 1) {
                // 如果点击了最后一个加号图标,则显示选择图片对话框
                DialogUtils.showImagePickDialog(this);
            }
        } else if (itemAdapter instanceof EmotionGvAdapter) {
            // 点击的是表情
            EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;

            if (position == emotionGvAdapter.getCount() - 1) {
                // 如果点击了最后一个回退按钮,则调用删除键事件
                mEtWriteStatus.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                // 如果点击了表情,则添加到输入框中
                String emotionName = emotionGvAdapter.getItem(position);

                // 获取当前光标位置,在指定位置上添加表情图片文本
                int curPosition = mEtWriteStatus.getSelectionStart();
                StringBuilder sb = new StringBuilder(mEtWriteStatus.getText().toString());
                sb.insert(curPosition, emotionName);

                // 特殊文字处理,将表情等转换一下
                mEtWriteStatus.setText(StringUtils.getSpannableString(
                        this, mEtWriteStatus, sb.toString()));

                // 将光标设置到新增完表情的右侧
                mEtWriteStatus.setSelection(curPosition + emotionName.length());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                if (resultCode == RESULT_CANCELED) {
                    // 如果拍照取消,将之前新增的图片地址删除
                    ImageUtils.deleteImageUri(this, ImageUtils.sImageUriFromCamera);
                } else {
//				// 拍照后将图片添加到页面上
//				mImgUris.add(ImageUtils.sImageUriFromCamera);
//				updateImgs();

                    // crop
                    ImageUtils.cropImage(this, ImageUtils.sImageUriFromCamera);
                }
            case ImageUtils.CROP_IMAGE:
                if (resultCode != RESULT_CANCELED) {
                    mImgUris.add(ImageUtils.sCropImageUri);
                    updateImgs();
                }
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if (resultCode != RESULT_CANCELED) {
                    // 本地相册选择完后将图片添加到页面上
                    mImgUris.add(data.getData());
                    updateImgs();
                }
                break;
            default:
                break;
        }
    }

}
