package com.beemans.topon.nativead

import android.widget.FrameLayout
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdCallback {

    internal var onNativeAdLoadFail: ((adError: AdError?) -> Unit)? = null

    internal var onNativeAdLoaded: (() -> Unit)? = null

    internal var onNativeRenderSuc: ((flAd: FrameLayout) -> Unit)? = null

    internal var onNativeClicked: (() -> Unit)? = null

    internal var onNativeCloseClicked: (() -> Boolean)? = null

    /**
     * 广告加载失败
     */
    fun onNativeAdLoadFail(onNativeAdLoadFail: (adError: AdError?) -> Unit) {
        this.onNativeAdLoadFail = onNativeAdLoadFail
    }

    /**
     * 广告加载成功
     */
    fun onNativeAdLoaded(onNativeAdLoaded: () -> Unit) {
        this.onNativeAdLoaded = onNativeAdLoaded
    }

    /**
     * 广告渲染成功
     */
    fun onNativeRenderSuc(onNativeRenderSuc: (flAd: FrameLayout) -> Unit) {
        this.onNativeRenderSuc = onNativeRenderSuc
    }

    /**
     * 点击广告
     */
    fun onNativeClicked(onNativeClicked: () -> Unit) {
        this.onNativeClicked = onNativeClicked
    }

    /**
     * 对广告不感兴趣等，进行广告关闭点击
     */
    fun onNativeCloseClicked(onNativeCloseClicked: () -> Boolean) {
        this.onNativeCloseClicked = onNativeCloseClicked
    }
}