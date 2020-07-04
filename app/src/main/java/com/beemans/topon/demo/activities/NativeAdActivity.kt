package com.beemans.topon.demo.activities

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.bean.NativeStrategy
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseActivity
import com.beemans.topon.demo.ext.dp2px
import com.beemans.topon.nativead.NativeLoader
import kotlinx.android.synthetic.main.activity_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_native_ad

    companion object {
        const val PLACEMENT_ID_CSJ = "b5efef3c883a00"
    }

    private val nativeLoaders: MutableMap<String, NativeLoader> by lazy { mutableMapOf() }

    override fun initEvent() {
        nativeAd_adRequest.setOnClickListener {
            val nativeStrategy = NativeStrategy(PLACEMENT_ID_CSJ, 350.dp2px, 300.dp2px)
            TopOn.loadNative(getContext(), nativeStrategy)
        }

        nativeAd_adShow1.setOnClickListener {
            var nativeLoader = nativeLoaders[nativeAd_adShow1.toString()]
            if (nativeLoader == null) {
                val nativeStrategy = NativeStrategy(PLACEMENT_ID_CSJ, 350.dp2px, 300.dp2px)
                nativeLoader = TopOn.loadNative(getContext(), nativeStrategy) {
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
                        if (nativeAd_flAd.childCount > 0) {
                            nativeAd_flAd.removeAllViews()
                        }
                        nativeAd_flAd.addView(atNativeAdView, layoutParams)
                    }
                    onNativeAdLoadFail {
                        Log.e("xia", "show1:${it?.printStackTrace()}")
                    }
                }.also { nativeLoaders[nativeAd_adShow1.toString()] = it }
            }
            nativeLoader.show()
        }

        nativeAd_adShow2.setOnClickListener {
            var nativeLoader = nativeLoaders[nativeAd_adShow2.toString()]
            if (nativeLoader == null) {
                val nativeStrategy = NativeStrategy(PLACEMENT_ID_CSJ, 350.dp2px, 300.dp2px)
                nativeLoader = TopOn.loadNative(getContext(), nativeStrategy) {
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
                        if (nativeAd_flAd2.childCount > 0) {
                            nativeAd_flAd2.removeAllViews()
                        }
                        nativeAd_flAd2.addView(atNativeAdView, layoutParams)
                    }
                    onNativeAdLoadFail {
                        Log.e("xia", "show2:${it?.printStackTrace()}")
                    }
                }.also { nativeLoaders[nativeAd_adShow2.toString()] = it }
            }
            nativeLoader?.show()
        }
    }

    override fun doBusiness() {}

    override fun onResume() {
        super.onResume()
        TopOn.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        TopOn.onPause(this)
    }

    override fun onDestroy() {
        TopOn.release(this)
        super.onDestroy()
    }
}