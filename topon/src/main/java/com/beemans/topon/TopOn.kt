package com.beemans.topon

import androidx.lifecycle.LifecycleOwner
import com.beemans.topon.bean.NativeStrategy
import com.beemans.topon.nativead.BaseNativeAdRender
import com.beemans.topon.nativead.DefaultNativeAdRender
import com.beemans.topon.nativead.NativeAdCallback
import com.beemans.topon.nativead.NativeAdLoader

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
object TopOn {

    fun loadNative(
        owner: LifecycleOwner,
        nativeStrategy: NativeStrategy,
        nativeAdRender: BaseNativeAdRender = DefaultNativeAdRender(),
        nativeAdCallback: NativeAdCallback.() -> Unit = {}
    ): NativeAdLoader {
        return NativeAdLoader(owner, nativeStrategy, nativeAdRender, nativeAdCallback)
    }
}