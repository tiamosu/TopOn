package com.beemans.topon.demo.ui.activities

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseActivity
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.nativead.NativeAdLoader
import kotlinx.android.synthetic.main.activity_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdActivity : BaseActivity() {
    private val nativeAdLoaders: MutableMap<String, NativeAdLoader> by lazy { mutableMapOf() }

    override fun getLayoutId() = R.layout.activity_native_ad

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
                    onNativeRenderSuc { flAd ->
                        nativeAd_flAd.addView(flAd)
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
                    onNativeRenderSuc { flAd ->
                        nativeAd_flAd2.addView(flAd)
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