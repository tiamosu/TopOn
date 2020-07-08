package com.beemans.topon.demo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.nativead.NativeAdLoader
import com.tiamosu.fly.ext.navigate
import kotlinx.android.synthetic.main.fragment_native_ad.*

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class NativeAdFragment : BaseFragment() {
    private var nativeAdLoader: NativeAdLoader? = null
    private var isShowNewPageBtn = true

    companion object {
        const val SHOW_NEW_PAGE_BTN = "SHOW_NEW_PAGE_BTN"
    }

    override fun getLayoutId() = R.layout.fragment_native_ad

    override fun initParameters(bundle: Bundle?) {
        bundle?.apply {
            isShowNewPageBtn = getBoolean(SHOW_NEW_PAGE_BTN, true)
        }
    }

    override fun initView(rootView: View?) {
        nativeAd_btnOpenPage.isVisible = isShowNewPageBtn
    }

    override fun initEvent() {
        nativeAd_btnNativeAd.setOnClickListener {
            if (nativeAdLoader == null) {
                val config = NativeAdConfig(Constant.NATIVE_AD_ID, 350.pt2px, 270.pt2px)
                nativeAdLoader = TopOn.loadNativeAd(this, config) {
                    onNativeRenderSuc { flAd ->
                        nativeAd_flAd.addView(flAd)
                    }
                    onNativeAdLoadFail {
                        Log.e("xia", "${this.javaClass.simpleName}show:${it?.printStackTrace()}")
                    }
                    onAdCloseButtonClick { _, _ ->
                        true
                    }
                }
            }
            nativeAdLoader?.show()
        }

        nativeAd_btnOpenPage.setOnClickListener {
            navigate(R.id.action_nativeAdFragment_to_nativeAdFragment, Bundle().apply {
                putBoolean(SHOW_NEW_PAGE_BTN, false)
            })
        }
    }
}