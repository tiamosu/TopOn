package com.beemans.topon.splash

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
class SplashAdCallback {

    internal var onAdLoaded: ((flAd: FrameLayout) -> Unit)? = null

    internal var onNoAdError: ((error: AdError?) -> Unit)? = null

    internal var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdDismiss: ((info: ATAdInfo?) -> Boolean)? = null

    internal var onAdTick: ((tickTime: Long) -> Unit)? = null

    fun onAdLoaded(onAdLoaded: (flAd: FrameLayout) -> Unit) {
        this.onAdLoaded = onAdLoaded
    }

    /**
     * 广告加载失败
     */
    fun onNoAdError(onNoAdError: (error: AdError?) -> Unit) {
        this.onNoAdError = onNoAdError
    }

    /**
     * 广告展示
     */
    fun onAdShow(onAdShow: (info: ATAdInfo?) -> Unit) {
        this.onAdShow = onAdShow
    }

    /**
     * 广告点击
     */
    fun onAdClick(onAdClick: (info: ATAdInfo?) -> Unit) {
        this.onAdClick = onAdClick
    }

    /**
     * 广告关闭回调
     */
    fun onAdDismiss(onAdDismiss: (info: ATAdInfo?) -> Boolean) {
        this.onAdDismiss = onAdDismiss
    }

    /**
     * 广告的倒计时回调
     */
    fun onAdTick(onAdTick: (tickTime: Long) -> Unit) {
        this.onAdTick = onAdTick
    }
}