package com.beemans.topon.nativead.splash

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
@Keep
@Parcelize
data class NativeSplashConfig(
    val placementId: String,            //广告位ID
    val nativeWidth: Int,               //广告视图宽度
    val nativeHeight: Int = 0,          //广告视图高度，可传0进行自适应
    val requestTimeOut: Long = 5000,    //请求超时时间
    val fetchDelay: Long = 5000,        //广告展示的倒计时总时长
    val isHighlyAdaptive: Boolean = true,   //高度自适应（目前只支持穿山甲、广点通）
) : Parcelable