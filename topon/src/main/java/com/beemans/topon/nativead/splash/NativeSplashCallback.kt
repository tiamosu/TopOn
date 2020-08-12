package com.beemans.topon.nativead.splash

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeSplashCallback {

    var onAdRenderSuc: ((flAdView: FrameLayout) -> Unit)? = null

    var onAdLoaded: (() -> Unit)? = null

    var onAdError: ((errorMsg: String?) -> Unit)? = null

    var onAdSkip: (() -> Boolean)? = null

    var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    var onAdTick: ((tickTime: Long) -> Unit)? = null

    var onAdTimeOver: (() -> Unit)? = null

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
    fun onAdError(onAdError: (errorMsg: String?) -> Unit) {
        this.onAdError = onAdError
    }

    /**
     * 点击广告跳过
     */
    fun onAdSkip(onAdSkip: () -> Boolean) {
        this.onAdSkip = onAdSkip
    }

    /**
     * 广告展示回调
     */
    fun onAdShow(onAdShow: (info: ATAdInfo?) -> Unit) {
        this.onAdShow = onAdShow
    }

    /**
     * 点击广告
     */
    fun onAdClick(onAdClick: (info: ATAdInfo?) -> Unit) {
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