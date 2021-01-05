package com.beemans.topon.nativead.splash

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.*
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.splash.api.ATNativeSplash
import com.anythink.nativead.splash.api.ATNativeSplashListener
import com.beemans.topon.ext.context
import com.beemans.topon.nativead.NativeManager
import com.tiamosu.fly.utils.post
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeSplashLoader(
    private val owner: LifecycleOwner,
    private val nativeSplashConfig: NativeSplashConfig,
    private val nativeSplashCallback: NativeSplashCallback.() -> Unit
) : LifecycleObserver, ATNativeSplashListener {

    private var atNativeSplash: ATNativeSplash? = null

    private val logTag by lazy { this.javaClass.simpleName }

    private val nativeWidth by lazy { nativeSplashConfig.nativeWidth }
    private val nativeHeight by lazy { nativeSplashConfig.nativeHeight }

    //广告位ID
    private val placementId by lazy { nativeSplashConfig.placementId }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告正在播放
    private var isAdPlaying = false

    //广告加载完成
    private var isAdLoaded = false

    //广告是否已经渲染
    private var isAdRendered = false

    //页面是否已经销毁了
    private var isDestroyed = false

    //是否进行广告请求回调
    private var isRequestAdCallback = false

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData by lazy {
        var liveData = NativeManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = MutableLiveData()
            NativeManager.loadedLiveDataMap[placementId] = liveData!!
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

    //配置广告宽高
    private val localMap: MutableMap<String, Any> by lazy { mutableMapOf() }

    private val flContainer by lazy {
        FrameLayout(owner.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
        }
    }

    private val flAdView by lazy { FrameLayout(owner.context) }

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        localMap.apply {
            put(ATAdConst.KEY.AD_WIDTH, nativeWidth)
            put(ATAdConst.KEY.AD_HEIGHT, nativeHeight)
        }
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
    fun show(isManualShow: Boolean = true): NativeSplashLoader {
        if (isManualShow) {
            isRequestAdCallback = true
        }
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }

        isShowAfterLoaded = false
        onAdRenderSuc()
        return this
    }

    /**
     * 广告请求
     */
    private fun onAdRequest() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRequest")

        NativeSplashCallback().apply(nativeSplashCallback).onAdRequest?.invoke()
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed || isAdRendered) return
        Log.e(logTag, "onAdRenderSuc")

        clearView()
        isAdRendered = true
        flAdView.addView(flContainer)
        NativeSplashCallback().apply(nativeSplashCallback).onAdRenderSuc?.invoke(flAdView)
    }

    /**
     * 广告请求加载
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting = NativeManager.isRequesting(placementId) || isAdPlaying || isDestroyed
        if (!isRequesting && isRequestAdCallback) {
            isRequestAdCallback = false
            onAdRequest()
        }

        if (!isRequesting && !isAdLoaded) {
            NativeManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atNativeSplash = ATNativeSplash(
                    owner.context,
                    flContainer,
                    null,
                    placementId,
                    localMap,
                    nativeSplashConfig.requestTimeOut,
                    nativeSplashConfig.fetchDelay,
                    this
                )
            }
            return true
        }
        return isRequesting
    }

    private fun clearView() {
        isAdRendered = false
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
        if (isDestroyed) return
        Log.e(logTag, "onAdLoaded")

        isAdLoaded = true
        NativeManager.updateRequestStatus(placementId, false)
        NativeSplashCallback().apply(nativeSplashCallback).onAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show(false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onNoAdError(errorMsg: String?) {
        if (isDestroyed) return
        Log.e(logTag, "onNoAdError:$errorMsg")

        NativeManager.updateRequestStatus(placementId, false)
        NativeSplashCallback().apply(nativeSplashCallback).onAdError?.invoke(errorMsg)
    }

    /**
     * Skip按钮点击回调
     */
    override fun onAdSkip() {
        if (isDestroyed) return
        Log.e(logTag, "onAdSkip")

        isAdLoaded = false
        isAdPlaying = false
        if (NativeSplashCallback().apply(nativeSplashCallback).onAdSkip?.invoke() == true) {
            clearView()
        }
    }

    /**
     * 广告展示回调
     */
    override fun onAdShow(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdShow:${info.toString()}")

        isAdPlaying = true
        NativeSplashCallback().apply(nativeSplashCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告点击回调
     */
    override fun onAdClick(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClick:${info.toString()}")

        NativeSplashCallback().apply(nativeSplashCallback).onAdClick?.invoke(info)
    }

    /**
     * 广告的倒计时回调，用于倒计时秒数的刷新，返回单位：毫秒
     */
    override fun onAdTick(tickTime: Long) {
        if (isDestroyed) return
        Log.e(logTag, "onAdTick:$tickTime")

        NativeSplashCallback().apply(nativeSplashCallback).onAdTick?.invoke(tickTime)
    }

    /**
     * 广告的倒计时结束，可在这里关闭NativeSplash广告的Activity
     */
    override fun onAdTimeOver() {
        if (isDestroyed) return
        Log.e(logTag, "onAdTimeOver")

        isAdPlaying = false
        NativeSplashCallback().apply(nativeSplashCallback).onAdTimeOver?.invoke()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        loadedLiveData.removeObserver(observer)
        clearView()
        NativeManager.release(placementId)
        atNativeSplash = null
    }
}