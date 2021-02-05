package com.beemans.topon.splash

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.*
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.beemans.topon.ext.context
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

    //广告位ID
    private val placementId by lazy { splashAdConfig.placementId }

    //请求超时时间
    private val requestTimeOut by lazy { splashAdConfig.requestTimeOut }

    //各个平台参数信息
    private val atMediationRequestInfo by lazy { splashAdConfig.atRequestInfo }

    //本地参数
    private val localExtra by lazy { splashAdConfig.localExtra }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告正在播放
    private var isAdPlaying = false

    //是否超时
    private var isTimeOut = false

    //页面是否已经销毁了
    private var isDestroyed = false

    //是否进行广告请求回调
    private var isRequestAdCallback = false

    private var flContainer: FrameLayout? = null

    private val flAdView by lazy { FrameLayout(owner.context) }

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData by lazy {
        var liveData = SplashAdManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = MutableLiveData()
            SplashAdManager.loadedLiveDataMap[placementId] = liveData!!
        }
        liveData!!
    }

    //观察者
    private val observer by lazy {
        Observer<Boolean> {
            if (isShowAfterLoaded) {
                show(false)
            }
        }
    }

    init {
        createObserve()
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)
        loadedLiveData.observe(owner, observer)
    }

    /**
     * 广告加载显示
     *
     * @param isManualShow 是否手动调用进行展示
     */
    fun show(isManualShow: Boolean = true): SplashAdLoader {
        if (isManualShow) {
            isRequestAdCallback = true
        }
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }

        isShowAfterLoaded = false
        flContainer = FrameLayout(owner.context).apply {
            layoutParams = FrameLayout.LayoutParams(-1, -1)
        }
        atSplashAd?.show(owner.context, flContainer)
        onAdRenderSuc()
        return this
    }

    /**
     * 广告请求加载
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting = SplashAdManager.isRequesting(placementId) || isAdPlaying || isDestroyed
        if (!isRequesting && isRequestAdCallback) {
            isRequestAdCallback = false
            onAdRequest()
        }

        val isAdReady = atSplashAd?.isAdReady ?: false
        if (!isRequesting && !isAdReady) {
            SplashAdManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                ATSplashAd(
                    owner.context,
                    placementId,
                    atMediationRequestInfo,
                    this,
                    requestTimeOut.toInt()
                ).apply {
                    setLocalExtra(localExtra)
                    loadAd()
                }.also {
                    atSplashAd = it
                }
            }

            handler.postDelayed({
                onAdTimeOut()
            }, requestTimeOut)
            return true
        }
        return isRequesting
    }

    /**
     * 广告请求
     */
    private fun onAdRequest() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRequest")

        isTimeOut = false
        SplashAdCallback().apply(splashAdCallback).onAdRequest?.invoke()
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc")

        clearView()
        if (flContainer != null) {
            flAdView.addView(flContainer)
        }
        SplashAdCallback().apply(splashAdCallback).onAdRenderSuc?.invoke(flAdView)
    }

    /**
     * 广告超时
     */
    private fun onAdTimeOut() {
        if (isDestroyed) return
        Log.e(logTag, "onAdTimeOut")

        isTimeOut = true
        SplashAdManager.updateRequestStatus(placementId, false)
        SplashAdCallback().apply(splashAdCallback).onAdLoadTimeOut?.invoke()
    }

    private fun clearView() {
        val parent = flAdView.parent
        if (parent is ViewGroup && parent.childCount > 0) {
            parent.removeAllViews()
        }
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

        handler.removeCallbacksAndMessages(null)
        SplashAdManager.updateRequestStatus(placementId, false)
        SplashAdCallback().apply(splashAdCallback).onAdLoadSuc?.invoke()

        if (isShowAfterLoaded) {
            show(false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onNoAdError(error: AdError?) {
        if (isDestroyed || isTimeOut) return
        Log.e(logTag, "onNoAdError:${error?.printStackTrace()}")

        handler.removeCallbacksAndMessages(null)
        SplashAdManager.updateRequestStatus(placementId, false)
        SplashAdCallback().apply(splashAdCallback).onAdLoadFail?.invoke(error)
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

        if (SplashAdCallback().apply(splashAdCallback).onAdClose?.invoke(info) == true) {
            isAdPlaying = false
            clearView()
        }
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        loadedLiveData.removeObserver(observer)
        handler.removeCallbacksAndMessages(null)
        clearView()
        SplashAdManager.release(placementId)
        atSplashAd?.onDestory()
        flContainer = null
        atSplashAd = null
    }
}