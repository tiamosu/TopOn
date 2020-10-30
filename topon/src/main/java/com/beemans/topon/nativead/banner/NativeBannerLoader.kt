package com.beemans.topon.nativead.banner

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.banner.api.ATNativeBannerListener
import com.anythink.nativead.banner.api.ATNativeBannerView
import com.beemans.topon.ext.context
import com.beemans.topon.nativead.NativeManager
import com.tiamosu.fly.callback.EventLiveData
import com.tiamosu.fly.utils.post
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerLoader(
    private val owner: LifecycleOwner,
    private val bannerConfig: NativeBannerConfig,
    private val bannerCallback: NativeBannerCallback.() -> Unit
) : LifecycleObserver, ATNativeBannerListener {

    private lateinit var atNativeBannerView: ATNativeBannerView

    private val logTag by lazy { this.javaClass.simpleName }

    private val nativeWidth by lazy { bannerConfig.nativeWidth }
    private val nativeHeight by lazy { bannerConfig.nativeHeight }

    //广告位ID
    private val placementId by lazy { bannerConfig.placementId }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告加载完成
    private var isAdLoaded = false

    //广告是否已经渲染
    private var isAdRendered = false

    //页面是否已经销毁了
    private var isDestroyed = false

    //是否手动调用广告展示[show]
    private var isManualShow = false

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
        atNativeBannerView = ATNativeBannerView(owner.context).apply {
            //配置广告宽高
            val localMap: MutableMap<String, Any> = mutableMapOf()
            localMap.apply {
                put(ATAdConst.KEY.AD_WIDTH, nativeWidth)
                put(ATAdConst.KEY.AD_HEIGHT, nativeHeight)
            }.let(this::setLocalExtra)

            setBannerConfig(bannerConfig.atBannerConfig)
            setUnitId(placementId)
            setAdListener(this@NativeBannerLoader)
        }
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)

        loadedLiveData.observe(owner) {
            if (isShowAfterLoaded) {
                show(false)
            }
        }
    }

    /**
     * 广告加载显示
     *
     * @param isManualShow 是否手动调用进行展示
     */
    fun show(isManualShow: Boolean = true): NativeBannerLoader {
        this.isManualShow = isManualShow
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
        if (!isRequesting && isManualShow) {
            isManualShow = false
            onAdRequest()
        }

        if (!isRequesting && !isAdLoaded) {
            NativeManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atNativeBannerView.loadAd(null)
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

        NativeBannerCallback().apply(bannerCallback).onAdRequest?.invoke()
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed || isAdRendered) return
        Log.e(logTag, "onAdRenderSuc")

        clearView()
        isAdRendered = true
        flAdView.addView(atNativeBannerView, layoutParams)
        NativeBannerCallback().apply(bannerCallback).onAdRenderSuc?.invoke(flAdView)
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
        NativeBannerCallback().apply(bannerCallback).onAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show(false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onAdError(errorMsg: String?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdError:$errorMsg")

        NativeManager.updateRequestStatus(placementId, false)
        NativeBannerCallback().apply(bannerCallback).onAdError?.invoke(errorMsg)
    }

    /**
     * 广告展示回调
     */
    override fun onAdShow(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdShow:${info.toString()}")

        NativeBannerCallback().apply(bannerCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告刷新回调
     */
    override fun onAutoRefresh(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAutoRefresh:${info.toString()}")

        NativeBannerCallback().apply(bannerCallback).onAdAutoRefresh?.invoke(info)
    }

    /**
     * 广告刷新失败回调
     */
    override fun onAutoRefreshFail(errorMsg: String?) {
        if (isDestroyed) return
        Log.e(logTag, "onAutoRefreshFail:$errorMsg")

        NativeBannerCallback().apply(bannerCallback).onAdAutoRefreshFail?.invoke(errorMsg)
    }

    /**
     * 广告点击
     */
    override fun onAdClick(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClick:${info.toString()}")

        NativeBannerCallback().apply(bannerCallback).onAdClick?.invoke(info)
    }

    /**
     * 广告关闭回调（部分广告平台有该回调），可在此处执行移除view的操作
     */
    override fun onAdClose() {
        if (isDestroyed) return
        Log.e(logTag, "onAdClose")

        if (NativeBannerCallback().apply(bannerCallback).onAdClose?.invoke() == true) {
            isAdLoaded = false
            clearView()
        }
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        clearView()
        NativeManager.release(placementId)
    }
}