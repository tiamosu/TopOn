package com.beemans.topon.nativead.banner

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
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
) : LifecycleObserver, ATNativeBannerListener {

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

    private val flAd by lazy {
        FrameLayout(activity).apply {
            layoutParams = this@NativeBannerLoader.layoutParams
        }
    }

    private val layoutParams by lazy { ViewGroup.LayoutParams(nativeWidth, nativeHeight) }

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        atNativeBannerView.apply {
            //配置广告宽高
            val localMap: MutableMap<String, Any> = mutableMapOf()
            localMap.apply {
                put(TTATConst.NATIVE_AD_IMAGE_WIDTH, nativeWidth)
                put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, nativeHeight)
                put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, nativeWidth)
                put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, nativeHeight)
            }.let(this::setLocalExtra)

            setBannerConfig(bannerConfig.atBannerConfig)
            setUnitId(placementId)
            setAdListener(this@NativeBannerLoader)
        }
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)

        loadedLiveData.observe(owner) {
            if (isShowAfterLoaded && !isDestroyed) {
                show()
            }
        }
    }

    /**
     * 广告加载显示
     */
    fun show(): NativeBannerLoader {
        isShowAfterLoaded = true
        isRequesting = NativeManager.isRequesting(placementId)
        if (!isRequesting && !isDestroyed) {
            isShowAfterLoaded = false
            NativeManager.updateRequestStatus(placementId, loaderTag, true)
            atNativeBannerView.loadAd(null)
        }
        return this
    }

    private fun clearView() {
        (flAd.parent as? ViewGroup)?.removeView(flAd)
        if (flAd.childCount > 0) {
            flAd.removeAllViews()
        }
    }

    /**
     * 广告加载成功回调
     */
    override fun onAdLoaded() {
        Log.e(logTag, "onAdLoaded")
        if (isDestroyed) return
        NativeManager.updateRequestStatus(placementId, loaderTag, false)

        clearView()
        flAd.addView(atNativeBannerView, layoutParams)
        NativeBannerCallback().apply(bannerCallback).onAdLoaded?.invoke(flAd)
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onAdError(errorMsg: String?) {
        Log.e(logTag, "onAdError:$errorMsg")
        if (isDestroyed) return
        isShowAfterLoaded = true
        NativeManager.updateRequestStatus(placementId, loaderTag, false)
        NativeBannerCallback().apply(bannerCallback).onAdError?.invoke(errorMsg)
    }

    /**
     * 广告刷新回调
     */
    override fun onAutoRefresh(info: ATAdInfo?) {
        Log.e(logTag, "onAutoRefresh:${info.toString()}")
        if (isDestroyed) return
    }

    /**
     * 广告展示回调
     */
    override fun onAdShow(info: ATAdInfo?) {
        Log.e(logTag, "onAdShow:${info.toString()}")
        if (isDestroyed) return
    }

    /**
     * 广告点击
     */
    override fun onAdClick(info: ATAdInfo?) {
        Log.e(logTag, "onAdClick:${info.toString()}")
        NativeBannerCallback().apply(bannerCallback).onAdClick?.invoke()
    }

    /**
     * 广告刷新失败回调
     */
    override fun onAutoRefreshFail(errorMsg: String?) {
        Log.e(logTag, "onAutoRefreshFail:$errorMsg")
        if (isDestroyed) return
    }

    /**
     * 广告关闭回调（部分广告平台有该回调），可在此处执行移除view的操作
     */
    override fun onAdClose() {
        Log.e(logTag, "onAdClose")
        if (NativeBannerCallback().apply(bannerCallback).onAdClose?.invoke() == true) {
            clearView()
        }
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        clearView()
        NativeManager.release(placementId)
    }
}