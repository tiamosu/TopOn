package com.beemans.topon.interstitial

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class InterstitialAdCallback {

    internal var onAdRenderSuc: (() -> Unit)? = null

    internal var onAdTimeOut: (() -> Unit)? = null

    internal var onAdLoaded: (() -> Unit)? = null

    internal var onAdLoadFail: ((error: AdError?) -> Unit)? = null

    internal var onAdClicked: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdClose: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdVideoStart: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdVideoEnd: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdVideoError: ((error: AdError?) -> Unit)? = null

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: () -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告加载超时
     */
    fun onAdTimeOut(onAdTimeOut: () -> Unit) {
        this.onAdTimeOut = onAdTimeOut
    }

    /**
     * 广告加载成功回调
     */
    fun onAdLoaded(onAdLoaded: () -> Unit) {
        this.onAdLoaded = onAdLoaded
    }

    /**
     * 广告加载失败回调
     */
    fun onAdLoadFail(onAdLoadFail: (error: AdError?) -> Unit) {
        this.onAdLoadFail = onAdLoadFail
    }

    /**
     * 广告点击
     */
    fun onAdClicked(onAdClicked: (info: ATAdInfo?) -> Unit) {
        this.onAdClicked = onAdClicked
    }

    /**
     * 广告展示回调
     */
    fun onAdShow(onAdShow: (info: ATAdInfo?) -> Unit) {
        this.onAdShow = onAdShow
    }

    /**
     * 广告关闭回调
     */
    fun onAdClose(onAdClose: (info: ATAdInfo?) -> Unit) {
        this.onAdClose = onAdClose
    }

    /**
     * 视频广告刷新回调
     */
    fun onAdVideoStart(onAdVideoStart: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoStart = onAdVideoStart
    }

    /**
     * 视频广告播放结束
     */
    fun onAdVideoEnd(onAdVideoEnd: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoEnd = onAdVideoEnd
    }

    /**
     * 视频广告播放失败回调
     */
    fun onAdVideoError(onAdVideoError: (error: AdError?) -> Unit) {
        this.onAdVideoError = onAdVideoError
    }
}