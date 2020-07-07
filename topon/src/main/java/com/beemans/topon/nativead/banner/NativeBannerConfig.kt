package com.beemans.topon.nativead.banner

import android.os.Parcelable
import com.anythink.nativead.banner.api.ATNativeBannerConfig
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
@Parcelize
data class NativeBannerConfig(
    val placementId: String,        //广告位ID
    val nativeWidth: Int,           //广告视图宽度
    val nativeHeight: Int,          //广告视图高度
    val atBannerConfig:
    @RawValue ATNativeBannerConfig = ATNativeBannerConfig(),    //NativeBanner的本地设置项
) : Parcelable