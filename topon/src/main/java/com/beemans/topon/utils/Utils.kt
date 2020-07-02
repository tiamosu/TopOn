package com.beemans.topon.utils

import android.widget.ImageView
import com.tiamosu.fly.http.imageloader.ImageLoader
import com.tiamosu.fly.imageloader.glide.ImageConfigImpl

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
object Utils {

    /**
     * 加载图片
     */
    fun loadImage(any: Any?, imageView: ImageView) {
        ImageConfigImpl.load(any)
            .into(imageView)
            .build()
            .let(ImageLoader::loadImage)
    }
}