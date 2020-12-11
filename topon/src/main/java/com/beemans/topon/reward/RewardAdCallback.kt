package com.beemans.topon.reward

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
class RewardAdCallback {

    var onAdRequest: (() -> Unit)? = null

    var onAdRenderSuc: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoLoaded: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoFailed: ((error: AdError?) -> Unit)? = null

    var onAdVideoClosed: ((info: ATAdInfo?) -> Unit)? = null

    var onAdReward: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoPlayFailed: ((error: AdError?, info: ATAdInfo?) -> Unit)? = null

    var onAdVideoPlayStart: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoPlayEnd: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoPlayClicked: ((info: ATAdInfo?) -> Unit)? = null

    var onAdTimeOut: (() -> Unit)? = null

    /**
     * 广告请求
     */
    fun onAdRequest(onAdRequest: () -> Unit) {
        this.onAdRequest = onAdRequest
    }

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: (info: ATAdInfo?) -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告加载成功回调
     */
    fun onAdVideoLoaded(onAdVideoLoaded: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoLoaded = onAdVideoLoaded
    }

    /**
     * 广告加载失败回调
     */
    fun onAdVideoFailed(onAdVideoFailed: (error: AdError?) -> Unit) {
        this.onAdVideoFailed = onAdVideoFailed
    }

    /**
     * 广告关闭回调，建议在此回调中调用load进行广告的加载，方便下一次广告的展示
     */
    fun onAdVideoClosed(onAdVideoClosed: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoClosed = onAdVideoClosed
    }

    /**
     * 下发激励的时候会回调该接口
     */
    fun onAdReward(onAdReward: (info: ATAdInfo?) -> Unit) {
        this.onAdReward = onAdReward
    }

    /**
     * 广告播放失败回调
     */
    fun onAdVideoPlayFailed(onAdVideoPlayFailed: (error: AdError?, info: ATAdInfo?) -> Unit) {
        this.onAdVideoPlayFailed = onAdVideoPlayFailed
    }

    /**
     * 广告刷新回调
     */
    fun onAdVideoPlayStart(onAdVideoPlayStart: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoPlayStart = onAdVideoPlayStart
    }

    /**
     * 广告播放结束
     */
    fun onAdVideoPlayEnd(onAdVideoPlayEnd: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoPlayEnd = onAdVideoPlayEnd
    }

    /**
     * 广告点击
     */
    fun onAdVideoPlayClicked(onAdVideoPlayClicked: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoPlayClicked = onAdVideoPlayClicked
    }

    /**
     * 广告加载超时
     */
    fun onAdTimeOut(onAdTimeOut: () -> Unit) {
        this.onAdTimeOut = onAdTimeOut
    }
}