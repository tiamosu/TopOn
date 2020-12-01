package com.beemans.topon.demo.ui.fragments

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.banner.BannerConfig
import com.beemans.topon.banner.BannerLoader
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.databinding.FragmentBannerBinding
import com.beemans.topon.demo.ext.pt2px

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentBannerBinding }
    private var bannerLoader: BannerLoader? = null

    override fun getLayoutId() = R.layout.fragment_banner

    override fun initEvent() {
        dataBinding.bannerBtnLoad.setOnClickListener {
            if (bannerLoader == null) {
                val config = BannerConfig(Constant.BANNER_ID, 375.pt2px, 180.pt2px)
                bannerLoader = TopOn.loadBanner(this, config, dataBinding.bannerFlAd) {
                    onAdRenderSuc {}
                    onAdClose { true }
                    onAdAutoRefreshed {
                        Log.e("xia", "onAdAutoRefreshed")
                    }
                }
            }
            bannerLoader?.show(true)
        }
    }

    override fun onFlySupportVisible() {
        bannerLoader?.onFlySupportVisible()
    }

    override fun onFlySupportInvisible() {
        bannerLoader?.onFlySupportInvisible()
    }

    override fun doBusiness() {
    }
}