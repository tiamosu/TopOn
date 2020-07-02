package com.beemans.topon.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Parcelize
data class NativeStrategy(
    var nativeWidth: Int = 0,
    var nativeHeight: Int = 0,
    var isUsePreload: Boolean = true
) : Parcelable