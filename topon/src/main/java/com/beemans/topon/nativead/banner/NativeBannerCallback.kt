package com.beemans.topon.nativead.banner

import android.view.ViewGroup
import com.anythink.nativead.banner.api.ATNativeBannerView

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerCallback {

    internal var onAdLoaded: ((
        atNativeBannerView: ATNativeBannerView,
        layoutParams: ViewGroup.LayoutParams,
    ) -> Unit)? = null

    internal var onAdError: ((errorMsg: String?) -> Unit)? = null

    internal var onAdClick: (() -> Unit)? = null

    /**
     * 广告加载成功
     */
    fun onAdLoaded(
        onAdLoaded: (
            atNativeBannerView: ATNativeBannerView,
            layoutParams: ViewGroup.LayoutParams
        ) -> Unit
    ) {
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
}