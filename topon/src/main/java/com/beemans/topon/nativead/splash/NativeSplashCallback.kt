package com.beemans.topon.nativead.splash

import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeSplashCallback {

    internal var onAdLoaded: ((
        frameLayout: FrameLayout,
        layoutParams: ViewGroup.LayoutParams
    ) -> Unit)? = null

    /**
     * 广告加载成功
     */
    fun onAdLoaded(
        onAdLoaded: (
            frameLayout: FrameLayout,
            layoutParams: ViewGroup.LayoutParams
        ) -> Unit
    ) {
        this.onAdLoaded = onAdLoaded
    }
}