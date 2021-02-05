package com.beemans.topon.banner

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
@Keep
@Parcelize
data class BannerConfig(
    val placementId: String,            //广告位ID
    val nativeWidth: Int,               //广告视图宽度
    val nativeHeight: Int,              //广告视图高度
) : Parcelable