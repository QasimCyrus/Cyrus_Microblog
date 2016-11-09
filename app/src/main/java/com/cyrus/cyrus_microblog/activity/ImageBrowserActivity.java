package com.cyrus.cyrus_microblog.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cyrus.cyrus_microblog.BaseActivity;
import com.cyrus.cyrus_microblog.R;
import com.cyrus.cyrus_microblog.adapter.ImageBrowserAdapter;
import com.cyrus.cyrus_microblog.model.PicUrls;
import com.cyrus.cyrus_microblog.utils.PermissionsChecker;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;

public class ImageBrowserActivity extends BaseActivity implements OnClickListener {
    private static final String PERMISSION_WRITE_EXTERNAL_STORAGE
            = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private ViewPager mVpImageBrowser;
    private TextView mTvImageIndex;
    private Button mBtnSave;

    private Status mStatus;
    private ImageBrowserAdapter mImageBrowserAdapter;
    private PermissionsChecker mPermissionsChecker;

    private int mPosition;
    private boolean mHasExternalStoragePermissionGot;
    private ArrayList<String> mStrImgUrls;
    private ArrayList<PicUrls> mImgUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_brower);

        initData();
        initView();
        setData();
    }

    private void initData() {
        mPermissionsChecker = new PermissionsChecker(this);
        mStatus = (Status) getIntent().getSerializableExtra("mStatus");
        mPosition = getIntent().getIntExtra("mPosition", 0);
        // 获取图片数据集合(单图也有对应的集合,集合的size为1)
        mStrImgUrls = mStatus.pic_urls;
        mImgUrls = new ArrayList<>();
        for (String urlStr : mStrImgUrls) {
            PicUrls picUrls = new PicUrls(urlStr);
            mImgUrls.add(picUrls);
        }
    }

    private void initView() {
        mVpImageBrowser = (ViewPager) findViewById(R.id.vp_image_browser);
        mTvImageIndex = (TextView) findViewById(R.id.tv_image_index);
        mBtnSave = (Button) findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
    }

    private void setData() {
        mImageBrowserAdapter = new ImageBrowserAdapter(this, mImgUrls);
        mVpImageBrowser.setAdapter(mImageBrowserAdapter);

        final int size = mImgUrls.size();

        if (size > 1) {
            mTvImageIndex.setVisibility(View.VISIBLE);
            mTvImageIndex.setText((mPosition + 1) + "/" + size);
        } else {
            mTvImageIndex.setVisibility(View.GONE);
        }

        mVpImageBrowser.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                mTvImageIndex.setText((arg0 + 1) + "/" + size);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mVpImageBrowser.setCurrentItem(mPosition);
    }

    @Override
    public void onClick(View v) {
        PicUrls picUrl = mImageBrowserAdapter.getPic(mVpImageBrowser.getCurrentItem());

        switch (v.getId()) {
            case R.id.btn_save:
                checkExternalStoragePermission();
                if (mHasExternalStoragePermissionGot) {
                    Bitmap bitmap = mImageBrowserAdapter.getBitmap(mVpImageBrowser.getCurrentItem());

                    String fileName = "img-ori-" + picUrl.getImageId();

                    String title = fileName.substring(0, fileName.lastIndexOf("."));
                    String insertImage = MediaStore.Images.Media.insertImage(
                            getContentResolver(), bitmap, title, "MicroblogImage");
                    if (insertImage == null) {
                        showToast("图片保存失败");
                    } else {
                        showToast("图片保存成功");
                    }

//                    try {
//                        ImageUtils.saveFile(this, bitmap, fileName);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    showToast("需要读写SD卡的权限");
                }

                break;
        }
    }

    private void checkExternalStoragePermission() {
        if (mPermissionsChecker.lacksPermissions(PERMISSION_WRITE_EXTERNAL_STORAGE)) {
            mHasExternalStoragePermissionGot = false;
            Intent intent = new Intent(this, PermissionsActivity.class);
            intent.putExtra(PermissionsActivity.EXTRA_EXTERNAL,
                    new String[]{PERMISSION_WRITE_EXTERNAL_STORAGE});
            startActivityForResult(intent, PermissionsActivity.PERMISSIONS_REQUEST_CODE);
        } else {
            mHasExternalStoragePermissionGot = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PermissionsActivity.PERMISSIONS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mHasExternalStoragePermissionGot = true;
            } else if (resultCode == RESULT_CANCELED) {
                mHasExternalStoragePermissionGot = false;
            }
        }
    }
}
