package com.beemans.topon.rewardad

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
@Parcelize
data class RewardAdConfig(
    val placementId: String,
    val isUsePreload: Boolean = true,
    val requestTimeOut: Long = 3000,    //请求超时时间
) : Parcelable