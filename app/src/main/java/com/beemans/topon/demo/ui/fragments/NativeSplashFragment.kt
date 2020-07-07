package com.beemans.topon.demo.ui.fragments

import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.nativead.splash.NativeSplashConfig
import kotlinx.android.synthetic.main.fragment_native_splash.*

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeSplashFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_native_splash

    override fun onFlySupportVisible() {
        val config = NativeSplashConfig(Constant.NATIVE_AD_ID)
        TopOn.loadNativeSplash(this, config) {
            onAdLoaded { frameLayout, layoutParams ->
                if (nativeSplash_flAd.childCount > 0) {
                    nativeSplash_flAd.removeAllViews()
                }
                nativeSplash_flAd.addView(frameLayout, layoutParams)
            }
        }.show()
    }
}