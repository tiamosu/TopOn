package com.beemans.topon.nativead.banner

import android.widget.FrameLayout

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerCallback {

    internal var onAdLoaded: ((flAd: FrameLayout) -> Unit)? = null

    internal var onAdError: ((errorMsg: String?) -> Unit)? = null

    internal var onAdClick: (() -> Unit)? = null

    internal var onAdClose: (() -> Boolean)? = null

    /**
     * 广告加载成功
     */
    fun onAdLoaded(onAdLoaded: (flAd: FrameLayout) -> Unit) {
        this.onAdLoaded = onAdLoaded
    }

    /**
     * 广告加载失败
     */
    fun onAdError(onAdError: (errorMsg: String?) -> Unit) {
        this.onAdError = onAdError
    }

    /**
     * 点击广告
     */
    fun onAdClick(onAdClick: () -> Unit) {
        this.onAdClick = onAdClick
    }

    /**
     * 关闭广告
     */
    fun onAdClose(onAdClose: () -> Boolean) {
        this.onAdClose = onAdClose
    }
}