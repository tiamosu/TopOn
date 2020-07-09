package com.beemans.topon.splash

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
@Parcelize
data class SplashAdConfig(
    val placementId: String,            //广告位ID
    val requestTimeOut: Long = 5000     //请求超时时间
) : Parcelable