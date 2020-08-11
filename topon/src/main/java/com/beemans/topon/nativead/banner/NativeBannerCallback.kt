package com.beemans.topon.nativead.banner

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerCallback {

    internal var onAdRenderSuc: ((flAdView: FrameLayout) -> Unit)? = null

    internal var onAdLoaded: (() -> Unit)? = null

    internal var onAdError: ((errorMsg: String?) -> Unit)? = null

    internal var onAdAutoRefresh: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    internal var onAdAutoRefreshFail: ((errorMsg: String?) -> Unit)? = null

    internal var onAdClose: (() -> Boolean)? = null

    /**
     * 广告渲染成功
     */
    fun onAdRenderSuc(onAdRenderSuc: (flAdView: FrameLayout) -> Unit) {
        this.onAdRenderSuc = onAdRenderSuc
    }

    /**
     * 广告加载成功
     */
    fun onAdLoaded(onAdLoaded: () -> Unit) {
        this.onAdLoaded = onAdLoaded
    }

    /**
     * 广告加载失败
     */
    fun onAdError(onAdError: (errorMsg: String?) -> Unit) {
        this.onAdError = onAdError
    }

    /**
     * 广告刷新回调
     */
    fun onAdAutoRefresh(onAdAutoRefresh: (info: ATAdInfo?) -> Unit) {
        this.onAdAutoRefresh = onAdAutoRefresh
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
     * 广告刷新失败回调
     */
    fun onAdAutoRefreshFail(onAdAutoRefreshFail: (errorMsg: String?) -> Unit) {
        this.onAdAutoRefreshFail = onAdAutoRefreshFail
    }

    /**
     * 关闭广告
     */
    fun onAdClose(onAdClose: () -> Boolean) {
        this.onAdClose = onAdClose
    }
}