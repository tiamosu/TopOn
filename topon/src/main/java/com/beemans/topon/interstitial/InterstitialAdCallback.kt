package com.beemans.topon.interstitial

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class InterstitialAdCallback {

    internal var onAdRenderSuc: (() -> Unit)? = null

    internal var onInterstitialAdTimeOut: (() -> Unit)? = null

    internal var onInterstitialAdLoaded: (() -> Unit)? = null

    internal var onInterstitialAdLoadFail: ((error: AdError?) -> Unit)? = null

    internal var onInterstitialAdClicked: ((info: ATAdInfo?) -> Unit)? = null

    internal var onInterstitialAdShow: ((info: ATAdInfo?) -> Unit)? = null

    internal var onInterstitialAdClose: ((info: ATAdInfo?) -> Unit)? = null

    internal var onInterstitialAdVideoStart: ((info: ATAdInfo?) -> Unit)? = null

    internal var onInterstitialAdVideoEnd: ((info: ATAdInfo?) -> Unit)? = null

    internal var onInterstitialAdVideoError: ((error: AdError?) -> Unit)? = null

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: () -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告加载超时
     */
    fun onInterstitialAdTimeOut(onInterstitialAdTimeOut: () -> Unit) {
        this.onInterstitialAdTimeOut = onInterstitialAdTimeOut
    }

    /**
     * 广告加载成功回调
     */
    fun onInterstitialAdLoaded(onInterstitialAdLoaded: () -> Unit) {
        this.onInterstitialAdLoaded = onInterstitialAdLoaded
    }

    /**
     * 广告加载失败回调
     */
    fun onInterstitialAdLoadFail(onInterstitialAdLoadFail: (error: AdError?) -> Unit) {
        this.onInterstitialAdLoadFail = onInterstitialAdLoadFail
    }

    /**
     * 广告点击
     */
    fun onInterstitialAdClicked(onInterstitialAdClicked: (info: ATAdInfo?) -> Unit) {
        this.onInterstitialAdClicked = onInterstitialAdClicked
    }

    /**
     * 广告展示回调
     */
    fun onInterstitialAdShow(onInterstitialAdShow: (info: ATAdInfo?) -> Unit) {
        this.onInterstitialAdShow = onInterstitialAdShow
    }

    /**
     * 广告关闭回调
     */
    fun onInterstitialAdClose(onInterstitialAdClose: (info: ATAdInfo?) -> Unit) {
        this.onInterstitialAdClose = onInterstitialAdClose
    }

    /**
     * 视频广告刷新回调
     */
    fun onInterstitialAdVideoStart(onInterstitialAdVideoStart: (info: ATAdInfo?) -> Unit) {
        this.onInterstitialAdVideoStart = onInterstitialAdVideoStart
    }

    /**
     * 视频广告播放结束
     */
    fun onInterstitialAdVideoEnd(onInterstitialAdVideoEnd: (info: ATAdInfo?) -> Unit) {
        this.onInterstitialAdVideoEnd = onInterstitialAdVideoEnd
    }

    /**
     * 视频广告播放失败回调
     */
    fun onInterstitialAdVideoError(onInterstitialAdVideoError: (error: AdError?) -> Unit) {
        this.onInterstitialAdVideoError = onInterstitialAdVideoError
    }
}