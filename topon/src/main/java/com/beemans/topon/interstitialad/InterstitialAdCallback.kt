package com.beemans.topon.interstitialad

import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class InterstitialAdCallback {

    internal var onInterstitialSuc: (() -> Unit)? = null

    internal var onInterstitialAdTimeOut: (() -> Unit)? = null

    internal var onInterstitialAdLoaded: (() -> Unit)? = null

    internal var onInterstitialAdLoadFail: ((error: AdError?) -> Unit)? = null

    internal var onInterstitialAdClicked: (() -> Unit)? = null

    internal var onInterstitialAdShow: (() -> Unit)? = null

    internal var onInterstitialAdClose: (() -> Unit)? = null

    internal var onInterstitialAdVideoStart: (() -> Unit)? = null

    internal var onInterstitialAdVideoEnd: (() -> Unit)? = null

    internal var onInterstitialAdVideoError: (() -> Unit)? = null

    /**
     * 广告渲染成功
     */
    fun onInterstitialSuc(onInterstitialSuc: () -> Unit) {
        this.onInterstitialSuc = onInterstitialSuc
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
    fun onInterstitialAdClicked(onInterstitialAdClicked: () -> Unit) {
        this.onInterstitialAdClicked = onInterstitialAdClicked
    }

    /**
     * 广告展示回调
     */
    fun onInterstitialAdShow(onInterstitialAdShow: () -> Unit) {
        this.onInterstitialAdShow = onInterstitialAdShow
    }

    /**
     * 广告关闭回调
     */
    fun onInterstitialAdClose(onInterstitialAdClose: () -> Unit) {
        this.onInterstitialAdClose = onInterstitialAdClose
    }

    /**
     * 视频广告刷新回调
     */
    fun onInterstitialAdVideoStart(onInterstitialAdVideoStart: () -> Unit) {
        this.onInterstitialAdVideoStart = onInterstitialAdVideoStart
    }

    /**
     * 视频广告播放结束
     */
    fun onInterstitialAdVideoEnd(onInterstitialAdVideoEnd: () -> Unit) {
        this.onInterstitialAdVideoEnd = onInterstitialAdVideoEnd
    }

    /**
     * 视频广告播放失败回调
     */
    fun onInterstitialAdVideoError(onInterstitialAdVideoError: () -> Unit) {
        this.onInterstitialAdVideoError = onInterstitialAdVideoError
    }
}