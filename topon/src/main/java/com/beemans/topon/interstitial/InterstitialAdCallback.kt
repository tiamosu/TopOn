package com.beemans.topon.interstitial

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class InterstitialAdCallback {

    var onAdRequest: (() -> Unit)? = null

    var onAdLoadSuc: ((info: ATAdInfo?) -> Unit)? = null

    var onAdLoadFail: ((error: AdError?) -> Unit)? = null

    var onAdLoadTimeOut: (() -> Unit)? = null

    var onAdRenderSuc: ((info: ATAdInfo?) -> Unit)? = null

    var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClose: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoStart: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoEnd: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoError: ((error: AdError?) -> Unit)? = null

    /**
     * 广告请求
     */
    fun onAdRequest(onAdRequest: () -> Unit) {
        this.onAdRequest = onAdRequest
    }

    /**
     * 广告加载成功回调
     */
    fun onAdLoadSuc(onAdLoadSuc: (info: ATAdInfo?) -> Unit) {
        this.onAdLoadSuc = onAdLoadSuc
    }

    /**
     * 广告加载失败回调
     */
    fun onAdLoadFail(onAdLoadFail: (error: AdError?) -> Unit) {
        this.onAdLoadFail = onAdLoadFail
    }

    /**
     * 广告加载超时
     */
    fun onAdLoadTimeOut(onAdLoadTimeOut: () -> Unit) {
        this.onAdLoadTimeOut = onAdLoadTimeOut
    }

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: (info: ATAdInfo?) -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告展示回调
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