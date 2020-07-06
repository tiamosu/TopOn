package com.beemans.topon.nativead.banner

import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
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
    private val bannerConfig: NativeBannerConfig,
    private val bannerCallback: NativeBannerCallback.() -> Unit
) : ATNativeBannerListener, LifecycleObserver {

    private val loaderTag by lazy { this.toString() }

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

    private val nativeWidth by lazy { bannerConfig.nativeWidth }
    private val nativeHeight by lazy { bannerConfig.nativeHeight }

    private val layoutParams by lazy {
        ViewGroup.LayoutParams(nativeWidth, nativeHeight)
    }

    //广告正在进行请求
    private var isRequesting = false

    init {
        owner.lifecycle.addObserver(this)
        initNative()
    }

    private fun initNative() {
        atNativeBannerView.apply {
            setUnitId(bannerConfig.placementId)
            setBannerConfig(bannerConfig.atNativeBannerConfig)
            setAdListener(this@NativeBannerLoader)

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

    fun show(): NativeBannerLoader {
        if (isRequesting) return this
        isRequesting = true
        atNativeBannerView.loadAd(null)
        return this
    }

    override fun onAutoRefresh(info: ATAdInfo?) {
        Log.e(loaderTag, "onAutoRefresh")
    }

    override fun onAdShow(info: ATAdInfo?) {
        Log.e(loaderTag, "onAdShow")
    }

    override fun onAdClick(info: ATAdInfo?) {
        Log.e(loaderTag, "onAdClick")
        NativeBannerCallback().apply(bannerCallback).onAdClick?.invoke()
    }

    override fun onAutoRefreshFail(errorMsg: String?) {
        Log.e(loaderTag, "onAutoRefreshFail")
    }

    override fun onAdClose() {
        Log.e(loaderTag, "onAdClose")
    }

    override fun onAdLoaded() {
        Log.e(loaderTag, "onAdLoaded")
        isRequesting = false
        NativeBannerCallback().apply(bannerCallback).onAdLoaded?.invoke(
            atNativeBannerView,
            layoutParams
        )
    }

    override fun onAdError(errorMsg: String?) {
        Log.e(loaderTag, "onAdError:$errorMsg")
        isRequesting = false
        NativeBannerCallback().apply(bannerCallback).onAdError?.invoke(errorMsg)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        (atNativeBannerView.parent as? ViewGroup)?.removeView(atNativeBannerView)
    }
}