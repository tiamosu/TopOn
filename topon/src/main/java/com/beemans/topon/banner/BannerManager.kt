package com.beemans.topon.banner

import androidx.lifecycle.MutableLiveData

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
internal class BannerManager {
    private val requestingMap: MutableMap<String, Boolean> by lazy { mutableMapOf() }

    companion object {
        val loadedLiveDataMap: MutableMap<String, MutableLiveData<Boolean>> by lazy { mutableMapOf() }

        private val instance: BannerManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BannerManager()
        }

        fun isRequesting(placementId: String): Boolean {
            return instance.requestingMap[placementId] == true
        }

        fun updateRequestStatus(placementId: String, isRequesting: Boolean) {
            instance.requestingMap[placementId] = isRequesting
        }

        fun release(placementId: String) {
            instance.requestingMap[placementId] = false
        }
    }
}