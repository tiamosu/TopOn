package com.beemans.topon.ext

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.tiamosu.fly.base.BaseFlyActivity
import com.tiamosu.fly.base.BaseFlyFragment
import com.tiamosu.fly.http.imageloader.ImageLoader
import com.tiamosu.fly.imageloader.glide.ImageConfigImpl

/**
 * @author tiamosu
 * @date 2020/8/18.
 */
val LifecycleOwner.context: FragmentActivity
    get() {
        return when (this) {
            is BaseFlyFragment -> context
            is Fragment -> this.requireActivity()
            is BaseFlyActivity -> this
            is FragmentActivity -> this
            else -> throw IllegalArgumentException("owner must is Fragment or FragmentActivity")
        }
    }

/**
 * 添加广告 View
 */
fun ViewGroup.addAdView(adView: View) {
    if (childCount > 0) {
        removeAllViews()
    }
    addView(adView)
}

/**
 * 加载广告图片
 */
fun ImageView.loadAdImage(any: Any) {
    ImageConfigImpl
        .load(any)
        .into(this)
        .build()
        .let(ImageLoader::loadImage)
}