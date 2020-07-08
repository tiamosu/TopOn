package com.beemans.topon

import androidx.lifecycle.LifecycleOwner
import com.beemans.topon.nativead.*
import com.beemans.topon.nativead.banner.NativeBannerCallback
import com.beemans.topon.nativead.banner.NativeBannerConfig
import com.beemans.topon.nativead.banner.NativeBannerLoader
import com.beemans.topon.nativead.splash.NativeSplashCallback
import com.beemans.topon.nativead.splash.NativeSplashConfig
import com.beemans.topon.nativead.splash.NativeSplashLoader
import com.beemans.topon.rewardad.RewardAdCallback
import com.beemans.topon.rewardad.RewardAdConfig
import com.beemans.topon.rewardad.RewardAdLoader

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
object TopOn {

    fun loadNativeAd(
        owner: LifecycleOwner,
        nativeAdConfig: NativeAdConfig,
        nativeAdRender: BaseNativeAdRender = DefaultNativeAdRender(),
        nativeAdCallback: NativeAdCallback.() -> Unit = {}
    ): NativeAdLoader {
        return NativeAdLoader(owner, nativeAdConfig, nativeAdRender, nativeAdCallback)
    }

    fun loadNativeBanner(
        owner: LifecycleOwner,
        bannerConfig: NativeBannerConfig,
        bannerCallback: NativeBannerCallback.() -> Unit = {}
    ): NativeBannerLoader {
        return NativeBannerLoader(owner, bannerConfig, bannerCallback)
    }

    fun loadNativeSplash(
        owner: LifecycleOwner,
        splashConfig: NativeSplashConfig,
        splashCallback: NativeSplashCallback.() -> Unit = {}
    ): NativeSplashLoader {
        return NativeSplashLoader(owner, splashConfig, splashCallback)
    }

    fun loadRewardAd(
        owner: LifecycleOwner,
        rewardAdConfig: RewardAdConfig,
        rewardAdCallback: RewardAdCallback.() -> Unit = {}
    ): RewardAdLoader {
        return RewardAdLoader(owner, rewardAdConfig, rewardAdCallback)
    }
}