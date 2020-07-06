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

    override fun getLayoutId() = R.layout.fragment_native_banner

    override fun initEvent() {
        nativeBanner_btnShow.setOnClickListener {
            if (nativeBannerLoader == null) {
                val bannerConfig = ATNativeBannerConfig().apply {
                    bannerSize = ATNativeBannerSize.BANNER_SIZE_AUTO
                    isCloseBtnShow = true
                }
                val config =
                    NativeBannerConfig(Constant.NATIVE_AD_ID, 350.dp2px, 300.dp2px, bannerConfig)
                nativeBannerLoader = NativeBannerLoader(this, config)
            }
            nativeBannerLoader?.show { atNativeBannerView, layoutParams ->
                if (nativeBanner_flBanner.childCount > 0) {
                    nativeBanner_flBanner.removeAllViews()
                }
                nativeBanner_flBanner.addView(atNativeBannerView, layoutParams)
            }
        }
    }
}