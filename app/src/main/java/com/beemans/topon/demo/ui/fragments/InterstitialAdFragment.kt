package com.beemans.topon.demo.ui.fragments

import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.interstitial.InterstitialAdConfig
import com.beemans.topon.interstitial.InterstitialAdLoader
import kotlinx.android.synthetic.main.fragment_interstitial_ad.*

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class InterstitialAdFragment : BaseFragment() {
    private var interstitialAdLoader: InterstitialAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_interstitial_ad

    override fun initEvent() {
        interstitialAd_btnLoad.setOnClickListener {
            if (interstitialAdLoader == null) {
                val config = InterstitialAdConfig(Constant.INTERSTITIAL_ID)
                interstitialAdLoader =
                    TopOn.loadInterstitialAd(this, config) {}
            }
            interstitialAdLoader?.show()
        }
    }
}