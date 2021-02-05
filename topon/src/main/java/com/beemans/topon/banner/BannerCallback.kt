package com.beemans.topon.banner

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerCallback {

    var onAdRequest: (() -> Unit)? = null

    var onAdLoadSuc: (() -> Unit)? = null

    var onAdLoadFail: ((error: AdError?) -> Unit)? = null

    var onAdRenderSuc: (() -> Unit)? = null

    var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClose: ((info: ATAdInfo?) -> Boolean)? = null

    var onAdAutoRefresh: ((info: ATAdInfo?) -> Unit)? = null

    var onAdAutoRefreshFail: ((error: AdError?) -> Unit)? = null

    /**
     * 广告请求
     */
    fun onAdRequest(onAdRequest: () -> Unit) {
        this.onAdRequest = onAdRequest
    }

    /**
     * 广告加载成功回调
     */
    fun onAdLoadSuc(onAdLoadSuc: () -> Unit) {
        this.onAdLoadSuc = onAdLoadSuc
    }

    /**
     * 广告加载失败回调
     */
    fun onAdLoadFail(onAdLoadFail: (error: AdError?) -> Unit) {
        this.onAdLoadFail = onAdLoadFail
    }

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: () -> Unit) {
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
    fun onAdClose(onAdClose: (info: ATAdInfo?) -> Boolean) {
        this.onAdClose = onAdClose
    }

    /**
     * 广告自动刷新回调
     */
    fun onAdAutoRefresh(onAdAutoRefresh: (info: ATAdInfo?) -> Unit) {
        this.onAdAutoRefresh = onAdAutoRefresh
    }

    /**
     * 广告自动刷新失败回调
     */
    fun onAdAutoRefreshFail(onAdAutoRefreshFail: (error: AdError?) -> Unit) {
        this.onAdAutoRefreshFail = onAdAutoRefreshFail
    }
}