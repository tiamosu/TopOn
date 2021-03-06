package com.beemans.topon

import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.beemans.topon.banner.BannerCallback
import com.beemans.topon.banner.BannerConfig
import com.beemans.topon.banner.BannerLoader
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
import com.beemans.topon.splash.SplashAdCallback
import com.beemans.topon.splash.SplashAdConfig
import com.beemans.topon.splash.SplashAdLoader

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
        nativeSplashConfig: NativeSplashConfig,
        nativeSplashCallback: NativeSplashCallback.() -> Unit = {}
    ): NativeSplashLoader {
        return NativeSplashLoader(owner, nativeSplashConfig, nativeSplashCallback)
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

    /**
     * 加载横幅
     */
    fun loadBanner(
        owner: LifecycleOwner,
        bannerConfig: BannerConfig,
        flContainer: FrameLayout,
        bannerCallback: BannerCallback.() -> Unit = {}
    ): BannerLoader {
        return BannerLoader(owner, bannerConfig, flContainer, bannerCallback)
    }

    /**
     * 加载开屏
     */
    fun loadSplash(
        owner: LifecycleOwner,
        splashAdConfig: SplashAdConfig,
        splashAdCallback: SplashAdCallback.() -> Unit
    ): SplashAdLoader {
        return SplashAdLoader(owner, splashAdConfig, splashAdCallback)
    }
}