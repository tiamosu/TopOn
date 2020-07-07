package com.beemans.topon.nativead

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Parcelize
data class NativeAdConfig(
    var placementId: String,            //广告位ID
    var nativeWidth: Int,               //广告视图宽度
    var nativeHeight: Int,              //广告视图高度
    var isUsePreload: Boolean = true    //是否进行广告预加载
) : Parcelable