package com.beemans.topon.banner

import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerCallback {

    internal var onBannerLoaded: ((flAd: FrameLayout) -> Unit)? = null

    internal var onBannerFailed: ((error: AdError?) -> Unit)? = null

    internal var onBannerClicked: ((info: ATAdInfo?) -> Unit)? = null

    internal var onBannerShow: ((info: ATAdInfo?) -> Unit)? = null

    internal var onBannerClose: ((info: ATAdInfo?) -> Boolean)? = null

    internal var onBannerAutoRefreshed: ((info: ATAdInfo?) -> Unit)? = null

    internal var onBannerAutoRefreshFail: ((error: AdError?) -> Unit)? = null

    /**
     * 广告加载成功回调
     */
    fun onBannerLoaded(onBannerLoaded: (flAd: FrameLayout) -> Unit) {
        this.onBannerLoaded = onBannerLoaded
    }

    /**
     * 广告加载失败回调
     */
    fun onBannerFailed(onBannerFailed: (error: AdError?) -> Unit) {
        this.onBannerFailed = onBannerFailed
    }

    /**
     * 广告点击
     */
    fun onBannerClicked(onBannerClicked: (info: ATAdInfo?) -> Unit) {
        this.onBannerClicked = onBannerClicked
    }

    /**
     * 广告展示回调
     */
    fun onBannerShow(onBannerShow: (info: ATAdInfo?) -> Unit) {
        this.onBannerShow = onBannerShow
    }

    /**
     * 广告关闭回调
     */
    fun onBannerClose(onBannerClose: (info: ATAdInfo?) -> Boolean) {
        this.onBannerClose = onBannerClose
    }

    /**
     * 广告自动刷新回调
     */
    fun onBannerAutoRefreshed(onBannerAutoRefreshed: (info: ATAdInfo?) -> Unit) {
        this.onBannerAutoRefreshed = onBannerAutoRefreshed
    }

    /**
     * 广告自动刷新失败回调
     */
    fun onBannerAutoRefreshFail(onBannerAutoRefreshFail: (error: AdError?) -> Unit) {
        this.onBannerAutoRefreshFail = onBannerAutoRefreshFail
    }
}