package com.beemans.topon.nativead

import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/3.
 */
internal object NativeManager {
    val loadedLiveDataMap: MutableMap<String, EventLiveData<Boolean>> by lazy { mutableMapOf() }
    private val requestingMap: MutableMap<String, Boolean> by lazy { mutableMapOf() }

    fun isRequesting(): Boolean {
        for (isRequesting in requestingMap.values) {
            if (isRequesting) {
                return true
            }
        }
        return false
    }

    fun updateRequestStatus(tag: String, isRequesting: Boolean) {
        requestingMap[tag] = isRequesting
    }
}