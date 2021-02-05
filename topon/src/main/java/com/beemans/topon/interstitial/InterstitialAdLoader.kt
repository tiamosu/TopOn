package com.beemans.topon.interstitial

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialListener
import com.beemans.topon.ext.context
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

    //广告位ID
    private val placementId by lazy { interstitialAdConfig.placementId }

    //请求超时时间
    private val requestTimeOut by lazy { interstitialAdConfig.requestTimeOut }

    //本地参数
    private val localExtra by lazy { interstitialAdConfig.localExtra }

    //广告场景
    private val scenario by lazy { interstitialAdConfig.scenario }

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData by lazy {
        var liveData = InterstitialAdManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = MutableLiveData()
            InterstitialAdManager.loadedLiveDataMap[placementId] = liveData!!
        }
        liveData!!
    }

    //观察者
    private val observer by lazy {
        Observer<Boolean> {
            if (isShowAfterLoaded) {
                showAd(false)
            }
        }
    }

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

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        atInterstitial = ATInterstitial(owner.context, placementId).apply {
            setLocalExtra(localExtra)
            setAdListener(this@InterstitialAdLoader)
        }
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)
        loadedLiveData.observe(owner, observer)
    }

    /**
     * 广告预加载
     */
    fun preLoadAd() {
        makeAdRequest()
    }

    /**
     * 广告加载显示
     */
    fun show(): InterstitialAdLoader {
        return showAd()
    }

    /**
     * 广告加载显示
     *
     * @param isManualShow 是否手动调用进行展示
     */
    private fun showAd(isManualShow: Boolean = true): InterstitialAdLoader {
        if (isManualShow) {
            isRequestAdCallback = true
        }
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }

        isShowAfterLoaded = false
        atInterstitial?.show(owner.context, scenario)
        onAdRenderSuc()
        return this
    }

    /**
     * 广告加载请求
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting =
            InterstitialAdManager.isRequesting(placementId) || isDestroyed
        if (!isRequesting && isRequestAdCallback) {
            isRequestAdCallback = false
            onAdRequest()
        }

        val isAdReady = atInterstitial?.isAdReady ?: false
        if (!isRequesting && !isAdReady) {
            InterstitialAdManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atInterstitial?.load()
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
        InterstitialAdCallback().apply(interstitialAdCallback).onAdRequest?.invoke()
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc:${atAdInfo?.toString()}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdRenderSuc?.invoke(atAdInfo)
    }

    /**
     * 广告加载超时
     */
    private fun onAdLoadTimeOut() {
        if (isDestroyed) return
        Log.e(logTag, "onAdLoadTimeOut")

        isAdLoadTimeOut = true
        InterstitialAdManager.updateRequestStatus(placementId, false)
        InterstitialAdCallback().apply(interstitialAdCallback).onAdLoadTimeOut?.invoke()
    }

    /**
     * 广告加载成功回调
     */
    override fun onInterstitialAdLoaded() {
        atAdInfo = atInterstitial?.checkAdStatus()?.atTopAdInfo

        if (isDestroyed || isAdLoadTimeOut) return
        Log.e(logTag, "onAdLoadSuc:${atAdInfo?.toString()}")

        handler.removeCallbacksAndMessages(null)
        InterstitialAdManager.updateRequestStatus(placementId, false)
        InterstitialAdCallback().apply(interstitialAdCallback).onAdLoadSuc?.invoke(atAdInfo)

        if (isShowAfterLoaded) {
            showAd(false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onInterstitialAdLoadFail(error: AdError?) {
        if (isDestroyed || isAdLoadTimeOut) return
        Log.e(logTag, "onAdLoadFail:${error?.fullErrorInfo}")

        handler.removeCallbacksAndMessages(null)
        InterstitialAdManager.updateRequestStatus(placementId, false)
        InterstitialAdCallback().apply(interstitialAdCallback).onAdLoadFail?.invoke(error)
    }

    /**
     * 广告点击
     */
    override fun onInterstitialAdClicked(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClick:${info.toString()}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdClick?.invoke(info)
    }

    /**
     * 广告展示回调
     */
    override fun onInterstitialAdShow(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdShow:${info.toString()}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告关闭回调
     */
    override fun onInterstitialAdClose(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClose:${info.toString()}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdClose?.invoke(info)
    }

    /**
     * 视频广告刷新回调
     */
    override fun onInterstitialAdVideoStart(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoStart:${info.toString()}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdVideoStart?.invoke(info)
    }

    /**
     * 视频广告播放结束
     */
    override fun onInterstitialAdVideoEnd(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoEnd:${info.toString()}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdVideoEnd?.invoke(info)
    }

    /**
     * 视频广告播放失败回调
     */
    override fun onInterstitialAdVideoError(error: AdError?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoError:${error?.fullErrorInfo}")

        InterstitialAdCallback().apply(interstitialAdCallback).onAdVideoError?.invoke(error)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onAdLoaderDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        loadedLiveData.removeObserver(observer)
        handler.removeCallbacksAndMessages(null)
        InterstitialAdManager.release(placementId)
        atInterstitial?.setAdListener(null)
        atInterstitial = null
    }
}