package com.beemans.topon.nativead.banner

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.banner.api.ATNativeBannerListener
import com.anythink.nativead.banner.api.ATNativeBannerView
import com.anythink.network.mintegral.MintegralATConst
import com.anythink.network.toutiao.TTATConst

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerLoader(
    private val owner: LifecycleOwner,
    private val nativeBannerConfig: NativeBannerConfig
) : ATNativeBannerListener {
    private val activity by lazy {
        when (owner) {
            is Fragment -> {
                owner.requireActivity()
            }
            is FragmentActivity -> {
                owner
            }
            else -> {
                throw IllegalArgumentException("owner must instanceof Fragment or FragmentActivity！")
            }
        }
    }

    private val atNativeBannerView by lazy { ATNativeBannerView(activity) }

    private val nativeWidth by lazy { nativeBannerConfig.nativeWidth }
    private val nativeHeight by lazy { nativeBannerConfig.nativeHeight }

    init {
        initNative()
    }

    private fun initNative() {
        atNativeBannerView.apply {
//            isVisible = false
            setUnitId(nativeBannerConfig.placementId)
            setBannerConfig(nativeBannerConfig.atNativeBannerConfig)

            //配置广告宽高
            val localMap: MutableMap<String, Any> = mutableMapOf()
            localMap.apply {
                put(TTATConst.NATIVE_AD_IMAGE_WIDTH, nativeWidth)
                put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, nativeHeight)
                put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, nativeWidth)
                put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, nativeHeight)
            }.let(this::setLocalExtra)
        }
    }

    fun show(
        onShow: (
            atNativeBannerView: ATNativeBannerView,
            layoutParams: ViewGroup.LayoutParams
        ) -> Unit
    ) {
        atNativeBannerView.loadAd(null)

        val params = ViewGroup.LayoutParams(nativeWidth, nativeHeight)
        onShow.invoke(atNativeBannerView, params)
    }

    override fun onAutoRefresh(info: ATAdInfo?) {
    }

    override fun onAdShow(info: ATAdInfo?) {
    }

    override fun onAdClick(info: ATAdInfo?) {
    }

    override fun onAutoRefreshFail(errorMsg: String?) {
    }

    override fun onAdClose() {
    }

    override fun onAdLoaded() {
    }

    override fun onAdError(errorMsg: String?) {
    }
}