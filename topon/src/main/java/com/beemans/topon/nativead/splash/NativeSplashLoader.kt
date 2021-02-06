package com.beemans.topon.nativead.splash

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.contains
import androidx.lifecycle.*
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.splash.api.ATNativeSplash
import com.anythink.nativead.splash.api.ATNativeSplashListener
import com.anythink.network.gdt.GDTATConst
import com.anythink.network.toutiao.TTATConst
import com.beemans.topon.ext.context
import com.beemans.topon.nativead.NativeManager
import com.qq.e.ads.nativ.ADSize
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

    private val logTag by lazy { this.javaClass.simpleName }

    private val nativeWidth by lazy { nativeSplashConfig.nativeWidth }
    private val nativeHeight by lazy { nativeSplashConfig.nativeHeight }

    //广告位ID
    private val placementId by lazy { nativeSplashConfig.placementId }

    //高度自适应
    private val isHighlyAdaptive by lazy { nativeSplashConfig.isHighlyAdaptive }

    //请求超时时间
    private val requestTimeOut by lazy { nativeSplashConfig.requestTimeOut }

    //广告展示的倒计时总时长
    private val fetchDelay by lazy { nativeSplashConfig.fetchDelay }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告加载完成
    private var isAdLoaded = false

    //加载广告成功是否进行渲染
    private var isRenderAd = false

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
                showAd(isManualShow = false)
            }
        }
    }

    //配置广告宽高
    private val localExtra by lazy {
        mutableMapOf<String, Any>().apply {
            put(ATAdConst.KEY.AD_WIDTH, nativeWidth)
            put(ATAdConst.KEY.AD_HEIGHT, nativeHeight)

            if (isHighlyAdaptive) {
                //穿山甲
                put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, 0)
                //广点通
                put(GDTATConst.AD_HEIGHT, ADSize.AUTO_HEIGHT)
            }
        }
    }

    private val flContainer by lazy {
        FrameLayout(owner.context).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
        }
    }

    private val flAdView by lazy { FrameLayout(owner.context) }

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
    fun show(): NativeSplashLoader {
        return showAd()
    }

    /**
     * 广告加载显示
     *
     * @param isManualShow 是否手动调用进行展示
     */
    private fun showAd(isManualShow: Boolean = true): NativeSplashLoader {
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
     * 广告请求加载
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting = NativeManager.isRequesting(placementId) || isDestroyed
        if (!isRequesting && !isAdLoaded) {
            if (isRequestAdCallback) {
                isRequestAdCallback = false
                onAdRequest()
            }
            NativeManager.updateRequestStatus(placementId, true)

            try {
                post(Schedulers.io()) {
                    ATNativeSplash(
                        owner.context,
                        flContainer,
                        null,
                        placementId,
                        localExtra,
                        requestTimeOut,
                        fetchDelay,
                        this
                    )
                }
            } catch (e: Exception) {
            }
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

        NativeSplashCallback().apply(nativeSplashCallback).onAdRequest?.invoke()
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed || !isRenderAd) return
        Log.e(logTag, "onAdRenderSuc")

        isRenderAd = false
        if (!flAdView.contains(flContainer)) {
            flAdView.addView(flContainer)
        }
        val parent = flAdView.parent
        if (parent is ViewGroup && parent.contains(flAdView)) {
            parent.removeView(flAdView)
        }
        NativeSplashCallback().apply(nativeSplashCallback).onAdRenderSuc?.invoke(flAdView)
    }

    /**
     * 广告加载成功回调
     */
    override fun onAdLoaded() {
        if (isDestroyed) return
        Log.e(logTag, "onAdLoadSuc")

        isAdLoaded = true
        isRenderAd = true
        NativeManager.updateRequestStatus(placementId, false)
        NativeSplashCallback().apply(nativeSplashCallback).onAdLoadSuc?.invoke()

        if (isShowAfterLoaded) {
            showAd(isManualShow = false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onNoAdError(errorMsg: String?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdLoadFail:$errorMsg")

        NativeManager.updateRequestStatus(placementId, false)
        NativeSplashCallback().apply(nativeSplashCallback).onAdLoadFail?.invoke(errorMsg)
    }

    /**
     * Skip按钮点击回调
     */
    override fun onAdSkip() {
        if (isDestroyed) return
        Log.e(logTag, "onAdSkip")

        isAdLoaded = false
        if (NativeSplashCallback().apply(nativeSplashCallback).onAdSkip?.invoke() == true) {
            clearView()
        }
    }

    /**
     * 广告展示回调
     */
    override fun onAdShow(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdShow:${info?.toString()}")

        NativeSplashCallback().apply(nativeSplashCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告点击回调
     */
    override fun onAdClick(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClick:${info?.toString()}")

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

        NativeSplashCallback().apply(nativeSplashCallback).onAdTimeOver?.invoke()
    }

    private fun clearView() {
        if (flAdView.contains(flContainer)) {
            flAdView.removeView(flContainer)
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
        clearView()
        NativeManager.release(placementId)
    }
}