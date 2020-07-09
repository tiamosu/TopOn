package com.beemans.topon.reward

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
class RewardAdCallback {

    internal var onAdRenderSuc: (() -> Unit)? = null

    internal var onRewardedVideoAdLoaded: (() -> Unit)? = null

    internal var onRewardedVideoAdFailed: ((error: AdError?) -> Unit)? = null

    internal var onRewardedVideoAdClosed: ((info: ATAdInfo?) -> Unit)? = null

    internal var onReward: ((info: ATAdInfo?) -> Unit)? = null

    internal var onRewardedVideoAdPlayFailed: ((error: AdError?, info: ATAdInfo?) -> Unit)? = null

    internal var onRewardedVideoAdPlayStart: ((info: ATAdInfo?) -> Unit)? = null

    internal var onRewardedVideoAdPlayEnd: ((info: ATAdInfo?) -> Unit)? = null

    internal var onRewardedVideoAdPlayClicked: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdTimeOut: (() -> Unit)? = null

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: () -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告加载成功回调
     */
    fun onRewardedVideoAdLoaded(onRewardedVideoAdLoaded: () -> Unit) {
        this.onRewardedVideoAdLoaded = onRewardedVideoAdLoaded
    }

    /**
     * 广告加载失败回调
     */
    fun onRewardedVideoAdFailed(onRewardedVideoAdFailed: (error: AdError?) -> Unit) {
        this.onRewardedVideoAdFailed = onRewardedVideoAdFailed
    }

    /**
     * 广告关闭回调，建议在此回调中调用load进行广告的加载，方便下一次广告的展示
     */
    fun onRewardedVideoAdClosed(onRewardedVideoAdClosed: (info: ATAdInfo?) -> Unit) {
        this.onRewardedVideoAdClosed = onRewardedVideoAdClosed
    }

    /**
     * 下发激励的时候会回调该接口
     */
    fun onReward(onReward: (info: ATAdInfo?) -> Unit) {
        this.onReward = onReward
    }

    /**
     * 广告播放失败回调
     */
    fun onRewardedVideoAdPlayFailed(onRewardedVideoAdPlayFailed: (error: AdError?, info: ATAdInfo?) -> Unit) {
        this.onRewardedVideoAdPlayFailed = onRewardedVideoAdPlayFailed
    }

    /**
     * 广告刷新回调
     */
    fun onRewardedVideoAdPlayStart(onRewardedVideoAdPlayStart: (info: ATAdInfo?) -> Unit) {
        this.onRewardedVideoAdPlayStart = onRewardedVideoAdPlayStart
    }

    /**
     * 广告播放结束
     */
    fun onRewardedVideoAdPlayEnd(onRewardedVideoAdPlayEnd: (info: ATAdInfo?) -> Unit) {
        this.onRewardedVideoAdPlayEnd = onRewardedVideoAdPlayEnd
    }

    /**
     * 广告点击
     */
    fun onRewardedVideoAdPlayClicked(onRewardedVideoAdPlayClicked: (info: ATAdInfo?) -> Unit) {
        this.onRewardedVideoAdPlayClicked = onRewardedVideoAdPlayClicked
    }

    /**
     * 广告加载超时
     */
    fun onAdTimeOut(onAdTimeOut: () -> Unit) {
        this.onAdTimeOut = onAdTimeOut
    }
}