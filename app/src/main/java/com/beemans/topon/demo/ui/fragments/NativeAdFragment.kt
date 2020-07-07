package com.beemans.topon.demo.ui.fragments

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.NativeAdLoader
import kotlinx.android.synthetic.main.fragment_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class NativeAdFragment : BaseFragment() {
    private var nativeAdLoader: NativeAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_native_ad

    override fun initEvent() {
        nativeAd_btnNativeAd.setOnClickListener {
            if (nativeAdLoader == null) {
                val config = NativeAdConfig(Constant.NATIVE_AD_ID2, 350.pt2px, 270.pt2px)
                nativeAdLoader = TopOn.loadNativeAd(this, config) {
                    onNativeRenderSuc { flAd ->
                        nativeAd_flAd.addView(flAd)
                    }
                    onNativeAdLoadFail {
                        Log.e("xia", "${this.javaClass.simpleName}show:${it?.printStackTrace()}")
                    }
                    onNativeCloseClicked { true }
                }
            }
            nativeAdLoader?.show()
        }
    }
}