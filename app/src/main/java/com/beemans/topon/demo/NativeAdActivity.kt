package com.beemans.topon.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beemans.topon.TopOn
import com.beemans.topon.bean.NativeStrategy
import kotlinx.android.synthetic.main.activity_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdActivity : AppCompatActivity() {

    companion object {
        const val PLACEMENT_ID_CSJ = "b5c2c97629da0d"
        const val PLACEMENT_ID_BD = "b5d148f9f2e47d"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_ad)

        nativeAd_adRequest1.setOnClickListener {
            val strategy = NativeStrategy(180.dp2px, 120.dp2px)
            TopOn.loadNative(this, PLACEMENT_ID_CSJ).init(strategy)
        }
        nativeAd_adRequest2.setOnClickListener {
            val strategy = NativeStrategy(280.dp2px, 200.dp2px)
            TopOn.loadNative(this, PLACEMENT_ID_BD).init(strategy)
        }

        nativeAd_adShow1.setOnClickListener {
            val strategy = NativeStrategy(180.dp2px, 120.dp2px)
            TopOn.loadNative(this, PLACEMENT_ID_CSJ).init(strategy) {
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
            TopOn.loadNative(this, PLACEMENT_ID_BD).init(strategy) {
                onNativeRenderSuc { atNativeAdView, lp ->
                    if (nativeAd_flAd.childCount > 0) {
                        nativeAd_flAd.removeAllViews()
                    }
                    nativeAd_flAd.addView(atNativeAdView, lp)
                }
            }.show()
        }
    }
}