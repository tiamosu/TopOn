package com.beemans.topon.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Parcelize
data class AdWrapper(
    val pageTag: String,
    val placementId: String,
) : Parcelable