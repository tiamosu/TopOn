package com.beemans.topon.rewardad

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
@Parcelize
data class RewardAdConfig(
    val placementId: String,            //广告位ID
    val isUsePreload: Boolean = true,   //是否进行广告预加载
    val requestTimeOut: Long = 5000,    //请求超时时间
    val userId: String = "",            //设置用户的信息，主要用于激励下发
    val customData: String = ""
) : Parcelable