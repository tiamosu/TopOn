package com.beemans.topon.nativead.splash

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
import com.anythink.nativead.splash.api.ATNativeSplash
import com.anythink.nativead.splash.api.ATNativeSplashListener
import com.anythink.network.mintegral.MintegralATConst
import com.anythink.network.toutiao.TTATConst
import com.beemans.topon.nativead.NativeManager
import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeSplashLoader(
    private val owner: LifecycleOwner,
    private val splashConfig: NativeSplashConfig,
    private val splashCallback: NativeSplashCallback.() -> Unit
) : LifecycleObserver, ATNativeSplashListener {

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

    private val nativeWidth by lazy { splashConfig.nativeWidth }
    private val nativeHeight by lazy { splashConfig.nativeHeight }

    //广告位ID
    private val placementId by lazy { splashConfig.placementId }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告正在播放
    private var isAdPlaying = false

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

    //配置广告宽高
    private val localMap: MutableMap<String, Any> by lazy { mutableMapOf() }

    private val frameLayout by lazy {
        FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
        }
    }

    private val flAd by lazy { FrameLayout(activity) }

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        localMap.apply {
            put(TTATConst.NATIVE_AD_IMAGE_WIDTH, nativeWidth)
            put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, nativeHeight)
            put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, nativeWidth)
            put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, nativeHeight)
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
    fun show(): NativeSplashLoader {
        isShowAfterLoaded = true
        val isRequesting = NativeManager.isRequesting(placementId) || isAdPlaying || isDestroyed
        if (isRequesting) {
            return this
        }

        isShowAfterLoaded = false
        NativeManager.updateRequestStatus(placementId, loaderTag, true)
        ATNativeSplash(
            activity,
            frameLayout,
            null,
            placementId,
            localMap,
            splashConfig.requestTimeOut,
            splashConfig.fetchDelay,
            this
        )
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
        flAd.addView(frameLayout)
        NativeSplashCallback().apply(splashCallback).onAdLoaded?.invoke(flAd)
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onNoAdError(errorMsg: String?) {
        Log.e(logTag, "onNoAdError:$errorMsg")
        if (isDestroyed) return
        isShowAfterLoaded = true
        NativeManager.updateRequestStatus(placementId, loaderTag, false)
        NativeSplashCallback().apply(splashCallback).onNoAdError?.invoke(errorMsg)
    }

    /**
     * Skip按钮点击回调
     */
    override fun onAdSkip() {
        Log.e(logTag, "onAdSkip")
        isAdPlaying = false
        if (NativeSplashCallback().apply(splashCallback).onAdSkip?.invoke() == true) {
            clearView()
        }
    }

    /**
     * 广告展示回调
     */
    override fun onAdShow(info: ATAdInfo?) {
        Log.e(logTag, "onAdShow:${info.toString()}")
        if (isDestroyed) return
        isAdPlaying = true
        NativeSplashCallback().apply(splashCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告点击回调
     */
    override fun onAdClick(info: ATAdInfo?) {
        Log.e(logTag, "onAdClick:${info.toString()}")
        NativeSplashCallback().apply(splashCallback).onAdClick?.invoke(info)
    }

    /**
     * 广告的倒计时回调，用于倒计时秒数的刷新，返回单位：毫秒
     */
    override fun onAdTick(tickTime: Long) {
        Log.e(logTag, "onAdTick:$tickTime")
        if (isDestroyed) return
        NativeSplashCallback().apply(splashCallback).onAdTick?.invoke(tickTime)
    }

    /**
     * 广告的倒计时结束，可在这里关闭NativeSplash广告的Activity
     */
    override fun onAdTimeOver() {
        Log.e(logTag, "onAdTimeOver")
        if (isDestroyed) return
        isAdPlaying = false
        NativeSplashCallback().apply(splashCallback).onAdTimeOver?.invoke()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")
        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        clearView()
        NativeManager.release(placementId)
    }
}