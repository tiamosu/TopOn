package com.beemans.topon

import androidx.fragment.app.FragmentActivity
import com.beemans.topon.bean.NativeStrategy
import com.beemans.topon.nativead.NativeCallback
import com.beemans.topon.nativead.NativeLoader

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
object TopOn {

    fun loadNative(
        activity: FragmentActivity,
        nativeStrategy: NativeStrategy,
        nativeCallback: NativeCallback.() -> Unit = {}
    ): NativeLoader {
        return NativeLoader(activity, nativeStrategy, nativeCallback)
    }
}