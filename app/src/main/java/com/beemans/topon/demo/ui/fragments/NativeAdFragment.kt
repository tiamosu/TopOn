package com.beemans.topon.demo.ui.fragments

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.nativead.NativeAdLoader
import kotlinx.android.synthetic.main.fragment_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class NativeAdFragment : BaseFragment() {
    private val config by lazy { NativeAdConfig(Constant.NATIVE_AD_ID, 350.pt2px, 270.pt2px) }
    private var nativeAdLoader: NativeAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_native_ad

    override fun initEvent() {
        //预加载广告
        nativeAd_btnPreload.setOnClickListener {
            TopOn.loadNativeAd(this, config)
        }

        //显示广告
        nativeAd_btnShowAd.setOnClickListener {
            if (nativeAdLoader == null) {
                nativeAdLoader = TopOn.loadNativeAd(this, config) {
                    onAdRenderSuc { flAdView ->
                        nativeAd_flAd.addView(flAdView)
                    }
                    onAdLoadFail {
                        Log.e("xia", "${this.javaClass.simpleName}show:${it?.printStackTrace()}")
                    }
                    onAdCloseClick { _, _ ->
                        true
                    }
                }
            }
            nativeAdLoader?.show()
        }
    }
}