package com.beemans.topon.nativead

import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/3.
 */
internal object NativeManager {
    val loadedLiveDataMap: MutableMap<String, EventLiveData<Boolean>> by lazy { mutableMapOf() }
    private val requestingMap: MutableMap<String, Boolean> by lazy { mutableMapOf() }

    fun isRequesting(placementId: String): Boolean {
        return requestingMap[placementId] == true
    }

    fun updateRequestStatus(placementId: String, isRequesting: Boolean) {
        requestingMap[placementId] = isRequesting
    }

    fun release(placementId: String) {
        requestingMap[placementId] = false
    }
}