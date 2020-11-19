package com.beemans.topon.splash

import android.os.Parcelable
import com.anythink.core.api.ATMediationRequestInfo
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
@Parcelize
data class SplashAdConfig(
    val placementId: String,                                        //广告位ID
    val requestTimeOut: Long = 5000,                                //请求超时时间
    val atRequestInfo: @RawValue ATMediationRequestInfo? = null,    //各个平台的参数信息
    val localMap: @RawValue Map<String, Any>? = null                //本地参数
) : Parcelable