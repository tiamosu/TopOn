package com.beemans.topon.nativead.banner

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerCallback {

    var onAdRequest: (() -> Unit)? = null

    var onAdLoadSuc: (() -> Unit)? = null

    var onAdLoadFail: ((errorMsg: String?) -> Unit)? = null

    var onAdRenderSuc: ((flAdView: FrameLayout) -> Unit)? = null

    var onAdShow: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClick: ((info: ATAdInfo?) -> Unit)? = null

    var onAdClose: (() -> Boolean)? = null

    var onAdAutoRefresh: ((info: ATAdInfo?) -> Unit)? = null

    var onAdAutoRefreshFail: ((errorMsg: String?) -> Unit)? = null

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
     * 关闭广告
     */
    fun onAdClose(onAdClose: () -> Boolean) {
        this.onAdClose = onAdClose
    }

    /**
     * 广告刷新回调
     */
    fun onAdAutoRefresh(onAdAutoRefresh: (info: ATAdInfo?) -> Unit) {
        this.onAdAutoRefresh = onAdAutoRefresh
    }

    /**
     * 广告刷新失败回调
     */
    fun onAdAutoRefreshFail(onAdAutoRefreshFail: (errorMsg: String?) -> Unit) {
        this.onAdAutoRefreshFail = onAdAutoRefreshFail
    }
}