package com.beemans.topon

import androidx.lifecycle.LifecycleOwner
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.nativead.BaseNativeAdRender
import com.beemans.topon.nativead.DefaultNativeAdRender
import com.beemans.topon.nativead.NativeAdCallback
import com.beemans.topon.nativead.NativeAdLoader

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
object TopOn {

    fun loadNativeAd(
        owner: LifecycleOwner,
        nativeAdConfig: NativeAdConfig,
        nativeAdRender: BaseNativeAdRender = DefaultNativeAdRender(),
        nativeAdCallback: NativeAdCallback.() -> Unit = {}
    ): NativeAdLoader {
        return NativeAdLoader(owner, nativeAdConfig, nativeAdRender, nativeAdCallback)
    }
}