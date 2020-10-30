package com.beemans.topon.nativead

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.nativead.api.ATNativeAdView

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdCallback {

    var onAdRequest: (() -> Unit)? = null

    var onAdRenderSuc: ((flAdView: FrameLayout) -> Unit)? = null

    var onAdLoadFail: ((adError: AdError?, isManualRequested: Boolean) -> Unit)? = null

    var onAdLoaded: (() -> Unit)? = null

    var onAdVideoStart: ((view: ATNativeAdView?) -> Unit)? = null

    var onAdVideoEnd: ((view: ATNativeAdView?) -> Unit)? = null

    var onAdVideoProgress: ((view: ATNativeAdView?, progress: Int) -> Unit)? = null

    var onAdClicked: ((view: ATNativeAdView?, info: ATAdInfo?) -> Unit)? = null

    var onAdImpressed: ((view: ATNativeAdView?, info: ATAdInfo?) -> Unit)? = null

    var onAdCloseClick: ((view: ATNativeAdView?, info: ATAdInfo?) -> Boolean)? = null

    /**
     * 广告请求
     */
    fun onAdRequest(onAdRequest: () -> Unit) {
        this.onAdRequest = onAdRequest
    }

    /**
     * 广告加载失败
     */
    fun onAdLoadFail(onAdLoadFail: (adError: AdError?, isManualRequested: Boolean) -> Unit) {
        this.onAdLoadFail = onAdLoadFail
    }

    /**
     * 广告加载成功
     */
    fun onAdLoaded(onAdLoaded: () -> Unit) {
        this.onAdLoaded = onAdLoaded
    }

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: (flAdView: FrameLayout) -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告视频播放开始（仅部分广告平台存在）
     */
    fun onAdVideoStart(onAdVideoStart: (view: ATNativeAdView?) -> Unit) {
        this.onAdVideoStart = onAdVideoStart
    }

    /**
     * 广告视频播放结束（仅部分广告平台存在）
     */
    fun onAdVideoEnd(onAdVideoEnd: (view: ATNativeAdView?) -> Unit) {
        this.onAdVideoEnd = onAdVideoEnd
    }

    /**
     * 广告视频播放进度（仅部分广告平台存在）
     */
    fun onAdVideoProgress(onAdVideoProgress: (view: ATNativeAdView?, progress: Int) -> Unit) {
        this.onAdVideoProgress = onAdVideoProgress
    }

    /**
     * 点击广告
     */
    fun onAdClicked(onAdClicked: (view: ATNativeAdView?, info: ATAdInfo?) -> Unit) {
        this.onAdClicked = onAdClicked
    }

    /**
     * 广告展示回调
     */
    fun onAdImpressed(onAdImpressed: (view: ATNativeAdView?, info: ATAdInfo?) -> Unit) {
        this.onAdImpressed = onAdImpressed
    }

    /**
     * 对广告不感兴趣等，进行广告关闭点击
     */
    fun onAdCloseClick(onAdCloseClick: (view: ATNativeAdView?, info: ATAdInfo?) -> Boolean) {
        this.onAdCloseClick = onAdCloseClick
    }
}