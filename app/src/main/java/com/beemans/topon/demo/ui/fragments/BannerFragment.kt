package com.beemans.topon.demo.ui.fragments

import com.beemans.topon.TopOn
import com.beemans.topon.banner.BannerConfig
import com.beemans.topon.banner.BannerLoader
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import kotlinx.android.synthetic.main.fragment_banner.*

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerFragment : BaseFragment() {
    private var bannerLoader: BannerLoader? = null

    override fun getLayoutId() = R.layout.fragment_banner

    override fun initEvent() {
        banner_btnLoad.setOnClickListener {
            if (bannerLoader == null) {
                val config = BannerConfig(Constant.BANNER_ID, 375.pt2px, 180.pt2px)
                bannerLoader = TopOn.loadBanner(this, config, banner_flAd) {
                    onAdRenderSuc {}
                    onAdClose { true }
                }
            }
            bannerLoader?.show(true)
        }
    }

    override fun doBusiness() {
    }
}