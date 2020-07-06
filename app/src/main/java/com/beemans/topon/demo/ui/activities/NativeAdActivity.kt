package com.beemans.topon.demo.ui.activities

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseActivity
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.NativeAdLoader
import kotlinx.android.synthetic.main.activity_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_native_ad

    private val nativeAdLoaders: MutableMap<String, NativeAdLoader> by lazy { mutableMapOf() }

    override fun initEvent() {
        nativeAd_adRequest.setOnClickListener {
            val config = NativeAdConfig(Constant.NATIVE_AD_ID, 350.pt2px, 270.pt2px)
            TopOn.loadNativeAd(getContext(), config)
        }

        nativeAd_adShow1.setOnClickListener {
            var nativeLoader = nativeAdLoaders[nativeAd_adShow1.toString()]
            if (nativeLoader == null) {
                val config = NativeAdConfig(Constant.NATIVE_AD_ID, 350.pt2px, 270.pt2px)
                nativeLoader = TopOn.loadNativeAd(getContext(), config) {
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
                        if (nativeAd_flAd.childCount > 0) {
                            nativeAd_flAd.removeAllViews()
                        }
                        nativeAd_flAd.addView(atNativeAdView, layoutParams)
                    }
                    onNativeAdLoadFail {
                        Log.e("xia", "show1:${it?.printStackTrace()}")
                    }
                }.also { nativeAdLoaders[nativeAd_adShow1.toString()] = it }
            }
            nativeLoader.show()
        }

        nativeAd_adShow2.setOnClickListener {
            var nativeLoader = nativeAdLoaders[nativeAd_adShow2.toString()]
            if (nativeLoader == null) {
                val config = NativeAdConfig(Constant.NATIVE_AD_ID, 350.pt2px, 270.pt2px)
                nativeLoader = TopOn.loadNativeAd(getContext(), config) {
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
                        if (nativeAd_flAd2.childCount > 0) {
                            nativeAd_flAd2.removeAllViews()
                        }
                        nativeAd_flAd2.addView(atNativeAdView, layoutParams)
                    }
                    onNativeAdLoadFail {
                        Log.e("xia", "show2:${it?.printStackTrace()}")
                    }
                }.also { nativeAdLoaders[nativeAd_adShow2.toString()] = it }
            }
            nativeLoader?.show()
        }
    }
}