package com.beemans.topon.splash

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.contains
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

    //是否超时
    private var isAdLoadTimeOut = false

    //页面是否已经销毁了
    private var isDestroyed = false

    //是否进行广告请求回调
    private var isRequestAdCallback = false

    //广告信息对象
    private var atAdInfo: ATAdInfo? = null

    private val flContainer by lazy {
        FrameLayout(owner.context).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
        }
    }

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
                showAd(isManualShow = false)
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
     */
    fun show(): SplashAdLoader {
        return showAd()
    }

    /**
     * 广告加载显示
     *
     * @param isManualShow 是否手动调用进行展示
     */
    private fun showAd(isManualShow: Boolean = true): SplashAdLoader {
        if (isManualShow) {
            isRequestAdCallback = true
        }
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }

        isShowAfterLoaded = false
        atSplashAd?.show(owner.context, flContainer)
        onAdRenderSuc()
        return this
    }

    /**
     * 广告请求加载
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting = SplashAdManager.isRequesting(placementId) || isDestroyed
        if (!isRequesting && isRequestAdCallback) {
            isRequestAdCallback = false
            onAdRequest()
        }

        val isAdReady = atSplashAd?.isAdReady ?: false
        if (!isRequesting && !isAdReady) {
            SplashAdManager.updateRequestStatus(placementId, true)

            try {
                post(Schedulers.io()) {
                    atSplashAd = ATSplashAd(
                        owner.context,
                        placementId,
                        atMediationRequestInfo,
                        this,
                        requestTimeOut.toInt()
                    ).apply {
                        setLocalExtra(localExtra)
                        loadAd()
                    }
                }
            } catch (e: Exception) {
            }

            //开始进行超时倒计时
            handler.postDelayed({
                onAdLoadTimeOut()
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

        isAdLoadTimeOut = false
        SplashAdCallback().apply(splashAdCallback).onAdRequest?.invoke()
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc:${atAdInfo?.toString()}")

        if (!flAdView.contains(flContainer)) {
            flAdView.addView(flContainer)
        }
        val parent = flAdView.parent
        if (parent is ViewGroup && parent.contains(flAdView)) {
            parent.removeView(flAdView)
        }
        SplashAdCallback().apply(splashAdCallback).onAdRenderSuc?.invoke(flAdView, atAdInfo)
    }

    /**
     * 广告超时
     */
    private fun onAdLoadTimeOut() {
        if (isDestroyed) return
        Log.e(logTag, "onAdLoadTimeOut")

        isAdLoadTimeOut = true
        SplashAdManager.updateRequestStatus(placementId, false)
        SplashAdCallback().apply(splashAdCallback).onAdLoadTimeOut?.invoke()
    }

    /**
     * 广告加载成功回调
     */
    override fun onAdLoaded() {
        atAdInfo = atSplashAd?.checkAdStatus()?.atTopAdInfo

        if (isDestroyed || isAdLoadTimeOut) return
        Log.e(logTag, "onAdLoadSuc:${atAdInfo?.toString()}")

        handler.removeCallbacksAndMessages(null)
        SplashAdManager.updateRequestStatus(placementId, false)
        SplashAdCallback().apply(splashAdCallback).onAdLoadSuc?.invoke(atAdInfo)

        if (isShowAfterLoaded) {
            showAd(isManualShow = false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onNoAdError(error: AdError?) {
        if (isDestroyed || isAdLoadTimeOut) return
        Log.e(logTag, "onAdLoadFail:${error?.fullErrorInfo}")

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
        Log.e(logTag, "onAdClose:${info.toString()}")

        if (SplashAdCallback().apply(splashAdCallback).onAdClose?.invoke(info) == true) {
            clearView()
        }
    }

    private fun clearView() {
        if (flAdView.childCount > 0) {
            flAdView.removeAllViews()
        }
        val parent = flAdView.parent
        if (parent is ViewGroup && parent.contains(flAdView)) {
            parent.removeView(flAdView)
        }
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onAdLoaderDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        loadedLiveData.removeObserver(observer)
        handler.removeCallbacksAndMessages(null)
        clearView()
        SplashAdManager.release(placementId)
        atSplashAd?.onDestory()
        atSplashAd = null
    }
}