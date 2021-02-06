package com.beemans.topon.demo.ui.fragments

import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.databinding.FragmentNativeAdBinding
import com.beemans.topon.demo.ext.isVisibleLocal
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.demo.ext.setVisibleLocalListener
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.nativead.NativeAdLoader
import com.tiamosu.fly.ext.clickNoRepeat
import com.tiamosu.fly.ext.navigate

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class NativeAdFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentNativeAdBinding }
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
        dataBinding.nativeAdBtnRefresh.clickNoRepeat {
            isNativeAd1Refresh = true
            isNativeAd2Refresh = true
            isNativeAd3Refresh = true
            loadNativeAd()
        }

        dataBinding.nativeAdBtnTest.clickNoRepeat {
            navigate(R.id.action_to_nativeAdFragment)
        }

        //滑动监听，广告位置可见时进行刷新
        setVisibleLocalListener(
            views = listOf(
                dataBinding.nativeAdFlAd1,
                dataBinding.nativeAdFlAd2,
                dataBinding.nativeAdFlAd3,
            ),
            dataBinding.nativeAdNsv,
            isAddScrollListener = true
        ) { view, isVisible ->
            if (!isVisible) return@setVisibleLocalListener
            when (view) {
                dataBinding.nativeAdFlAd1 -> loadNativeAd1()
                dataBinding.nativeAdFlAd2 -> loadNativeAd2()
                dataBinding.nativeAdFlAd3 -> loadNativeAd3()
            }
        }
    }

    override fun doBusiness() {
        loadNativeAd()
    }

    private fun loadNativeAd() {
        dataBinding.nativeAdFlAd1.isVisibleLocal {
            loadNativeAd1()
        }
        dataBinding.nativeAdFlAd2.isVisibleLocal {
            loadNativeAd2()
        }
        dataBinding.nativeAdFlAd3.isVisibleLocal {
            loadNativeAd3()
        }
    }

    private fun loadNativeAd1() {
        if (!isNativeAd1Refresh) return
        isNativeAd1Refresh = false

        if (nativeAdLoader1 == null) {
            val config = NativeAdConfig(
                Constant.NATIVE_AD_ID,
                350.pt2px,
                270.pt2px,
                isHighlyAdaptive = false
            )
            nativeAdLoader1 = TopOn.loadNativeAd(this, config) {
                onAdRenderSuc { flAdView, _ ->
                    dataBinding.nativeAdFlAd1.addView(flAdView)
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
                onAdRenderSuc { flAdView, _ ->
                    dataBinding.nativeAdFlAd2.addView(flAdView)
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
                onAdRenderSuc { flAdView, _ ->
                    dataBinding.nativeAdFlAd3.addView(flAdView)
                }
            }
        }
        nativeAdLoader3?.show()
    }
}