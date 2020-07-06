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

    internal var onNoAdError: ((errorMsg: String?) -> Unit)? = null

    internal var onAdSkip: (() -> Unit)? = null

    internal var onAdClick: (() -> Unit)? = null

    internal var onAdTick: ((tickTime: Long) -> Unit)? = null

    internal var onAdTimeOver: (() -> Unit)? = null

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

    /**
     * 广告加载失败
     */
    fun onNoAdError(onNoAdError: (errorMsg: String?) -> Unit) {
        this.onNoAdError = onNoAdError
    }

    /**
     * 点击广告跳过
     */
    fun onAdSkip(onAdSkip: () -> Unit) {
        this.onAdSkip = onAdSkip
    }

    /**
     * 点击广告
     */
    fun onAdClick(onAdClick: () -> Unit) {
        this.onAdClick = onAdClick
    }

    /**
     * 广告倒计时
     */
    fun onAdTick(onAdTick: (tickTime: Long) -> Unit) {
        this.onAdTick = onAdTick
    }

    /**
     * 广告播放完毕
     */
    fun onAdTimeOver(onAdTimeOver: () -> Unit) {
        this.onAdTimeOver = onAdTimeOver
    }
}