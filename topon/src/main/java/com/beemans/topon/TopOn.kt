package com.beemans.topon

import android.content.Context
import com.beemans.topon.bean.AdWrapper
import com.beemans.topon.nativead.NativeLoader

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class TopOn {

    private val nativeLoaders: MutableMap<String, NativeLoader> by lazy { mutableMapOf() }

    companion object {

        private val instance: TopOn by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TopOn()
        }

        fun loadNative(context: Context, placementId: String): NativeLoader {
            val key = AdWrapper(context.javaClass.simpleName, placementId).toString()
            var nativeLoader: NativeLoader?
            if (instance.nativeLoaders[key].also { nativeLoader = it } != null) {
                return nativeLoader!!
            }
            return NativeLoader(context, placementId).also { instance.nativeLoaders[key] = it }
        }
    }
}