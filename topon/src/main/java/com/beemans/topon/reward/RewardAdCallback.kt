package com.beemans.topon.reward

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
class RewardAdCallback {

    var onAdRequest: (() -> Unit)? = null

    var onAdLoadSuc: ((info: ATAdInfo?) -> Unit)? = null

    var onAdLoadFail: ((error: AdError?) -> Unit)? = null

    var onAdLoadTimeOut: (() -> Unit)? = null

    var onAdRenderSuc: ((info: ATAdInfo?) -> Unit)? = null

    var onAdReward: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClose: ((info: ATAdInfo?, isReward: Boolean) -> Unit)? = null

    var onAdVideoStart: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoEnd: ((info: ATAdInfo?) -> Unit)? = null

    var onAdVideoFail: ((error: AdError?, info: ATAdInfo?) -> Unit)? = null

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
     * 下发激励的时候会回调该接口
     */
    fun onAdReward(onAdReward: (info: ATAdInfo?) -> Unit) {
        this.onAdReward = onAdReward
    }

    /**
     * 广告点击
     */
    fun onAdClick(onAdClick: (info: ATAdInfo?) -> Unit) {
        this.onAdClick = onAdClick
    }

    /**
     * 广告关闭回调，建议在此回调中调用load进行广告的加载，方便下一次广告的展示
     */
    fun onAdClose(onAdClose: (info: ATAdInfo?, isReward: Boolean) -> Unit) {
        this.onAdClose = onAdClose
    }

    /**
     * 广告开始播放
     */
    fun onAdVideoStart(onAdVideoStart: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoStart = onAdVideoStart
    }

    /**
     * 广告播放结束
     */
    fun onAdVideoEnd(onAdVideoEnd: (info: ATAdInfo?) -> Unit) {
        this.onAdVideoEnd = onAdVideoEnd
    }

    /**
     * 广告播放失败回调
     */
    fun onAdVideoFail(onAdVideoFail: (error: AdError?, info: ATAdInfo?) -> Unit) {
        this.onAdVideoFail = onAdVideoFail
    }
}