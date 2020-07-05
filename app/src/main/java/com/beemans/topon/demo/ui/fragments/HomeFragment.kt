package com.beemans.topon.demo.ui.fragments

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.bean.NativeStrategy
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.dp2px
import com.beemans.topon.demo.ui.activities.NativeAdActivity
import com.beemans.topon.nativead.NativeLoader
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class HomeFragment : BaseFragment() {
    private var nativeLoader: NativeLoader? = null

    override fun getLayoutId() = R.layout.fragment_home

    override fun initEvent() {
        home_btnNativeAd.setOnClickListener {
            if (nativeLoader == null) {
                val nativeStrategy =
                    NativeStrategy(Constant.NATIVE_AD_ID, 350.dp2px, 300.dp2px)
                nativeLoader = TopOn.loadNative(context, nativeStrategy) {
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
                        if (home_flAd.childCount > 0) {
                            home_flAd.removeAllViews()
                        }
                        home_flAd.addView(atNativeAdView, layoutParams)
                    }
                    onNativeAdLoadFail {
                        Log.e("xia", "${this.javaClass.simpleName}show:${it?.printStackTrace()}")
                    }
                }
            }
            nativeLoader?.show()
        }
    }
}