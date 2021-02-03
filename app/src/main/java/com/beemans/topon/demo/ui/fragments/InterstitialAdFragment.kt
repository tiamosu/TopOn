package com.beemans.topon.demo.ui.fragments

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.databinding.FragmentInterstitialAdBinding
import com.beemans.topon.interstitial.InterstitialAdConfig
import com.beemans.topon.interstitial.InterstitialAdLoader

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class InterstitialAdFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentInterstitialAdBinding }
    private var interstitialAdLoader: InterstitialAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_interstitial_ad

    override fun initEvent() {
        dataBinding.interstitialAdBtnLoad.setOnClickListener {
            if (interstitialAdLoader == null) {
                val config = InterstitialAdConfig(Constant.INTERSTITIAL_ID)
                interstitialAdLoader = TopOn.loadInterstitialAd(this, config) {
                    onAdRenderSuc { info ->
                        Log.e("xia", "onAdRenderSuc:$info")
                    }
                }
            }
            interstitialAdLoader?.show()
        }
    }
}