package com.beemans.topon.interstitial

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialListener
import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class InterstitialAdLoader(
    private val owner: LifecycleOwner,
    private val interstitialAdConfig: InterstitialAdConfig,
    private val interstitialAdCallback: InterstitialAdCallback.() -> Unit
) : LifecycleObserver, ATInterstitialListener {

    private var atInterstitial: ATInterstitial? = null

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
    private val placementId by lazy { interstitialAdConfig.placementId }

    //是否进行广告预加载
    private val isUsePreload by lazy { interstitialAdConfig.isUsePreload }

    //请求超时时间
    private val requestTimeOut by lazy { interstitialAdConfig.requestTimeOut }

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData: EventLiveData<Boolean> by lazy {
        var liveData = InterstitialAdManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = EventLiveData()
            InterstitialAdManager.loadedLiveDataMap[placementId] = liveData
        }
        liveData
    }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告正在播放
    private var isAdPlaying = false

    //是否超时
    private var isTimeOut = false

    private var isDestroyed = false

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        if (atInterstitial == null) {
            atInterstitial = ATInterstitial(activity, placementId)
            atInterstitial?.setAdListener(this)
        }

        preLoadAd()
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)

        loadedLiveData.observe(owner) {
            if (isShowAfterLoaded && !isDestroyed && !isTimeOut) {
                show()
            }
        }
    }

    /**
     * 广告预加载
     */
    private fun preLoadAd() {
        if (isUsePreload) {
            load()
        }
    }

    /**
     * 广告加载请求
     */
    private fun load(): Boolean {
        val isRequesting =
            InterstitialAdManager.isRequesting(placementId) || isAdPlaying || isDestroyed
        val isAdReady = atInterstitial?.isAdReady ?: false
        if (!isRequesting && !isAdReady) {
            InterstitialAdManager.updateRequestStatus(placementId, loaderTag, true)
            atInterstitial?.load()

            handler.postDelayed({
                onInterstitialAdTimeOut()
            }, requestTimeOut)
            return true
        }
        return isRequesting
    }

    /**
     * 广告加载显示
     */
    fun show(): InterstitialAdLoader {
        isShowAfterLoaded = true
        if (load()) {
            return this
        }
        isTimeOut = false
        isShowAfterLoaded = false

        if (interstitialAdConfig.scenario.isNotBlank()) {
            atInterstitial?.show(activity, interstitialAdConfig.scenario)
        } else {
            atInterstitial?.show(activity)
        }
        adRenderSuc()
        return this
    }

    /**
     * 广告渲染成功
     */
    private fun adRenderSuc() {
        InterstitialAdCallback().apply(interstitialAdCallback).onInterstitialSuc?.invoke()
    }

    /**
     * 广告加载超时
     */
    private fun onInterstitialAdTimeOut() {
        Log.e(logTag, "onInterstitialAdTimeOut")
        if (isDestroyed) return
        isTimeOut = true
        isShowAfterLoaded = true
        InterstitialAdManager.updateRequestStatus(placementId, loaderTag, false)
        InterstitialAdCallback().apply(interstitialAdCallback).onInterstitialAdTimeOut?.invoke()
    }

    /**
     * 广告加载成功回调
     */
    override fun onInterstitialAdLoaded() {
        Log.e(logTag, "onInterstitialAdLoaded")
        if (isDestroyed || isTimeOut) return
        handler.removeCallbacksAndMessages(null)
        InterstitialAdManager.updateRequestStatus(placementId, loaderTag, false)
        InterstitialAdCallback().apply(interstitialAdCallback).onInterstitialAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onInterstitialAdLoadFail(error: AdError?) {
        Log.e(logTag, "onInterstitialAdLoadFail:${error?.printStackTrace()}")
        if (isDestroyed) return
        handler.removeCallbacksAndMessages(null)
        isShowAfterLoaded = true
        InterstitialAdManager.updateRequestStatus(placementId, loaderTag, false)
        InterstitialAdCallback().apply(interstitialAdCallback)
            .onInterstitialAdLoadFail?.invoke(error)
    }

    /**
     * 广告点击
     */
    override fun onInterstitialAdClicked(info: ATAdInfo?) {
        Log.e(logTag, "onInterstitialAdClicked:${info.toString()}")
        InterstitialAdCallback().apply(interstitialAdCallback).onInterstitialAdClicked?.invoke(info)
    }

    /**
     * 广告展示回调
     */
    override fun onInterstitialAdShow(info: ATAdInfo?) {
        Log.e(logTag, "onInterstitialAdShow:${info.toString()}")
        if (isDestroyed) return
        InterstitialAdCallback().apply(interstitialAdCallback).onInterstitialAdShow?.invoke(info)
    }

    /**
     * 广告关闭回调
     */
    override fun onInterstitialAdClose(info: ATAdInfo?) {
        Log.e(logTag, "onInterstitialAdClose:${info.toString()}")
        isAdPlaying = false
        InterstitialAdCallback().apply(interstitialAdCallback).onInterstitialAdClose?.invoke(info)
        preLoadAd()
    }

    /**
     * 视频广告刷新回调
     */
    override fun onInterstitialAdVideoStart(info: ATAdInfo?) {
        Log.e(logTag, "onInterstitialAdVideoStart:${info.toString()}")
        if (isDestroyed) return
        isAdPlaying = true
        InterstitialAdCallback().apply(interstitialAdCallback)
            .onInterstitialAdVideoStart?.invoke(info)
    }

    /**
     * 视频广告播放结束
     */
    override fun onInterstitialAdVideoEnd(info: ATAdInfo?) {
        Log.e(logTag, "onInterstitialAdVideoEnd:${info.toString()}")
        if (isDestroyed) return
        isAdPlaying = false
        InterstitialAdCallback().apply(interstitialAdCallback).onInterstitialAdVideoEnd?.invoke(info)
        preLoadAd()
    }

    /**
     * 视频广告播放失败回调
     */
    override fun onInterstitialAdVideoError(error: AdError?) {
        Log.e(logTag, "onInterstitialAdVideoError:${error?.printStackTrace()}")
        if (isDestroyed) return
        isAdPlaying = false
        InterstitialAdCallback().apply(interstitialAdCallback)
            .onInterstitialAdVideoError?.invoke(error)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")
        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        InterstitialAdManager.release(placementId)
        atInterstitial = null
    }
}