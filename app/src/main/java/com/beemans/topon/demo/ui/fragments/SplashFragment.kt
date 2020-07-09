package com.beemans.topon.demo.ui.fragments

import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.splash.SplashAdConfig
import com.beemans.topon.splash.SplashAdLoader
import kotlinx.android.synthetic.main.fragment_splash.*

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
class SplashFragment : BaseFragment() {
    private var splashAdLoader: SplashAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_splash

    override fun initEvent() {
        splash_btnLoad.setOnClickListener {
            if (splashAdLoader == null) {
                val config = SplashAdConfig(Constant.SPLASH_ID)
                splashAdLoader = SplashAdLoader(this, config) {
                    onAdRenderSuc { flAdView ->
                        splash_flAd.addView(flAdView)
                    }
                    onAdDismiss {
                        true
                    }
                }
            }
            splashAdLoader?.show()
        }
    }
}