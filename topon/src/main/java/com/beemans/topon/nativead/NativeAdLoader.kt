package com.beemans.topon.nativead

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.nativead.api.*
import com.anythink.network.gdt.GDTATConst
import com.anythink.network.toutiao.TTATConst
import com.beemans.topon.ext.context
import com.qq.e.ads.nativ.ADSize
import com.tiamosu.fly.callback.EventLiveData
import com.tiamosu.fly.utils.post
import io.reactivex.rxjava3.schedulers.Schedulers

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

    private var atNative: ATNative? = null
    private var nativeAd: NativeAd? = null

    //用于实现广告渲染
    private var atNativeAdView: ATNativeAdView? = null

    private val logTag by lazy { this.javaClass.simpleName }

    //广告位ID
    private val placementId by lazy { nativeAdConfig.placementId }

    private val nativeWidth by lazy { nativeAdConfig.nativeWidth }
    private val nativeHeight by lazy { nativeAdConfig.nativeHeight }

    //是否进行广告预加载
    private val isUsePreload by lazy { nativeAdConfig.isUsePreload }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告正在播放
    private var isAdPlaying = false

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

    private val flAdView by lazy { FrameLayout(owner.context) }

    private val layoutParams by lazy { ViewGroup.LayoutParams(nativeWidth, nativeHeight) }

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        if (atNative == null) {
            atNative = ATNative(owner.context, placementId, this).apply {
                //配置广告宽高
                val localMap: MutableMap<String, Any> = mutableMapOf()
                localMap.apply {
                    put(ATAdConst.KEY.AD_WIDTH, nativeWidth)
                    put(ATAdConst.KEY.AD_HEIGHT, nativeHeight)

                    //对穿山甲和广点通进行广告自适应高度
                    if (nativeHeight == 0) {
                        //穿山甲
                        put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, 0)
                        //广点通
                        put(GDTATConst.AD_HEIGHT, ADSize.AUTO_HEIGHT)
                    }
                }.let(this::setLocalExtra)
            }
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

            if (atNativeAdView == null) {
                atNativeAdView = ATNativeAdView(owner.context)
            }
            renderAdView(atNativeAdView, nativeAdRender)
            if (nativeAdRender.clickView.isNotEmpty()) {
                prepare(atNativeAdView, nativeAdRender.clickView, null)
            } else {
                prepare(atNativeAdView)
            }
            onAdRenderSuc()
        }
        return this
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc")

        clearView()
        flAdView.addView(atNativeAdView, layoutParams)
        NativeAdCallback().apply(nativeAdCallback).onAdRenderSuc?.invoke(flAdView)
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
     * 发起Native广告请求
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting = NativeManager.isRequesting(placementId) || isAdPlaying || isDestroyed
        if (!isRequesting && getNativeAd().also { nativeAd = it } == null) {
            NativeManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atNative?.makeAdRequest()
            }
            return true
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
     * 广告加载失败
     */
    override fun onNativeAdLoadFail(error: AdError?) {
        if (isDestroyed) return
        Log.e(logTag, "onNativeAdLoadFail:${error?.printStackTrace()}")

        isShowAfterLoaded = true
        NativeManager.updateRequestStatus(placementId, false)
        NativeAdCallback().apply(nativeAdCallback).onAdLoadFail?.invoke(error)
    }

    /**
     * 广告加载成功
     */
    override fun onNativeAdLoaded() {
        if (isDestroyed) return
        Log.e(logTag, "onNativeAdLoaded")

        NativeManager.updateRequestStatus(placementId, false)
        NativeAdCallback().apply(nativeAdCallback).onAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告视频播放开始（仅部分广告平台存在）
     */
    override fun onAdVideoStart(view: ATNativeAdView?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoStart")

        isAdPlaying = true
        NativeAdCallback().apply(nativeAdCallback).onAdVideoStart?.invoke(view)
    }

    /**
     * 广告视频播放结束（仅部分广告平台存在）
     */
    override fun onAdVideoEnd(view: ATNativeAdView?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoEnd")

        isAdPlaying = false
        NativeAdCallback().apply(nativeAdCallback).onAdVideoEnd?.invoke(view)
    }

    /**
     * 广告视频播放进度（仅部分广告平台存在）
     */
    override fun onAdVideoProgress(view: ATNativeAdView?, progress: Int) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoProgress")

        NativeAdCallback().apply(nativeAdCallback).onAdVideoProgress?.invoke(view, progress)
    }

    /**
     * 广告展示回调
     */
    override fun onAdImpressed(view: ATNativeAdView?, info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdImpressed:${info.toString()}")

        NativeAdCallback().apply(nativeAdCallback).onAdImpressed?.invoke(view, info)
        preLoadAd()
    }

    /**
     * 广告点击回调
     */
    override fun onAdClicked(view: ATNativeAdView?, info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClicked:${info.toString()}")

        NativeAdCallback().apply(nativeAdCallback).onAdClicked?.invoke(view, info)
    }

    /**
     * 广告Dislike监听回调
     */
    override fun onAdCloseButtonClick(view: ATNativeAdView?, info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdCloseButtonClick:${info.toString()}")

        if (NativeAdCallback().apply(nativeAdCallback).onAdCloseClick?.invoke(view, info) == true) {
            isAdPlaying = false
            clearView()
            nativeAd?.setDislikeCallbackListener(null)
        }
    }

    /**
     * 在Activity的onResume时调用（主要针对部分广告平台的视频广告）
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume(owner: LifecycleOwner) {
        Log.e(logTag, "onResume")
        nativeAd?.onResume()
    }

    /**
     * 在Activity的onPause时调用（主要针对部分广告平台的视频广告）
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause(owner: LifecycleOwner) {
        Log.e(logTag, "onPause")
        nativeAd?.onPause()
    }

    /**
     * 移除广告对view的绑定，销毁当前的广告对象（执行之后该广告无法再进行展示）
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        clearView()
        NativeManager.release(placementId)
        nativeAd?.destory()
        nativeAd?.setDislikeCallbackListener(null)
        atNative = null
        nativeAd = null
        atNativeAdView = null
    }
}