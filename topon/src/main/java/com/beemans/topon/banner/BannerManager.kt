package com.beemans.topon.banner

import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
internal object BannerManager {
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