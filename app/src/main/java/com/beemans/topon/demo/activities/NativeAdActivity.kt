package com.beemans.topon.demo.activities

import android.widget.Toast
import com.beemans.topon.TopOn
import com.beemans.topon.bean.NativeStrategy
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseActivity
import com.beemans.topon.demo.ext.dp2px
import kotlinx.android.synthetic.main.activity_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_native_ad

    companion object {
        const val PLACEMENT_ID_CSJ = "b5c2c97629da0d"
        const val PLACEMENT_ID_BD = "b5d148f9f2e47d"
    }

    override fun initEvent() {
        nativeAd_adRequest1.setOnClickListener {
            val strategy = NativeStrategy(200.dp2px, 150.dp2px)
            TopOn.loadNative(
                this,
                PLACEMENT_ID_CSJ
            ).init(strategy)
        }
        nativeAd_adRequest2.setOnClickListener {
            val strategy = NativeStrategy(280.dp2px, 200.dp2px)
            TopOn.loadNative(
                this,
                PLACEMENT_ID_BD
            ).init(strategy)
        }

        nativeAd_adShow1.setOnClickListener {
            val strategy = NativeStrategy(200.dp2px, 150.dp2px)
            TopOn.loadNative(
                this,
                PLACEMENT_ID_CSJ
            ).init(strategy) {
                onNativeRenderSuc { atNativeAdView, lp ->
                    if (nativeAd_flAd.childCount > 0) {
                        nativeAd_flAd.removeAllViews()
                    }
                    nativeAd_flAd.addView(atNativeAdView, lp)
                }
                onNativeClicked {
                    Toast.makeText(
                        this@NativeAdActivity,
                        "onNativeClicked", Toast.LENGTH_SHORT
                    ).show()
                }
                onNativeCloseClicked {
                    Toast.makeText(
                        this@NativeAdActivity,
                        "onNativeCloseClicked", Toast.LENGTH_SHORT
                    ).show()
                }
            }.show()
        }

        nativeAd_adShow2.setOnClickListener {
            val strategy = NativeStrategy(280.dp2px, 200.dp2px)
            TopOn.loadNative(
                this,
                PLACEMENT_ID_BD
            ).init(strategy) {
                onNativeRenderSuc { atNativeAdView, lp ->
                    if (nativeAd_flAd.childCount > 0) {
                        nativeAd_flAd.removeAllViews()
                    }
                    nativeAd_flAd.addView(atNativeAdView, lp)
                }
            }.show()
        }
    }

    override fun doBusiness() {}
}