package com.beemans.topon.demo.ui.fragments

import androidx.core.view.isVisible
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.databinding.FragmentNativeSplashBinding
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.splash.NativeSplashConfig
import com.beemans.topon.nativead.splash.NativeSplashLoader

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeSplashFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentNativeSplashBinding }
    private var nativeSplashLoader: NativeSplashLoader? = null

    override fun getLayoutId() = R.layout.fragment_native_splash

    override fun initEvent() {
        dataBinding.nativeSplashBtnLoad.setOnClickListener {
            if (nativeSplashLoader == null) {
                val config = NativeSplashConfig(Constant.NATIVE_AD_ID, 350.pt2px)
                nativeSplashLoader = TopOn.loadNativeSplash(this, config) {
                    onAdRenderSuc { flAdView ->
                        dataBinding.nativeSplashBtnLoad.isVisible = false
                        dataBinding.nativeSplashFlAd.addView(flAdView)
                    }
                    onAdSkip {
                        dataBinding.nativeSplashBtnLoad.isVisible = true
                        true
                    }
                }
            }
            nativeSplashLoader?.show()
        }
    }
}