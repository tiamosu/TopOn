package com.beemans.topon.nativead.splash

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeSplashCallback {

    var onAdRequest: (() -> Unit)? = null

    var onAdLoadSuc: (() -> Unit)? = null

    var onAdLoadFail: ((errorMsg: String?) -> Unit)? = null

    var onAdRenderSuc: ((flAdView: FrameLayout) -> Unit)? = null

    var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    var onAdSkip: (() -> Boolean)? = null

    var onAdTick: ((tickTime: Long) -> Unit)? = null

    var onAdTimeOver: (() -> Unit)? = null

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
    fun onAdLoadFail(onAdLoadFail: (errorMsg: String?) -> Unit) {
        this.onAdLoadFail = onAdLoadFail
    }

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: (flAdView: FrameLayout) -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
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
     * 点击广告跳过
     */
    fun onAdSkip(onAdSkip: () -> Boolean) {
        this.onAdSkip = onAdSkip
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