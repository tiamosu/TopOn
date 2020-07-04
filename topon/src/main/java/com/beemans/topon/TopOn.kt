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
    private val nativeLoaders: MutableMap<String, MutableList<NativeLoader>> by lazy { mutableMapOf() }

    fun loadNative(
        activity: FragmentActivity,
        nativeStrategy: NativeStrategy,
        nativeCallback: NativeCallback.() -> Unit = {}
    ): NativeLoader {
        return NativeLoader(activity, nativeStrategy, nativeCallback).apply {
            val list = nativeLoaders[activity.toString()] ?: mutableListOf()
            list.add(this)
            nativeLoaders[activity.toString()] = list
        }
    }

    fun onResume(activity: FragmentActivity) {
        nativeLoaders[activity.toString()]?.apply {
            for (nativeLoader in this) {
                nativeLoader.onResume()
            }
        }
    }

    fun onPause(activity: FragmentActivity) {
        nativeLoaders[activity.toString()]?.apply {
            for (nativeLoader in this) {
                nativeLoader.onPause()
            }
        }
    }

    fun release(activity: FragmentActivity) {
        nativeLoaders[activity.toString()]?.apply {
            for (nativeLoader in this) {
                nativeLoader.onDestroy()
            }
        }
        nativeLoaders.remove(activity.toString())
    }
}