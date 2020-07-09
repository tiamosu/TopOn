package com.beemans.topon.splash

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
class SplashAdCallback {

    internal var onAdRenderSuc: ((flAdView: FrameLayout) -> Unit)? = null

    internal var onAdLoaded: (() -> Unit)? = null

    internal var onNoAdError: ((error: AdError?) -> Unit)? = null

    internal var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdDismiss: ((info: ATAdInfo?) -> Boolean)? = null

    internal var onAdTick: ((tickTime: Long) -> Unit)? = null

    internal var onAdTimeOut: (() -> Unit)? = null

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: (flAdView: FrameLayout) -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告加载成功
     */
    fun onAdLoaded(onAdLoaded: () -> Unit) {
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

    /**
     * 广告超时
     */
    fun onAdTimeOut(onAdTimeOut: () -> Unit) {
        this.onAdTimeOut = onAdTimeOut
    }
}