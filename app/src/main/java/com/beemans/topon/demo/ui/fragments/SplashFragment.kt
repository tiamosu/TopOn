package com.beemans.topon.demo.ui.fragments

import androidx.core.view.isVisible
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.databinding.FragmentSplashBinding
import com.beemans.topon.splash.SplashAdConfig
import com.beemans.topon.splash.SplashAdLoader

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
class SplashFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentSplashBinding }
    private var splashAdLoader: SplashAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_splash

    override fun initEvent() {
        dataBinding.splashBtnLoad.setOnClickListener {
            if (splashAdLoader == null) {
                val config = SplashAdConfig(Constant.SPLASH_ID)
                splashAdLoader = TopOn.loadSplash(this, config) {
                    onAdRenderSuc { flAdView ->
                        dataBinding.splashBtnLoad.isVisible = false
                        dataBinding.splashFlAd.addView(flAdView)
                    }
                    onAdClose {
                        dataBinding.splashBtnLoad.isVisible = true
                        true
                    }
                }
            }
            splashAdLoader?.show()
        }
    }
}