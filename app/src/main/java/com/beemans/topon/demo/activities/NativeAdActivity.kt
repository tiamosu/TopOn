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
        const val PLACEMENT_ID_CSJ = "b5efef3c883a00"
    }

    override fun initEvent() {
        nativeAd_adRequest.setOnClickListener {
            val nativeStrategy = NativeStrategy(350.dp2px, 300.dp2px)
            NativeLoader(getContext(), PLACEMENT_ID_CSJ).init(nativeStrategy)
        }

        nativeAd_adShow1.setOnClickListener {
            val nativeStrategy = NativeStrategy(350.dp2px, 300.dp2px)
            NativeLoader(getContext(), PLACEMENT_ID_CSJ)
                .init(nativeStrategy) {
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
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
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
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