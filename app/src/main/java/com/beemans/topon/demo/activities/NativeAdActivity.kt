package com.beemans.topon.demo.activities

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
//        const val PLACEMENT_ID_CSJ = "b5c2c97629da0d"
        const val PLACEMENT_ID_CSJ = "b5efef3c883a00"
        const val PLACEMENT_ID_BD = "b5d148f9f2e47d"
    }

    override fun initEvent() {
//        nativeAd_adRequest1.setOnClickListener {
//            val strategy = NativeStrategy(350.dp2px, 300.dp2px)
//            TopOn.loadNative(
//                this,
//                PLACEMENT_ID_CSJ
//            ).init(strategy)
//        }
//        nativeAd_adRequest2.setOnClickListener {
//            val strategy = NativeStrategy(280.dp2px, 200.dp2px)
//            TopOn.loadNative(
//                this,
//                PLACEMENT_ID_BD
//            ).init(strategy)
//        }
//
//        nativeAd_adShow1.setOnClickListener {
//            val strategy = NativeStrategy(350.dp2px, 300.dp2px)
//            TopOn.loadNative(
//                this,
//                PLACEMENT_ID_CSJ
//            ).init(strategy) {
//                onNativeRenderSuc { atNativeAdView, lp ->
//                    if (nativeAd_flAd.childCount > 0) {
//                        nativeAd_flAd.removeAllViews()
//                    }
//                    nativeAd_flAd.addView(atNativeAdView, lp)
//                }
//                onNativeClicked {
//                    Toast.makeText(
//                        this@NativeAdActivity,
//                        "onNativeClicked", Toast.LENGTH_SHORT
//                    ).show()
//                }
//                onNativeCloseClicked {
//                    Toast.makeText(
//                        this@NativeAdActivity,
//                        "onNativeCloseClicked", Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }.show()
//        }
//
//        nativeAd_adShow2.setOnClickListener {
//            val strategy = NativeStrategy(280.dp2px, 200.dp2px)
//            TopOn.loadNative(
//                this,
//                PLACEMENT_ID_BD
//            ).init(strategy) {
//                onNativeRenderSuc { atNativeAdView, lp ->
//                    if (nativeAd_flAd.childCount > 0) {
//                        nativeAd_flAd.removeAllViews()
//                    }
//                    nativeAd_flAd.addView(atNativeAdView, lp)
//                }
//            }.show()
//        }

        nativeAd_adShow1.setOnClickListener {
            val nativeStrategy = NativeStrategy(350.dp2px, 300.dp2px)
            NativeLoader(getContext(), PLACEMENT_ID_CSJ)
                .init(nativeStrategy) {
                    onNativeAdLoadFail {
//                        Log.e("xia", "onNativeAdLoadFail:${it?.printStackTrace()}")
                    }
                    onNativeAdLoaded {
//                        Log.e("xia", "onNativeAdLoaded")
                    }
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
//                        Log.e("xia", "onNativeRenderSuc")
                        if (nativeAd_flAd.childCount > 0) {
                            nativeAd_flAd.removeAllViews()
                        }
                        nativeAd_flAd.addView(atNativeAdView, layoutParams)
                    }
                }.show()
        }

        nativeAd_adShow2.setOnClickListener {
            val nativeStrategy = NativeStrategy(350.dp2px, 300.dp2px)
            NativeLoader(getContext(), PLACEMENT_ID_CSJ)
                .init(nativeStrategy) {
                    onNativeAdLoadFail {
//                        Log.e("xia", "onNativeAdLoadFail:${it?.printStackTrace()}")
                    }
                    onNativeAdLoaded {
//                        Log.e("xia", "onNativeAdLoaded")
                    }
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
//                        Log.e("xia", "onNativeRenderSuc")
                        if (nativeAd_flAd2.childCount > 0) {
                            nativeAd_flAd2.removeAllViews()
                        }
                        nativeAd_flAd2.addView(atNativeAdView, layoutParams)
                    }
                }.show()
        }
    }

    override fun doBusiness() {}
}