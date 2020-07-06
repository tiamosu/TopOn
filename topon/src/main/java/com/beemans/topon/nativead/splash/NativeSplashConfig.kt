package com.beemans.topon.nativead.splash

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
@Parcelize
data class NativeSplashConfig(
    val placementId: String,
    val nativeWidth: Int = 0,
    val nativeHeight: Int = 0
) : Parcelable