package com.beemans.topon.rewardad

import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
class RewardAdCallback {

    internal var onRewardRenderSuc: (() -> Unit)? = null

    internal var onRewardedVideoAdLoaded: (() -> Unit)? = null

    internal var onRewardedVideoAdFailed: ((error: AdError?) -> Unit)? = null

    internal var onRewardedVideoAdClosed: (() -> Unit)? = null

    internal var onReward: (() -> Unit)? = null

    internal var onRewardedVideoAdPlayFailed: ((error: AdError?) -> Unit)? = null

    internal var onRewardedVideoAdPlayStart: (() -> Unit)? = null

    internal var onRewardedVideoAdPlayEnd: (() -> Unit)? = null

    internal var onRewardedVideoAdPlayClicked: (() -> Unit)? = null

    internal var onRewardedVideoAdTimeOut: (() -> Unit)? = null

    /**
     * 广告渲染成功
     */
    fun onRewardRenderSuc(onRewardRenderSuc: () -> Unit) {
        this.onRewardRenderSuc = onRewardRenderSuc
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
    fun onRewardedVideoAdClosed(onRewardedVideoAdClosed: () -> Unit) {
        this.onRewardedVideoAdClosed = onRewardedVideoAdClosed
    }

    /**
     * 下发激励的时候会回调该接口
     */
    fun onReward(onReward: () -> Unit) {
        this.onReward = onReward
    }

    /**
     * 广告播放失败回调
     */
    fun onRewardedVideoAdPlayFailed(onRewardedVideoAdPlayFailed: (error: AdError?) -> Unit) {
        this.onRewardedVideoAdPlayFailed = onRewardedVideoAdPlayFailed
    }

    /**
     * 广告刷新回调
     */
    fun onRewardedVideoAdPlayStart(onRewardedVideoAdPlayStart: () -> Unit) {
        this.onRewardedVideoAdPlayStart = onRewardedVideoAdPlayStart
    }

    /**
     * 广告播放结束
     */
    fun onRewardedVideoAdPlayEnd(onRewardedVideoAdPlayEnd: () -> Unit) {
        this.onRewardedVideoAdPlayEnd = onRewardedVideoAdPlayEnd
    }

    /**
     * 广告点击
     */
    fun onRewardedVideoAdPlayClicked(onRewardedVideoAdPlayClicked: () -> Unit) {
        this.onRewardedVideoAdPlayClicked = onRewardedVideoAdPlayClicked
    }

    /**
     * 广告加载超时
     */
    fun onRewardedVideoAdTimeOut(onRewardedVideoAdTimeOut: () -> Unit) {
        this.onRewardedVideoAdTimeOut = onRewardedVideoAdTimeOut
    }
}