package com.beemans.topon.splash

import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
internal object SplashAdManager {
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