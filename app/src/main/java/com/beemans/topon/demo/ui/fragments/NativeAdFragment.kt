package com.beemans.topon.demo.ui.fragments

import android.view.ViewGroup
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.nativead.api.ATNative
import com.anythink.nativead.api.ATNativeAdView
import com.anythink.nativead.api.ATNativeDislikeListener
import com.anythink.nativead.api.ATNativeNetworkListener
import com.anythink.network.mintegral.MintegralATConst
import com.anythink.network.toutiao.TTATConst
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.dp2px
import com.beemans.topon.nativead.NativeAdRender
import com.beemans.topon.nativead.NativeLoader
import kotlinx.android.synthetic.main.fragment_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class NativeAdFragment : BaseFragment() {
    private var nativeLoader: NativeLoader? = null
    private var atNative: ATNative? = null
    private val atNativeAdView by lazy { ATNativeAdView(context) }
    private val nativeAdRender by lazy { NativeAdRender() }

    override fun getLayoutId() = R.layout.fragment_native_ad

    override fun initEvent() {
        nativeAd_btnNativeAd.setOnClickListener {
//            if (nativeLoader == null) {
//                val nativeStrategy =
//                    NativeStrategy(Constant.NATIVE_AD_ID, 350.dp2px, 300.dp2px)
//                nativeLoader = NativeLoader(this, nativeStrategy) {
////                nativeLoader = TopOn.loadNative(this, nativeStrategy) {
//                    onNativeRenderSuc { atNativeAdView, layoutParams ->
//                        if (nativeAd_flAd.childCount > 0) {
//                            nativeAd_flAd.removeAllViews()
//                        }
//                        nativeAd_flAd.addView(atNativeAdView, layoutParams)
//                    }
//                    onNativeAdLoadFail {
//                        Log.e("xia", "${this.javaClass.simpleName}show:${it?.printStackTrace()}")
//                    }
//                    onNativeCloseClicked {
//                        Log.e("xia", "NativeAdFragment")
//                        if (nativeAd_flAd.childCount > 0) {
//                            nativeAd_flAd.removeAllViews()
//                        }
//                    }
//                }
//            }
//            nativeLoader?.show()

            if (atNative == null) {
                atNative =
                    ATNative(context, Constant.NATIVE_AD_ID, object : ATNativeNetworkListener {
                        override fun onNativeAdLoadFail(p0: AdError?) {
                        }

                        override fun onNativeAdLoaded() {
                            atNative?.nativeAd?.apply {
                                setDislikeCallbackListener(DislikeCallback())
                                renderAdView(atNativeAdView, nativeAdRender)
                                prepare(atNativeAdView)
                                if (nativeAd_flAd.childCount > 0) {
                                    nativeAd_flAd.removeAllViews()
                                }
                                val params = ViewGroup.LayoutParams(350.dp2px, 300.dp2px)
                                nativeAd_flAd.addView(atNativeAdView, params)
                            }
                        }
                    }).apply {
                        //配置广告宽高
                        val localMap: MutableMap<String, Any> = mutableMapOf()
                        localMap.apply {
                            put(TTATConst.NATIVE_AD_IMAGE_WIDTH, 350.dp2px)
                            put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, 300.dp2px)
                            put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, 350.dp2px)
                            put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, 300.dp2px)
                        }.let(this::setLocalExtra)
                    }
            }
            atNative?.makeAdRequest()
        }
    }

    class DislikeCallback : ATNativeDislikeListener() {
        override fun onAdCloseButtonClick(view: ATNativeAdView?, info: ATAdInfo?) {
            (view?.parent as? ViewGroup)?.removeView(view)
        }
    }
}