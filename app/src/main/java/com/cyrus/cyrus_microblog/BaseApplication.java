package com.cyrus.cyrus_microblog;

import android.app.Application;
import android.content.Context;

import com.cyrus.cyrus_microblog.utils.ImageOptHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 在Application创建时进行第三方库的配置
 * 并将该类配置到AndroidManifest.xml中
 *
 * Created by Cyrus on 2016/9/2.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader(this);
    }

    /**
     * 初始化Universal-Image-Loader
     *
     * @param context application上下文
     */
    private void initImageLoader(Context context) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(ImageOptHelper.getImgOptions())
                .build();
        ImageLoader.getInstance().init(configuration);
    }
}
