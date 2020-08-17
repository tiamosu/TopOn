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
import com.tiamosu.fly.callback.EventLiveData
import com.tiamosu.fly.utils.post
import io.reactivex.rxjava3.schedulers.Schedulers

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

    //广告加载完成
    private var isAdLoaded = false

    //是否超时
    private var isTimeOut = false

    private var isDestroyed = false

    private val frameLayout by lazy {
        FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
        }
    }

    private val flAdView by lazy { FrameLayout(activity) }

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData: EventLiveData<Boolean> by lazy {
        var liveData = SplashAdManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = EventLiveData()
            SplashAdManager.loadedLiveDataMap[placementId] = liveData
        }
        liveData
    }

    init {
        createObserve()
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)

        loadedLiveData.observe(owner) {
            if (isShowAfterLoaded) {
                show()
            }
        }
    }

    /**
     * 广告加载显示
     */
    fun show(): SplashAdLoader {
        isShowAfterLoaded = true
        if (onAdLoad()) {
            return this
        }

        isTimeOut = false
        isShowAfterLoaded = false
        onAdRenderSuc()
        return this
    }

    /**
     * 广告请求加载
     */
    private fun onAdLoad(): Boolean {
        val isRequesting = SplashAdManager.isRequesting(placementId) || isAdPlaying || isDestroyed
        if (!isRequesting && !isAdLoaded) {
            SplashAdManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atSplashAd = ATSplashAd(activity, frameLayout, placementId, this)
            }

            handler.postDelayed({
                onAdTimeOut()
            }, requestTimeOut)
            return true
        }
        return isRequesting
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc")

        clearView()
        flAdView.addView(frameLayout)
        SplashAdCallback().apply(splashAdCallback).onAdRenderSuc?.invoke(flAdView)
    }

    /**
     * 广告超时
     */
    private fun onAdTimeOut() {
        if (isDestroyed) return
        Log.e(logTag, "onAdTimeOut")

        isTimeOut = true
        isShowAfterLoaded = true
        SplashAdManager.updateRequestStatus(placementId, false)
        SplashAdCallback().apply(splashAdCallback).onAdTimeOut?.invoke()
    }

    private fun clearView() {
        (flAdView.parent as? ViewGroup)?.removeView(flAdView)
        if (flAdView.childCount > 0) {
            flAdView.removeAllViews()
        }
    }

    /**
     * 广告加载成功回调
     */
    override fun onAdLoaded() {
        if (isDestroyed || isTimeOut) return
        Log.e(logTag, "onAdLoaded")

        isAdLoaded = true
        handler.removeCallbacksAndMessages(null)
        SplashAdManager.updateRequestStatus(placementId, false)
        SplashAdCallback().apply(splashAdCallback).onAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onNoAdError(error: AdError?) {
        if (isDestroyed || isTimeOut) return
        Log.e(logTag, "onNoAdError:${error?.printStackTrace()}")

        isShowAfterLoaded = true
        handler.removeCallbacksAndMessages(null)
        SplashAdManager.updateRequestStatus(placementId, false)
        SplashAdCallback().apply(splashAdCallback).onAdError?.invoke(error)
    }

    /**
     * 广告展示回调
     */
    override fun onAdShow(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdShow:${info.toString()}")

        isAdPlaying = true
        SplashAdCallback().apply(splashAdCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告点击回调
     */
    override fun onAdClick(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClick:${info.toString()}")

        SplashAdCallback().apply(splashAdCallback).onAdClick?.invoke(info)
    }

    /**
     * 广告关闭回调
     */
    override fun onAdDismiss(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdDismiss:${info.toString()}")

        if (SplashAdCallback().apply(splashAdCallback).onAdDismiss?.invoke(info) == true) {
            isAdPlaying = false
            isAdLoaded = false
            clearView()
        }
    }

    /**
     * 广告的倒计时回调
     */
    override fun onAdTick(tickTime: Long) {
        if (isDestroyed) return
        Log.e(logTag, "onAdTick:$tickTime")

        SplashAdCallback().apply(splashAdCallback).onAdTick?.invoke(tickTime)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        handler.removeCallbacksAndMessages(null)
        clearView()
        SplashAdManager.release(placementId)
        atSplashAd?.onDestory()
        atSplashAd = null
    }
}