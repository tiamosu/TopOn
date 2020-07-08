package com.beemans.topon.banner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
@Parcelize
data class BannerConfig(
    val placementId: String,
) : Parcelable