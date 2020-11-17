package com.beemans.topon.demo.ui.fragments

import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.isVisibleLocal
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.demo.ext.setVisibleLocalListener
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.nativead.NativeAdLoader
import com.tiamosu.fly.ext.clickNoRepeat
import kotlinx.android.synthetic.main.fragment_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class NativeAdFragment : BaseFragment() {
    private val config by lazy { NativeAdConfig(Constant.NATIVE_AD_ID, 350.pt2px, 270.pt2px) }

    private var nativeAdLoader1: NativeAdLoader? = null
    private var nativeAdLoader2: NativeAdLoader? = null
    private var nativeAdLoader3: NativeAdLoader? = null
    private var isNativeAd1Refresh = true   //是否刷新信息流广告
    private var isNativeAd2Refresh = true   //是否刷新信息流广告
    private var isNativeAd3Refresh = true   //是否刷新信息流广告

    override fun getLayoutId() = R.layout.fragment_native_ad

    override fun initEvent() {
        //广告刷新
        nativeAd_btnRefresh.clickNoRepeat {
            isNativeAd1Refresh = true
            isNativeAd2Refresh = true
            isNativeAd3Refresh = true
            loadNativeAd()
        }

        //滑动监听，广告位置可见时进行刷新
        setVisibleLocalListener(
            views = listOf(
                nativeAd_flAd1,
                nativeAd_flAd2,
                nativeAd_flAd3
            ),
            nativeAd_nsv,
            isAddScrollListener = true
        ) { view, isVisible ->
            if (!isVisible) return@setVisibleLocalListener
            when (view) {
                nativeAd_flAd1 -> loadNativeAd1()
                nativeAd_flAd2 -> loadNativeAd2()
                nativeAd_flAd3 -> loadNativeAd3()
            }
        }
    }

    override fun doBusiness() {
        loadNativeAd()
    }

    private fun loadNativeAd() {
        nativeAd_flAd1.isVisibleLocal {
            loadNativeAd1()
        }
        nativeAd_flAd2.isVisibleLocal {
            loadNativeAd2()
        }
        nativeAd_flAd3.isVisibleLocal {
            loadNativeAd3()
        }
    }

    private fun loadNativeAd1() {
        if (!isNativeAd1Refresh) return
        isNativeAd1Refresh = false

        if (nativeAdLoader1 == null) {
            nativeAdLoader1 = TopOn.loadNativeAd(this, config) {
                onAdRenderSuc { flAdView ->
                    nativeAd_flAd1.addView(flAdView)
                }
            }
        }
        nativeAdLoader1?.show()
    }

    private fun loadNativeAd2() {
        if (!isNativeAd2Refresh) return
        isNativeAd2Refresh = false

        if (nativeAdLoader2 == null) {
            nativeAdLoader2 = TopOn.loadNativeAd(this, config) {
                onAdRenderSuc { flAdView ->
                    nativeAd_flAd2.addView(flAdView)
                }
            }
        }
        nativeAdLoader2?.show()
    }

    private fun loadNativeAd3() {
        if (!isNativeAd3Refresh) return
        isNativeAd3Refresh = false

        if (nativeAdLoader3 == null) {
            nativeAdLoader3 = TopOn.loadNativeAd(this, config) {
                onAdRenderSuc { flAdView ->
                    nativeAd_flAd3.addView(flAdView)
                }
            }
        }
        nativeAdLoader3?.show()
    }
}