package com.beemans.topon.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Parcelize
data class NativeStrategy(
    var nativeWidth: Int = 0,           //广告宽度
    var nativeHeight: Int = 0,          //广告高度
    var isUsePreload: Boolean = true,   //是否进行广告预加载
    var timeout: Int = -1               //设定物理超时，默认 -1 不进行超时操作
) : Parcelable