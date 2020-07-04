package com.beemans.topon.nativead

import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/3.
 */
object NativeManager {
    internal val loadedLiveDataMap: MutableMap<String, EventLiveData<Boolean>> by lazy { mutableMapOf() }
}