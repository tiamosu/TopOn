package com.beemans.topon.splash

import android.os.Handler
import android.os.Looper
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
import com.anythink.core.api.AdError
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.beemans.topon.nativead.NativeManager
import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/9.
 */
class SplashAdLoader(
    private val owner: LifecycleOwner,
    private val splashAdConfig: SplashAdConfig,
    private val splashAdCallback: SplashAdCallback.() -> Unit
) : LifecycleObserver, ATSplashAdListener {

    private var atSplashAd: ATSplashAd? = null

    private val logTag by lazy { this.javaClass.simpleName }
    private val loaderTag by lazy { this.toString() }
    private val handler by lazy { Handler(Looper.getMainLooper()) }

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

    //广告位ID
    private val placementId by lazy { splashAdConfig.placementId }

    //请求超时时间
    private val requestTimeOut by lazy { splashAdConfig.requestTimeOut }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告正在播放
    private var isAdPlaying = false

    private var isDestroyed = false

    private val frameLayout by lazy {
        FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
        }
    }

    private val flAd by lazy { FrameLayout(activity) }

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
        createObserve()
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)
    }

    /**
     * 广告加载显示
     */
    fun show(): SplashAdLoader {
        isShowAfterLoaded = true
        if (SplashAdManager.isRequesting(placementId) || isAdPlaying || isDestroyed) {
            return this
        }

        isShowAfterLoaded = false
        SplashAdManager.updateRequestStatus(placementId, loaderTag, true)
        atSplashAd = ATSplashAd(activity, frameLayout, placementId, this)
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
        SplashAdManager.updateRequestStatus(placementId, loaderTag, false)

        clearView()
        flAd.addView(frameLayout)
        SplashAdCallback().apply(splashAdCallback).onAdLoaded?.invoke(flAd)
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onNoAdError(error: AdError?) {
        Log.e(logTag, "onNoAdError:${error?.printStackTrace()}")
        if (isDestroyed) return
        isShowAfterLoaded = true
        SplashAdManager.updateRequestStatus(placementId, loaderTag, false)
        SplashAdCallback().apply(splashAdCallback).onNoAdError?.invoke(error)
    }

    /**
     * 广告展示回调
     */
    override fun onAdShow(info: ATAdInfo?) {
        Log.e(logTag, "onAdShow:${info.toString()}")
        if (isDestroyed) return
        isAdPlaying = true
        SplashAdCallback().apply(splashAdCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告点击回调
     */
    override fun onAdClick(info: ATAdInfo?) {
        Log.e(logTag, "onAdClick:${info.toString()}")
        SplashAdCallback().apply(splashAdCallback).onAdClick?.invoke(info)
    }

    /**
     * 广告关闭回调
     */
    override fun onAdDismiss(info: ATAdInfo?) {
        Log.e(logTag, "onAdDismiss:${info.toString()}")
        isAdPlaying = false
        if (SplashAdCallback().apply(splashAdCallback).onAdDismiss?.invoke(info) == true) {
            clearView()
        }
    }

    /**
     * 广告的倒计时回调
     */
    override fun onAdTick(tickTime: Long) {
        Log.e(logTag, "onAdTick:$tickTime")
        if (isDestroyed) return
        SplashAdCallback().apply(splashAdCallback).onAdTick?.invoke(tickTime)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")
        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        clearView()
        SplashAdManager.release(placementId)
        atSplashAd = null
    }
}