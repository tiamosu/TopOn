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
import com.beemans.topon.nativead.NativeManager
import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerLoader(
    private val owner: LifecycleOwner,
    private val bannerConfig: NativeBannerConfig,
    private val bannerCallback: NativeBannerCallback.() -> Unit
) : ATNativeBannerListener, LifecycleObserver {

    private val logTag by lazy { this.javaClass.simpleName }
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

    //广告位ID
    private val placementId by lazy { bannerConfig.placementId }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    private var isDestroyed = false

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData: EventLiveData<Boolean> by lazy {
        var liveData = NativeManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = EventLiveData()
            NativeManager.loadedLiveDataMap[placementId] = liveData
        }
        liveData
    }

    init {
        owner.lifecycle.addObserver(this)
        initNative()
        createObserve()
    }

    private fun initNative() {
        atNativeBannerView.apply {
            //配置广告宽高
            val localMap: MutableMap<String, Any> = mutableMapOf()
            localMap.apply {
                put(TTATConst.NATIVE_AD_IMAGE_WIDTH, nativeWidth)
                put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, nativeHeight)
                put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, nativeWidth)
                put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, nativeHeight)
            }.let(this::setLocalExtra)

            setUnitId(placementId)
            setBannerConfig(bannerConfig.atNativeBannerConfig)
            setAdListener(this@NativeBannerLoader)
        }
    }

    private fun createObserve() {
        loadedLiveData.observe(owner) {
            if (isShowAfterLoaded && !isDestroyed) {
                show()
            }
        }
    }

    fun show(): NativeBannerLoader {
        isShowAfterLoaded = true
        isRequesting = NativeManager.isRequesting(placementId)
        if (!isRequesting) {
            isShowAfterLoaded = false
            NativeManager.updateRequestStatus(placementId, loaderTag, true)
            atNativeBannerView.loadAd(null)
        }
        return this
    }

    override fun onAutoRefresh(info: ATAdInfo?) {
        Log.e(logTag, "onAutoRefresh")
    }

    override fun onAdShow(info: ATAdInfo?) {
        Log.e(logTag, "onAdShow")
    }

    override fun onAdClick(info: ATAdInfo?) {
        Log.e(logTag, "onAdClick")
        NativeBannerCallback().apply(bannerCallback).onAdClick?.invoke()
    }

    override fun onAutoRefreshFail(errorMsg: String?) {
        Log.e(logTag, "onAutoRefreshFail")
    }

    override fun onAdClose() {
        Log.e(logTag, "onAdClose")
    }

    override fun onAdLoaded() {
        Log.e(logTag, "onAdLoaded")
        NativeManager.updateRequestStatus(placementId, loaderTag, false)
        NativeBannerCallback().apply(bannerCallback).onAdLoaded?.invoke(
            atNativeBannerView,
            layoutParams
        )
        loadedLiveData.value = true
    }

    override fun onAdError(errorMsg: String?) {
        Log.e(logTag, "onAdError:$errorMsg")
        isShowAfterLoaded = true
        NativeManager.updateRequestStatus(placementId, loaderTag, false)
        NativeBannerCallback().apply(bannerCallback).onAdError?.invoke(errorMsg)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        (atNativeBannerView.parent as? ViewGroup)?.removeView(atNativeBannerView)
        NativeManager.release(placementId)
    }
}