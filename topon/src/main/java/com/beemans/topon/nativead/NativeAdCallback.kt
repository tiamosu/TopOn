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

    var onAdLoadSuc: (() -> Unit)? = null

    var onAdLoadFail: ((adError: AdError?) -> Unit)? = null

    var onAdRenderSuc: ((flAdView: FrameLayout, info: ATAdInfo?) -> Unit)? = null

    var onAdShow: ((view: ATNativeAdView?, info: ATAdInfo?) -> Unit)? = null

    var onAdClick: ((view: ATNativeAdView?, info: ATAdInfo?) -> Unit)? = null

    var onAdClose: ((view: ATNativeAdView?, info: ATAdInfo?) -> Boolean)? = null

    var onAdVideoStart: ((view: ATNativeAdView?) -> Unit)? = null

    var onAdVideoEnd: ((view: ATNativeAdView?) -> Unit)? = null

    var onAdVideoProgress: ((view: ATNativeAdView?, progress: Int) -> Unit)? = null

    /**
     * 广告请求
     */
    fun onAdRequest(onAdRequest: () -> Unit) {
        this.onAdRequest = onAdRequest
    }

    /**
     * 广告加载成功
     */
    fun onAdLoadSuc(onAdLoadSuc: () -> Unit) {
        this.onAdLoadSuc = onAdLoadSuc
    }

    /**
     * 广告加载失败
     */
    fun onAdLoadFail(onAdLoadFail: (adError: AdError?) -> Unit) {
        this.onAdLoadFail = onAdLoadFail
    }

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: (flAdView: FrameLayout, info: ATAdInfo?) -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告展示回调
     */
    fun onAdShow(onAdShow: (view: ATNativeAdView?, info: ATAdInfo?) -> Unit) {
        this.onAdShow = onAdShow
    }

    /**
     * 点击广告
     */
    fun onAdClick(onAdClick: (view: ATNativeAdView?, info: ATAdInfo?) -> Unit) {
        this.onAdClick = onAdClick
    }

    /**
     * 对广告不感兴趣等，进行广告关闭点击
     */
    fun onAdClose(onAdClose: (view: ATNativeAdView?, info: ATAdInfo?) -> Boolean) {
        this.onAdClose = onAdClose
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
}