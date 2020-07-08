package com.beemans.topon

import androidx.lifecycle.LifecycleOwner
import com.beemans.topon.interstitial.InterstitialAdCallback
import com.beemans.topon.interstitial.InterstitialAdConfig
import com.beemans.topon.interstitial.InterstitialAdLoader
import com.beemans.topon.nativead.*
import com.beemans.topon.nativead.banner.NativeBannerCallback
import com.beemans.topon.nativead.banner.NativeBannerConfig
import com.beemans.topon.nativead.banner.NativeBannerLoader
import com.beemans.topon.nativead.splash.NativeSplashCallback
import com.beemans.topon.nativead.splash.NativeSplashConfig
import com.beemans.topon.nativead.splash.NativeSplashLoader
import com.beemans.topon.reward.RewardAdCallback
import com.beemans.topon.reward.RewardAdConfig
import com.beemans.topon.reward.RewardAdLoader

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
object TopOn {

    /**
     * 加载原生信息流
     */
    fun loadNativeAd(
        owner: LifecycleOwner,
        nativeAdConfig: NativeAdConfig,
        nativeAdRender: BaseNativeAdRender = DefaultNativeAdRender(),
        nativeAdCallback: NativeAdCallback.() -> Unit = {}
    ): NativeAdLoader {
        return NativeAdLoader(owner, nativeAdConfig, nativeAdRender, nativeAdCallback)
    }

    /**
     * 加载信息流——Banner
     */
    fun loadNativeBanner(
        owner: LifecycleOwner,
        bannerConfig: NativeBannerConfig,
        bannerCallback: NativeBannerCallback.() -> Unit = {}
    ): NativeBannerLoader {
        return NativeBannerLoader(owner, bannerConfig, bannerCallback)
    }

    /**
     * 加载信息流——Splash
     */
    fun loadNativeSplash(
        owner: LifecycleOwner,
        splashConfig: NativeSplashConfig,
        splashCallback: NativeSplashCallback.() -> Unit = {}
    ): NativeSplashLoader {
        return NativeSplashLoader(owner, splashConfig, splashCallback)
    }

    /**
     * 加载激励视频
     */
    fun loadRewardAd(
        owner: LifecycleOwner,
        rewardAdConfig: RewardAdConfig,
        rewardAdCallback: RewardAdCallback.() -> Unit = {}
    ): RewardAdLoader {
        return RewardAdLoader(owner, rewardAdConfig, rewardAdCallback)
    }

    /**
     * 加载插屏
     */
    fun loadInterstitialAd(
        owner: LifecycleOwner,
        interstitialAdConfig: InterstitialAdConfig,
        interstitialAdCallback: InterstitialAdCallback.() -> Unit = {}
    ): InterstitialAdLoader {
        return InterstitialAdLoader(owner, interstitialAdConfig, interstitialAdCallback)
    }
}