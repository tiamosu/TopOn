package com.beemans.topon.nativead

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Parcelize
data class NativeAdConfig(
    val placementId: String,                //广告位ID
    val nativeWidth: Int,                   //广告视图宽度
    val nativeHeight: Int,                  //广告视图高度
    val isUsePreload: Boolean = false,      //是否进行广告预加载
    val isHighlyAdaptive: Boolean = true,   //高度自适应（目前只支持穿山甲、广点通）
    val backgroundColor: Int? = null,       //设置广告背景颜色
    val isCustomRender: Boolean = false     //是否是自渲染
) : Parcelable