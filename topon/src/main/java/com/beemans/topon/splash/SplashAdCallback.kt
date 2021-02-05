package com.beemans.topon.splash

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
class SplashAdCallback {

    var onAdRequest: (() -> Unit)? = null

    var onAdLoadSuc: (() -> Unit)? = null

    var onAdLoadFail: ((error: AdError?) -> Unit)? = null

    var onAdLoadTimeOut: (() -> Unit)? = null

    var onAdRenderSuc: ((flAdView: FrameLayout) -> Unit)? = null

    var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClose: ((info: ATAdInfo?) -> Boolean)? = null

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
    fun onAdLoadFail(onAdLoadFail: (error: AdError?) -> Unit) {
        this.onAdLoadFail = onAdLoadFail
    }

    /**
     * 广告超时
     */
    fun onAdLoadTimeOut(onAdLoadTimeOut: () -> Unit) {
        this.onAdLoadTimeOut = onAdLoadTimeOut
    }

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: (flAdView: FrameLayout) -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
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
    fun onAdClose(onAdClose: (info: ATAdInfo?) -> Boolean) {
        this.onAdClose = onAdClose
    }
}