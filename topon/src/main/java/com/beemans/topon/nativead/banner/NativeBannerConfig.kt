package com.beemans.topon.nativead.banner

import android.os.Parcelable
import com.anythink.nativead.banner.api.ATNativeBannerConfig
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
@Parcelize
data class NativeBannerConfig(
    val placementId: String,
    val nativeWidth: Int,
    val nativeHeight: Int,
    val atNativeBannerConfig: @RawValue ATNativeBannerConfig,
) : Parcelable