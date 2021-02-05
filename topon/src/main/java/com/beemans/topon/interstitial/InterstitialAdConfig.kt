package com.beemans.topon.interstitial

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
@Keep
@Parcelize
data class InterstitialAdConfig(
    val placementId: String,            //广告位ID
    val requestTimeOut: Long = 5000,    //请求超时时间
    val scenario: String = "",          //广告展示场景，可从后台创建场景参数
    val localExtra: @RawValue Map<String, Any>? = null,    //本地参数
) : Parcelable