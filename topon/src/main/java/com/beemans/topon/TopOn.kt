package com.beemans.topon

import androidx.lifecycle.LifecycleOwner
import com.beemans.topon.bean.NativeStrategy
import com.beemans.topon.nativead.BaseNativeAdRender
import com.beemans.topon.nativead.NativeAdRender
import com.beemans.topon.nativead.NativeCallback
import com.beemans.topon.nativead.NativeLoader

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
object TopOn {

    fun loadNative(
        owner: LifecycleOwner,
        nativeStrategy: NativeStrategy,
        nativeAdRender: BaseNativeAdRender = NativeAdRender(),
        nativeCallback: NativeCallback.() -> Unit = {}
    ): NativeLoader {
        return NativeLoader(owner, nativeStrategy, nativeAdRender, nativeCallback)
    }
}