package com.beemans.topon.interstitial

import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
object InterstitialAdManager {
    val loadedLiveDataMap: MutableMap<String, EventLiveData<Boolean>> by lazy { mutableMapOf() }
    private val requestingMap: MutableMap<String, MutableMap<String, Boolean>> by lazy { mutableMapOf() }

    fun isRequesting(placementId: String): Boolean {
        return ((requestingMap[placementId] ?: mutableMapOf()).values).contains(true)
    }

    fun updateRequestStatus(placementId: String, nativeLoaderTag: String, isRequesting: Boolean) {
        (requestingMap[placementId] ?: mutableMapOf()).apply {
            put(nativeLoaderTag, isRequesting)
            requestingMap[placementId] = this
        }
    }

    fun release(placementId: String) {
        (requestingMap[placementId])?.clear()
    }
}