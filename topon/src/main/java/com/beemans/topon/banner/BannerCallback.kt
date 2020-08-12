package com.beemans.topon.banner

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerCallback {

    var onAdRenderSuc: (() -> Unit)? = null

    var onAdLoaded: (() -> Unit)? = null

    var onAdFailed: ((error: AdError?) -> Unit)? = null

    var onAdClicked: ((info: ATAdInfo?) -> Unit)? = null

    var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClose: ((info: ATAdInfo?) -> Boolean)? = null

    var onAdAutoRefreshed: ((info: ATAdInfo?) -> Unit)? = null

    var onAdAutoRefreshFail: ((error: AdError?) -> Unit)? = null

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: () -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告加载成功回调
     */
    fun onAdLoaded(onAdLoaded: () -> Unit) {
        this.onAdLoaded = onAdLoaded
    }

    /**
     * 广告加载失败回调
     */
    fun onAdFailed(onAdFailed: (error: AdError?) -> Unit) {
        this.onAdFailed = onAdFailed
    }

    /**
     * 广告点击
     */
    fun onAdClicked(onAdClicked: (info: ATAdInfo?) -> Unit) {
        this.onAdClicked = onAdClicked
    }

    /**
     * 广告展示回调
     */
    fun onAdShow(onAdShow: (info: ATAdInfo?) -> Unit) {
        this.onAdShow = onAdShow
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
    fun onAdAutoRefreshed(onAdAutoRefreshed: (info: ATAdInfo?) -> Unit) {
        this.onAdAutoRefreshed = onAdAutoRefreshed
    }

    /**
     * 广告自动刷新失败回调
     */
    fun onAdAutoRefreshFail(onAdAutoRefreshFail: (error: AdError?) -> Unit) {
        this.onAdAutoRefreshFail = onAdAutoRefreshFail
    }
}