package com.beemans.topon.nativead

import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/3.
 */
internal object NativeManager {
    val loadedLiveDataMap: MutableMap<String, EventLiveData<Boolean>> by lazy { mutableMapOf() }
    private val requestingMap: MutableMap<String, MutableMap<String, Boolean>> by lazy { mutableMapOf() }

    fun isRequesting(placementId: String): Boolean {
        return ((requestingMap[placementId] ?: mutableMapOf()).values).contains(true)
    }

    fun updateRequestStatus(placementId: String, tag: String, isRequesting: Boolean) {
        (requestingMap[placementId] ?: mutableMapOf()).apply {
            put(tag, isRequesting)
        }.also {
            requestingMap[placementId] = it
        }
    }

    fun release(placementId: String, tag: String) {
        (requestingMap[placementId])?.apply {
            this.remove(tag)
        }
    }
}