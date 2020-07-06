package com.beemans.topon.nativead

import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.nativead.api.*
import com.anythink.network.mintegral.MintegralATConst
import com.anythink.network.toutiao.TTATConst
import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Suppress("unused", "UNUSED_PARAMETER")
class NativeAdLoader(
    private val owner: LifecycleOwner,
    private val nativeAdConfig: NativeAdConfig,
    private val nativeAdRender: BaseNativeAdRender = DefaultNativeAdRender(),
    private val nativeAdCallback: NativeAdCallback.() -> Unit,
) : LifecycleObserver,
    ATNativeNetworkListener,
    ATNativeEventListener,
    ATNativeDislikeListener() {

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
    private var atNative: ATNative? = null
    private var nativeAd: NativeAd? = null

    //用于实现广告渲染
    private val atNativeAdView by lazy { ATNativeAdView(activity) }

    //广告位ID
    private val placementId by lazy { nativeAdConfig.placementId }

    private val nativeWidth by lazy { nativeAdConfig.nativeWidth }
    private val nativeHeight by lazy { nativeAdConfig.nativeHeight }

    //是否进行广告预加载
    private val isUsePreload by lazy { nativeAdConfig.isUsePreload }

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData: EventLiveData<Boolean> by lazy {
        var liveData = NativeManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = EventLiveData()
            NativeManager.loadedLiveDataMap[placementId] = liveData
        }
        liveData
    }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告请求中
    private var isRequesting = false

    private var isDestroyed = false

    private val layoutParams by lazy {
        ViewGroup.LayoutParams(nativeWidth, nativeHeight)
    }

    init {
        owner.lifecycle.addObserver(this)
        initNative()
        createObserve()
    }

    private fun initNative() {
        if (atNative == null) {
            atNative = ATNative(activity, placementId, this).apply {
                //配置广告宽高
                val localMap: MutableMap<String, Any> = mutableMapOf()
                localMap.apply {
                    put(TTATConst.NATIVE_AD_IMAGE_WIDTH, nativeWidth)
                    put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, nativeHeight)
                    put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, nativeWidth)
                    put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, nativeHeight)
                }.let(this::setLocalExtra)
            }
        }

        preLoadNative()
    }

    private fun createObserve() {
        loadedLiveData.observe(owner) {
            if (isShowAfterLoaded && !isDestroyed) {
                show()
            }
        }
    }

    /**
     * 广告请求
     */
    fun request(): NativeAdLoader {
        makeAdRequest()
        return this
    }

    /**
     * 广告加载显示
     */
    fun show(): NativeAdLoader {
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }
        isShowAfterLoaded = false
        nativeAd?.apply {
            setNativeEventListener(this@NativeAdLoader)
            setDislikeCallbackListener(this@NativeAdLoader)
            renderAdView(atNativeAdView, nativeAdRender)

            if (nativeAdRender.clickView.isNotEmpty()) {
                prepare(atNativeAdView, nativeAdRender.clickView, null)
            } else {
                prepare(atNativeAdView)
            }
            nativeRenderSuc()
        }
        return this
    }

    /**
     * 广告预加载
     */
    private fun preLoadNative() {
        if (isUsePreload) {
            makeAdRequest()
        }
    }

    /**
     * 广告渲染成功
     */
    private fun nativeRenderSuc() {
        NativeAdCallback().apply(nativeAdCallback).onNativeRenderSuc?.invoke(
            atNativeAdView,
            layoutParams
        )
    }

    /**
     * 在Activity的onResume时调用（主要针对部分广告平台的视频广告）
     */
    private fun onResume() {
        nativeAd?.onResume()
    }

    /**
     * 在Activity的onPause时调用（主要针对部分广告平台的视频广告）
     */
    private fun onPause() {
        nativeAd?.onPause()
    }

    /**
     * 移除广告对view的绑定
     * 销毁当前的广告对象（执行之后该广告无法再进行展示）
     */
    private fun onDestroy() {
        isDestroyed = true
        (atNativeAdView.parent as? ViewGroup)?.removeView(atNativeAdView)
        NativeManager.release(placementId)
        nativeAd?.destory()
    }

    /**
     * 发起Native广告请求
     */
    private fun makeAdRequest(): Boolean {
        isRequesting = NativeManager.isRequesting(placementId)
        if (!isRequesting && getNativeAd().also { nativeAd = it } == null && !isDestroyed) {
            NativeManager.updateRequestStatus(placementId, loaderTag, true)
            atNative?.makeAdRequest()
        }
        return isRequesting
    }

    /**
     * 获取广告对象
     */
    private fun getNativeAd(): NativeAd? {
        return atNative?.nativeAd
    }

    /**
     * 广告加载失败，可通过AdError.printStackTrace()获取全部错误信息
     */
    override fun onNativeAdLoadFail(error: AdError?) {
        if (isDestroyed) return
        Log.e(logTag, "onNativeAdLoadFail:${error?.printStackTrace()}")
        isShowAfterLoaded = true
        NativeManager.updateRequestStatus(placementId, loaderTag, false)
        NativeAdCallback().apply(nativeAdCallback).onNativeAdLoadFail?.invoke(error)
    }

    /**
     * 广告加载成功
     */
    override fun onNativeAdLoaded() {
        if (isDestroyed) return
        Log.e(logTag, "onNativeAdLoaded")
        NativeManager.updateRequestStatus(placementId, loaderTag, false)
        NativeAdCallback().apply(nativeAdCallback).onNativeAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告视频播放开始（仅部分广告平台存在）
     */
    override fun onAdVideoStart(view: ATNativeAdView?) {
        Log.e(logTag, "onAdVideoStart")
    }

    /**
     * 广告视频播放进度（仅部分广告平台存在）
     */
    override fun onAdVideoProgress(view: ATNativeAdView?, progress: Int) {
        Log.e(logTag, "onAdVideoProgress")
    }

    /**
     * 广告点击回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息
     */
    override fun onAdClicked(view: ATNativeAdView?, info: ATAdInfo?) {
        Log.e(logTag, "onAdClicked")
        NativeAdCallback().apply(nativeAdCallback).onNativeClicked?.invoke()
    }

    /**
     * 广告视频播放结束（仅部分广告平台存在）
     */
    override fun onAdVideoEnd(view: ATNativeAdView?) {
        Log.e(logTag, "onAdVideoEnd")
    }

    /**
     * 广告展示回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息
     */
    override fun onAdImpressed(view: ATNativeAdView?, info: ATAdInfo?) {
        Log.e(logTag, "onAdImpressed")
        preLoadNative()
    }

    /**
     * 广告Dislike监听回调
     */
    override fun onAdCloseButtonClick(view: ATNativeAdView?, info: ATAdInfo?) {
        Log.e(logTag, "onAdCloseButtonClick")
        if (NativeAdCallback().apply(nativeAdCallback).onNativeCloseClicked?.invoke() == true) {
            (view?.parent as? ViewGroup)?.removeView(view)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(owner: LifecycleOwner) {
        onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(owner: LifecycleOwner) {
        onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        onDestroy()
    }
}