package com.beemans.topon.nativead

import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.nativead.api.*
import com.anythink.nativead.unitgroup.api.CustomNativeAd
import com.anythink.network.mintegral.MintegralATConst
import com.anythink.network.toutiao.TTATConst
import com.beemans.topon.bean.AdWrapper
import com.beemans.topon.bean.NativeStrategy

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeLoader(private val activity: FragmentActivity, private val placementId: String) :
    ATNativeNetworkListener,
    ATNativeEventListener,
    ATNativeDislikeListener() {

    private var atNative: ATNative? = null
    private var nativeAd: NativeAd? = null

    //用于实现广告渲染
    private val nativeAdRender by lazy { NativeAdRender() }
    private val atNativeAdView by lazy { ATNativeAdView(activity) }

    //广告回调
    private var nativeCallback: (NativeCallback.() -> Unit)? = null

    private var nativeWidth = 0
    private var nativeHeight = 0

    //是否进行广告预加载
    private var isUsePreload = false

    //是否正在进行广告预加载
    private var isPreloading = false

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    fun init(
        nativeStrategy: NativeStrategy,
        nativeAdRender: () -> ATNativeAdRenderer<CustomNativeAd>? = { null },
        nativeCallback: NativeCallback.() -> Unit = {}
    ): NativeLoader {
        this.nativeCallback = nativeCallback
        this.isUsePreload = nativeStrategy.isUsePreload
        this.nativeWidth = nativeStrategy.nativeWidth
        this.nativeHeight = nativeStrategy.nativeHeight

        initNative()
        NativeManager.eventLiveData.observe(activity) {
            if (isShowAfterLoaded) {
                isPreloading = false
                show()
            }
        }
        return this
    }

    private fun initNative() {
        val key = AdWrapper(activity.javaClass.simpleName, placementId).toString()
        if (NativeManager.atNatives[key].also { atNative = it } == null) {
            atNative = ATNative(activity, placementId, this).apply {
                //配置广告宽高
                val localMap: MutableMap<String, Any> = mutableMapOf()
                localMap.apply {
                    put(TTATConst.NATIVE_AD_IMAGE_WIDTH, nativeWidth)
                    put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, nativeHeight)
                    put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, nativeWidth)
                    put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, nativeHeight)
                }.let(this::setLocalExtra)
            }.also { NativeManager.atNatives[key] = it }
        }

        preLoadNative()
    }

    /**
     * 广告请求
     */
    fun request(): NativeLoader {
        if (getNativeAd() == null) {
            makeAdRequest()
        }
        return this
    }

    /**
     * 广告加载显示
     */
    fun show(): NativeLoader {
        isShowAfterLoaded = true
        if (isPreloading) {
            return this
        }
        if (getNativeAd().also { nativeAd = it } == null) {
            makeAdRequest()
            return this
        }

        isShowAfterLoaded = false
        nativeAd?.apply {
            setNativeEventListener(this@NativeLoader)
            setDislikeCallbackListener(this@NativeLoader)
            renderAdView(atNativeAdView, nativeAdRender)
            prepare(atNativeAdView)
            nativeRenderSuc()
        }
        return this
    }

    /**
     * 广告预加载
     */
    private fun preLoadNative() {
        if (isUsePreload && getNativeAd() == null) {
            isPreloading = true
            makeAdRequest()
        }
    }

    /**
     * 广告渲染成功
     */
    private fun nativeRenderSuc() {
        nativeCallback?.let {
            val layoutParams = ViewGroup.LayoutParams(nativeWidth, nativeHeight)
            NativeCallback().apply(it).onNativeRenderSuc?.invoke(atNativeAdView, layoutParams)
        }
    }

    /**
     * 在Activity的onResume时调用（主要针对部分广告平台的视频广告）
     */
    fun onResume() {
        nativeAd?.onResume()
    }

    /**
     * 在Activity的onPause时调用（主要针对部分广告平台的视频广告）
     */
    fun onPause() {
        nativeAd?.onPause()
    }

    /**
     * 移除广告对view的绑定
     * 销毁当前的广告对象（执行之后该广告无法再进行展示）
     */
    fun onDestroy() {
        nativeAd?.clear(atNativeAdView)
        nativeAd?.destory()
    }

    /**
     * 发起Native广告请求
     */
    private fun makeAdRequest() {
        atNative?.makeAdRequest()
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
        isPreloading = false
        Log.e("xia", "onNativeAdLoadFail:${error?.printStackTrace()}   isPreloading:$isPreloading")
        nativeCallback?.let { NativeCallback().apply(it).onNativeAdLoadFail?.invoke(error) }
    }

    /**
     * 广告加载成功
     */
    override fun onNativeAdLoaded() {
        Log.e("xia", "onNativeAdLoaded:$isPreloading")
        isPreloading = false
        nativeCallback?.let { NativeCallback().apply(it).onNativeAdLoaded?.invoke() }

        if (isShowAfterLoaded) {
            show()
        }
        NativeManager.eventLiveData.value = true
    }

    /**
     * 广告视频播放开始（仅部分广告平台存在）
     */
    override fun onAdVideoStart(view: ATNativeAdView?) {
        Log.e("xia", "onAdVideoStart")
    }

    /**
     * 广告视频播放进度（仅部分广告平台存在）
     */
    override fun onAdVideoProgress(view: ATNativeAdView?, progress: Int) {
        Log.e("xia", "onAdVideoProgress")
    }

    /**
     * 广告点击回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息
     */
    override fun onAdClicked(view: ATNativeAdView?, info: ATAdInfo?) {
        Log.e("xia", "onAdClicked")
        nativeCallback?.let { NativeCallback().apply(it).onNativeClicked?.invoke() }
    }

    /**
     * 广告视频播放结束（仅部分广告平台存在）
     */
    override fun onAdVideoEnd(view: ATNativeAdView?) {
        Log.e("xia", "onAdVideoEnd")
    }

    /**
     * 广告展示回调，其中ATAdInfo是广告的信息对象，主要包含是第三方聚合平台的id信息
     */
    override fun onAdImpressed(view: ATNativeAdView?, info: ATAdInfo?) {
        Log.e("xia", "onAdImpressed")
        preLoadNative()
    }

    override fun onAdCloseButtonClick(view: ATNativeAdView?, info: ATAdInfo?) {
        Log.e("xia", "onAdCloseButtonClick")
        (view?.parent as? ViewGroup)?.removeView(view)
        nativeCallback?.let { NativeCallback().apply(it).onNativeCloseClicked?.invoke() }
    }
}