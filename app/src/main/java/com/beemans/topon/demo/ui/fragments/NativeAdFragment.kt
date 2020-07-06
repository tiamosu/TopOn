package com.beemans.topon.demo.ui.fragments

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.bean.NativeStrategy
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.dp2px
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
                val nativeStrategy = NativeStrategy(Constant.NATIVE_AD_ID2, 350.dp2px, 300.dp2px)
                nativeAdLoader = TopOn.loadNative(this, nativeStrategy) {
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
                        if (nativeAd_flAd.childCount > 0) {
                            nativeAd_flAd.removeAllViews()
                        }
                        nativeAd_flAd.addView(atNativeAdView, layoutParams)
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