package com.maple.beautyjournal;

import android.app.Application;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

import com.i2mobi.utils.Utils;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;
/**
 * Created by tian on 13-7-19.
 */
public class BeautyJournalApplication extends Application {
    private static final int IMAGE_LOADER_THREAD_POOL_SIZE = 3;
    private static final int MEMORY_CACHE_SIZE = 2 * 1024 * 1024;
    private static final int DISC_CACHE_SIZE = 10 * 1024 * 1024;

    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .memoryCacheExtraOptions(width, height).threadPoolSize(IMAGE_LOADER_THREAD_POOL_SIZE)
                .memoryCache(new LRULimitedMemoryCache(MEMORY_CACHE_SIZE)).memoryCacheSize(MEMORY_CACHE_SIZE)
                .discCache(new TotalSizeLimitedDiscCache(Utils.getCacheDir(getApplicationContext()), DISC_CACHE_SIZE))
                .build();
        ImageLoader.getInstance().init(configuration);
        
        //youmeng
		MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
    }
}
