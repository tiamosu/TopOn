package com.beemans.topon.interstitial

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
@Parcelize
data class InterstitialAdConfig(
    val placementId: String,            //广告位ID
    val isUsePreload: Boolean = false,  //是否进行广告预加载
    val requestTimeOut: Long = 5000,    //请求超时时间
    val scenario: String = ""           //广告展示场景，可从后台创建场景参数
) : Parcelable