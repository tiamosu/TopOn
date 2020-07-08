package com.beemans.topon.banner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
@Parcelize
data class BannerConfig(
    val placementId: String,            //广告位ID
    val nativeWidth: Int,               //广告视图宽度
    val nativeHeight: Int,              //广告视图高度
    val isUsePreload: Boolean = true    //是否进行广告预加载
) : Parcelable