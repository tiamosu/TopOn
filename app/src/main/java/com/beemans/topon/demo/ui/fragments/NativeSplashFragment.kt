package com.beemans.topon.demo.ui.fragments

import androidx.core.view.isVisible
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.splash.NativeSplashConfig
import com.beemans.topon.nativead.splash.NativeSplashLoader
import kotlinx.android.synthetic.main.fragment_native_splash.*

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeSplashFragment : BaseFragment() {
    private var nativeSplashLoader: NativeSplashLoader? = null

    override fun getLayoutId() = R.layout.fragment_native_splash

    override fun initEvent() {
        nativeSplash_btnLoad.setOnClickListener {
            if (nativeSplashLoader == null) {
                val config = NativeSplashConfig(Constant.NATIVE_AD_ID2, 350.pt2px)
                nativeSplashLoader = TopOn.loadNativeSplash(this, config) {
                    onAdLoaded { flAd ->
                        nativeSplash_btnLoad.isVisible = false
                        nativeSplash_flAd.addView(flAd)
                    }
                    onAdSkip {
                        nativeSplash_btnLoad.isVisible = true
                        true
                    }
                }
            }
            nativeSplashLoader?.show()
        }
    }
}