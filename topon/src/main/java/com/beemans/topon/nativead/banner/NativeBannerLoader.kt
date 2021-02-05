package com.beemans.topon.nativead.banner

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.contains
import androidx.lifecycle.*
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.banner.api.ATNativeBannerListener
import com.anythink.nativead.banner.api.ATNativeBannerView
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
class NativeBannerLoader(
    private val owner: LifecycleOwner,
    private val bannerConfig: NativeBannerConfig,
    private val bannerCallback: NativeBannerCallback.() -> Unit
) : LifecycleObserver, ATNativeBannerListener {

    private var atNativeBannerView: ATNativeBannerView? = null

    private val logTag by lazy { this.javaClass.simpleName }

    private val nativeWidth by lazy { bannerConfig.nativeWidth }
    private val nativeHeight by lazy { bannerConfig.nativeHeight }

    //广告位ID
    private val placementId by lazy { bannerConfig.placementId }

    //高度自适应
    private val isHighlyAdaptive by lazy { bannerConfig.isHighlyAdaptive }

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

    private val flAdView by lazy { FrameLayout(owner.context) }

    private val layoutParams by lazy { ViewGroup.LayoutParams(nativeWidth, nativeHeight) }

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        atNativeBannerView = ATNativeBannerView(owner.context).apply {
            layoutParams = this@NativeBannerLoader.layoutParams

            //配置广告宽高
            val localMap: MutableMap<String, Any> = mutableMapOf()
            localMap.apply {
                put(ATAdConst.KEY.AD_WIDTH, nativeWidth)
                put(ATAdConst.KEY.AD_HEIGHT, nativeHeight)

                if (isHighlyAdaptive) {
                    //穿山甲
                    put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, 0)
                    //广点通
                    put(GDTATConst.AD_HEIGHT, ADSize.AUTO_HEIGHT)
                }
            }.let(this::setLocalExtra)

            setBannerConfig(bannerConfig.atBannerConfig)
            setUnitId(placementId)
            setAdListener(this@NativeBannerLoader)
        }
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)
        loadedLiveData.observe(owner, observer)
    }

    /**
     * 广告加载显示
     *
     * @param isReload 是否重新加载广告，默认为false
     */
    fun show(isReload: Boolean = false): NativeBannerLoader {
        return showAd(isReload)
    }

    /**
     * 广告加载显示
     *
     * @param isReload 是否重新加载广告，默认为false
     * @param isManualShow 是否手动调用进行展示
     */
    private fun showAd(
        isReload: Boolean = false,
        isManualShow: Boolean = true
    ): NativeBannerLoader {
        if (isReload) {
            isAdLoaded = false
        }
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

            post(Schedulers.io()) {
                atNativeBannerView?.loadAd(null)
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
        if (isDestroyed || !isRenderAd) return
        Log.e(logTag, "onAdRenderSuc")

        isRenderAd = false
        if (atNativeBannerView != null && !flAdView.contains(atNativeBannerView!!)) {
            flAdView.addView(atNativeBannerView)
        }
        val parent = flAdView.parent
        if (parent is ViewGroup && parent.contains(flAdView)) {
            parent.removeView(flAdView)
        }
        NativeBannerCallback().apply(bannerCallback).onAdRenderSuc?.invoke(flAdView)
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
        NativeBannerCallback().apply(bannerCallback).onAdLoadSuc?.invoke()

        if (isShowAfterLoaded) {
            showAd(isManualShow = false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onAdError(errorMsg: String?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdLoadFail:$errorMsg")

        NativeManager.updateRequestStatus(placementId, false)
        NativeBannerCallback().apply(bannerCallback).onAdLoadFail?.invoke(errorMsg)
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
        Log.e(logTag, "onAdAutoRefresh:${info.toString()}")

        NativeBannerCallback().apply(bannerCallback).onAdAutoRefresh?.invoke(info)
    }

    /**
     * 广告刷新失败回调
     */
    override fun onAutoRefreshFail(errorMsg: String?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdAutoRefreshFail:$errorMsg")

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
            clearView()
        }
    }

    private fun clearView() {
        isAdLoaded = false
        if (atNativeBannerView != null && flAdView.contains(atNativeBannerView!!)) {
            flAdView.removeView(atNativeBannerView)
        }
        val parent = flAdView.parent
        if (parent is ViewGroup && parent.contains(flAdView)) {
            parent.removeView(flAdView)
        }
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
        atNativeBannerView?.setAdListener(null)
        atNativeBannerView = null
    }
}