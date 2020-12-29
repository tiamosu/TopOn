package com.beemans.topon.nativead.banner

import android.os.Parcelable
import androidx.annotation.Keep
import com.anythink.nativead.banner.api.ATNativeBannerConfig
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
@Keep
@Parcelize
data class NativeBannerConfig(
    val placementId: String,        //广告位ID
    val nativeWidth: Int,           //广告视图宽度
    val nativeHeight: Int,          //广告视图高度
    val atBannerConfig: @RawValue ATNativeBannerConfig = ATNativeBannerConfig() //NativeBanner的本地设置项
) : Parcelable