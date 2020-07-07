package com.beemans.topon.demo.ui.fragments

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.demo.ui.activities.NativeAdActivity
import com.beemans.topon.nativead.NativeAdLoader
import com.blankj.utilcode.util.ActivityUtils
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class HomeFragment : BaseFragment() {
    private var nativeAdLoader: NativeAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_home

    override fun initEvent() {
        home_btnNativeAd.setOnClickListener {
            if (nativeAdLoader == null) {
                val config = NativeAdConfig(Constant.NATIVE_AD_ID3, 350.pt2px, 270.pt2px)
                nativeAdLoader = TopOn.loadNativeAd(this, config) {
                    onNativeRenderSuc { atNativeAdView, layoutParams ->
                        if (home_flAd.childCount > 0) {
                            home_flAd.removeAllViews()
                        }
                        home_flAd.addView(atNativeAdView, layoutParams)
                    }
                    onNativeAdLoadFail {
                        Log.e("xia", "${this.javaClass.simpleName}show:${it?.printStackTrace()}")
                    }
                    onNativeCloseClicked { true }
                }
            }
            nativeAdLoader?.show()
        }

        home_startNativeAd.setOnClickListener {
            ActivityUtils.startActivity(NativeAdActivity::class.java)
        }
    }
}