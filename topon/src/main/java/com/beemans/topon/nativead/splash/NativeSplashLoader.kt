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
) : ATNativeSplashListener, LifecycleObserver {

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

    private val frameLayout by lazy {
        FrameLayout(activity).apply {
            val layoutParams = ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
            this.layoutParams = layoutParams
        }
    }

    //广告正在进行请求
    private var isRequesting = false

    //广告位ID
    private val placementId by lazy { splashConfig.placementId }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

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
    private val localMap: MutableMap<String, Any> = mutableMapOf()

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

    fun show() {
        isShowAfterLoaded = true
        isRequesting = NativeManager.isRequesting(placementId)
        if (!isRequesting) {
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
        }
    }

    /**
     * 广告加载成功回调
     */
    override fun onAdLoaded() {
        Log.e(logTag, "onAdLoaded")
        NativeManager.updateRequestStatus(placementId, loaderTag, false)
        NativeSplashCallback().apply(splashCallback).onAdLoaded?.invoke(frameLayout)
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onNoAdError(errorMsg: String?) {
        Log.e(logTag, "onNoAdError:$errorMsg")
        isShowAfterLoaded = true
        NativeManager.updateRequestStatus(placementId, loaderTag, false)
        NativeSplashCallback().apply(splashCallback).onNoAdError?.invoke(errorMsg)
    }

    /**
     * Skip按钮点击回调
     */
    override fun onAdSkip() {
        Log.e(logTag, "onAdSkip")
        NativeSplashCallback().apply(splashCallback).onAdSkip?.invoke()
    }

    /**
     * 广告展示回调
     */
    override fun onAdShow(info: ATAdInfo?) {
        Log.e(logTag, "onAdShow")
    }

    /**
     * 广告点击回调
     */
    override fun onAdClick(info: ATAdInfo?) {
        Log.e(logTag, "onAdClick")
        NativeSplashCallback().apply(splashCallback).onAdClick?.invoke()
    }

    /**
     * 广告的倒计时回调，用于倒计时秒数的刷新，返回单位：毫秒
     */
    override fun onAdTick(tickTime: Long) {
        Log.e(logTag, "onAdTick")
        NativeSplashCallback().apply(splashCallback).onAdTick?.invoke(tickTime)
    }

    /**
     * 广告的倒计时结束，可在这里关闭NativeSplash广告的Activity
     */
    override fun onAdTimeOver() {
        Log.e(logTag, "onAdTimeOver")
        NativeSplashCallback().apply(splashCallback).onAdTimeOver?.invoke()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        (frameLayout.parent as? ViewGroup)?.removeView(frameLayout)
        NativeManager.release(placementId)
    }
}