package com.beemans.topon.demo.ui.fragments

import com.anythink.nativead.banner.api.ATNativeBannerConfig
import com.anythink.nativead.banner.api.ATNativeBannerSize
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.dp2px
import com.beemans.topon.nativead.banner.NativeBannerConfig
import com.beemans.topon.nativead.banner.NativeBannerLoader
import kotlinx.android.synthetic.main.fragment_native_banner.*

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerFragment : BaseFragment() {
    private var nativeBannerLoader: NativeBannerLoader? = null
    private var nativeBannerLoader2: NativeBannerLoader? = null
    private var nativeBannerLoader3: NativeBannerLoader? = null

    override fun getLayoutId() = R.layout.fragment_native_banner

    override fun initEvent() {
        nativeBanner_btnShow.setOnClickListener {
            loadNativeBanner1()
            loadNativeBanner2()
            loadNativeBanner3()
        }
    }

    private fun loadNativeBanner1() {
        if (nativeBannerLoader == null) {
            val bannerConfig = ATNativeBannerConfig().apply {
                bannerSize = ATNativeBannerSize.BANNER_SIZE_320x50
                isCloseBtnShow = true
            }
            val config =
                NativeBannerConfig(Constant.NATIVE_AD_ID, 160.dp2px, 30.dp2px, bannerConfig)
            nativeBannerLoader = NativeBannerLoader(this, config) {
                onAdLoaded { atNativeBannerView, layoutParams ->
                    if (nativeBanner_flBanner.childCount > 0) {
                        nativeBanner_flBanner.removeAllViews()
                    }
                    nativeBanner_flBanner.addView(atNativeBannerView, layoutParams)
                }
            }
        }
        nativeBannerLoader?.show()
    }

    private fun loadNativeBanner2() {
        if (nativeBannerLoader2 == null) {
            val bannerConfig = ATNativeBannerConfig().apply {
                bannerSize = ATNativeBannerSize.BANNER_SIZE_640x150
                isCloseBtnShow = true
            }
            val config =
                NativeBannerConfig(Constant.NATIVE_AD_ID, 320.dp2px, 75.dp2px, bannerConfig)
            nativeBannerLoader2 = NativeBannerLoader(this, config) {
                onAdLoaded { atNativeBannerView, layoutParams ->
                    if (nativeBanner_flBanner2.childCount > 0) {
                        nativeBanner_flBanner2.removeAllViews()
                    }
                    nativeBanner_flBanner2.addView(atNativeBannerView, layoutParams)
                }
            }
        }
        nativeBannerLoader2?.show()
    }

    private fun loadNativeBanner3() {
        if (nativeBannerLoader3 == null) {
            val bannerConfig = ATNativeBannerConfig().apply {
                bannerSize = ATNativeBannerSize.BANNER_SIZE_AUTO
                isCloseBtnShow = true
            }
            val config =
                NativeBannerConfig(Constant.NATIVE_AD_ID, 350.dp2px, 270.dp2px, bannerConfig)
            nativeBannerLoader3 = NativeBannerLoader(this, config) {
                onAdLoaded { atNativeBannerView, layoutParams ->
                    if (nativeBanner_flBanner3.childCount > 0) {
                        nativeBanner_flBanner3.removeAllViews()
                    }
                    nativeBanner_flBanner3.addView(atNativeBannerView, layoutParams)
                }
            }
        }
        nativeBannerLoader3?.show()
    }
}