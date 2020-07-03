package com.beemans.topon.nativead

import com.anythink.nativead.api.ATNative
import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/3.
 */
internal object NativeManager {
    val atNatives: MutableMap<String, ATNative> by lazy { mutableMapOf() }
    val eventLiveData: EventLiveData<Boolean> by lazy { EventLiveData() }
}