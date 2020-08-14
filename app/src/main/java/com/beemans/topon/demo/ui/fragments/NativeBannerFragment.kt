package com.beemans.topon.demo.ui.fragments

import com.anythink.nativead.banner.api.ATNativeBannerConfig
import com.anythink.nativead.banner.api.ATNativeBannerSize
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.banner.NativeBannerConfig
import com.beemans.topon.nativead.banner.NativeBannerLoader
import kotlinx.android.synthetic.main.fragment_native_banner.*
import kotlin.math.roundToInt

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerFragment : BaseFragment() {
    private val bannerLoaders: MutableMap<String, NativeBannerLoader> by lazy { mutableMapOf() }

    override fun getLayoutId() = R.layout.fragment_native_banner

    override fun initEvent() {
        nativeBanner_btnShow.setOnClickListener {
            loadNativeBanner1()
            loadNativeBanner2()
            loadNativeBanner3()
        }
    }

    private fun loadNativeBanner1() {
        var loader = bannerLoaders["1"]
        if (loader == null) {
            val bannerConfig = ATNativeBannerConfig().apply {
                bannerSize = ATNativeBannerSize.BANNER_SIZE_320x50
                isCloseBtnShow = true
            }
            val width = 220.pt2px
            val height = ((65 / 320f) * width).roundToInt()
            val config =
                NativeBannerConfig(Constant.NATIVE_AD_ID, width, height, bannerConfig)
            loader = TopOn.loadNativeBanner(this, config) {
                onAdRenderSuc { flAdView ->
                    nativeBanner_flBanner.addView(flAdView)
                }
                onAdClose {
                    true
                }
            }.also { bannerLoaders["1"] = it }
        }
        loader.show()
    }

    private fun loadNativeBanner2() {
        var loader = bannerLoaders["2"]
        if (loader == null) {
            val bannerConfig = ATNativeBannerConfig().apply {
                bannerSize = ATNativeBannerSize.BANNER_SIZE_640x150
                isCloseBtnShow = true
            }
            val width = 320.pt2px
            val height = ((150 / 640f) * width).roundToInt()
            val config =
                NativeBannerConfig(Constant.NATIVE_AD_ID, width, height, bannerConfig)
            loader = TopOn.loadNativeBanner(this, config) {
                onAdRenderSuc { flAdView ->
                    nativeBanner_flBanner2.addView(flAdView)
                }
            }.also { bannerLoaders["2"] = it }
        }
        loader.show()
    }

    private fun loadNativeBanner3() {
        var loader = bannerLoaders["3"]
        if (loader == null) {
            val bannerConfig = ATNativeBannerConfig().apply {
                bannerSize = ATNativeBannerSize.BANNER_SIZE_AUTO
                isCloseBtnShow = true
            }
            val config =
                NativeBannerConfig(Constant.NATIVE_AD_ID, 350.pt2px, 270.pt2px, bannerConfig)
            loader = TopOn.loadNativeBanner(this, config) {
                onAdRenderSuc { flAdView ->
                    nativeBanner_flBanner3.addView(flAdView)
                }
            }.also { bannerLoaders["3"] = it }
        }
        loader.show()
    }
}