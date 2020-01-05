package com.example.naqi.bebettermuslim

import android.app.Application
import com.example.naqi.bebettermuslim.Utils.Constants
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.realm.Realm

class BeBetterMuslim : Application() {
    lateinit var imageLoader: ImageLoader
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/gothic.TTF")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
        val defaultOptions = DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .displayer(FadeInBitmapDisplayer(300))
            .build()

        val config = ImageLoaderConfiguration.Builder(applicationContext)
            .defaultDisplayImageOptions(defaultOptions)
            .memoryCache(WeakMemoryCache())
            .diskCacheSize(100 * 1024 * 1024)
            .build()

        imageLoader = ImageLoader.getInstance()
        imageLoader.init(config)
    }

    override fun onTerminate() {
        unbindService(Constants.audioConnection)
        super.onTerminate()
    }
}
