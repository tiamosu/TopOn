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
import com.tiamosu.fly.utils.post
import io.reactivex.rxjava3.schedulers.Schedulers

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
            if (isShowAfterLoaded) {
                show()
            }
        }
    }

    /**
     * 广告预加载
     */
    private fun preLoadAd() {
        if (isUsePreload) {
            makeAdRequest()
        }
    }

    /**
     * 广告加载请求
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting =
            InterstitialAdManager.isRequesting(placementId) || isAdPlaying || isDestroyed
        val isAdReady = atInterstitial?.isAdReady ?: false
        if (!isRequesting && !isAdReady) {
            InterstitialAdManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atInterstitial?.load()
            }

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
        if (makeAdRequest()) {
            return this
        }

        isTimeOut = false
        isShowAfterLoaded = false
        if (interstitialAdConfig.scenario.isNotBlank()) {
            atInterstitial?.show(activity, interstitialAdConfig.scenario)
        } else {
            atInterstitial?.show(activity)
        }
        onAdRenderSuc()
        return this
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdRenderSuc?.invoke()
    }

    /**
     * 广告加载超时
     */
    private fun onInterstitialAdTimeOut() {
        if (isDestroyed) return
        Log.e(logTag, "onInterstitialAdTimeOut")

        isTimeOut = true
        isShowAfterLoaded = true
        InterstitialAdManager.updateRequestStatus(placementId, false)
        InterstitialAdCallback().apply(interstitialAdCallback).onAdTimeOut?.invoke()
    }

    /**
     * 广告加载成功回调
     */
    override fun onInterstitialAdLoaded() {
        if (isDestroyed || isTimeOut) return
        Log.e(logTag, "onInterstitialAdLoaded")

        handler.removeCallbacksAndMessages(null)
        InterstitialAdManager.updateRequestStatus(placementId, false)
        InterstitialAdCallback().apply(interstitialAdCallback).onAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onInterstitialAdLoadFail(error: AdError?) {
        if (isDestroyed || isTimeOut) return
        Log.e(logTag, "onInterstitialAdLoadFail:${error?.printStackTrace()}")

        isShowAfterLoaded = true
        handler.removeCallbacksAndMessages(null)
        InterstitialAdManager.updateRequestStatus(placementId, false)
        InterstitialAdCallback().apply(interstitialAdCallback).onAdLoadFail?.invoke(error)
    }

    /**
     * 广告点击
     */
    override fun onInterstitialAdClicked(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onInterstitialAdClicked:${info.toString()}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdClicked?.invoke(info)
    }

    /**
     * 广告展示回调
     */
    override fun onInterstitialAdShow(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onInterstitialAdShow:${info.toString()}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告关闭回调
     */
    override fun onInterstitialAdClose(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onInterstitialAdClose:${info.toString()}")

        isAdPlaying = false
        InterstitialAdCallback().apply(interstitialAdCallback).onAdClose?.invoke(info)
        preLoadAd()
    }

    /**
     * 视频广告刷新回调
     */
    override fun onInterstitialAdVideoStart(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onInterstitialAdVideoStart:${info.toString()}")

        isAdPlaying = true
        InterstitialAdCallback().apply(interstitialAdCallback).onAdVideoStart?.invoke(info)
    }

    /**
     * 视频广告播放结束
     */
    override fun onInterstitialAdVideoEnd(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onInterstitialAdVideoEnd:${info.toString()}")

        isAdPlaying = false
        InterstitialAdCallback().apply(interstitialAdCallback).onAdVideoEnd?.invoke(info)
        preLoadAd()
    }

    /**
     * 视频广告播放失败回调
     */
    override fun onInterstitialAdVideoError(error: AdError?) {
        if (isDestroyed) return
        Log.e(logTag, "onInterstitialAdVideoError:${error?.printStackTrace()}")

        isAdPlaying = false
        InterstitialAdCallback().apply(interstitialAdCallback).onAdVideoError?.invoke(error)
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