package com.beemans.topon.banner

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.anythink.banner.api.ATBannerListener
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.beemans.topon.ext.context
import com.tiamosu.fly.callback.EventLiveData
import com.tiamosu.fly.utils.post
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerLoader(
    private val owner: LifecycleOwner,
    private val bannerConfig: BannerConfig,
    private val flSplashView: FrameLayout,
    private val bannerCallback: BannerCallback.() -> Unit
) : LifecycleObserver, ATBannerListener {

    private lateinit var atBannerView: ATBannerView

    private val logTag by lazy { this.javaClass.simpleName }

    private val nativeWidth by lazy { bannerConfig.nativeWidth }
    private val nativeHeight by lazy { bannerConfig.nativeHeight }

    //是否进行广告预加载
    private val isUsePreload by lazy { bannerConfig.isUsePreload }

    //广告位ID
    private val placementId by lazy { bannerConfig.placementId }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告加载成功
    private var isBannerLoaded = false

    private var isDestroyed = false

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData: EventLiveData<Boolean> by lazy {
        var liveData = BannerManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = EventLiveData()
            BannerManager.loadedLiveDataMap[placementId] = liveData
        }
        liveData
    }

    private val layoutParams by lazy { ViewGroup.LayoutParams(nativeWidth, nativeHeight) }

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        atBannerView = ATBannerView(owner.context).apply {
            setUnitId(placementId)
            setBannerAdListener(this@BannerLoader)
        }
        flSplashView.addView(atBannerView, layoutParams)
        setVisibility(View.GONE)
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
     * 广告显示
     */
    fun show(): BannerLoader {
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }

        isShowAfterLoaded = false
        onAdRenderSuc()
        return this
    }

    /**
     * 控制广告显隐
     */
    fun setVisibility(visible: Int): BannerLoader {
        atBannerView.visibility = visible
        return this
    }

    /**
     * 广告渲染成功，在已渲染添加到 View 容器上时，通过 [setVisibility] 来控制广告显隐
     */
    private fun onAdRenderSuc() {
        if (isDestroyed || atBannerView.isVisible) return
        Log.e(logTag, "onAdRenderSuc")

        setVisibility(View.VISIBLE)
        BannerCallback().apply(bannerCallback).onAdRenderSuc?.invoke()
    }

    /**
     * 广告请求加载
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting = BannerManager.isRequesting(placementId) || isDestroyed
        if (!isRequesting && !isBannerLoaded) {
            BannerManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atBannerView.loadAd()
            }
            return true
        }
        return isRequesting
    }

    private fun clearView() {
        val parent = atBannerView.parent
        if (parent is ViewGroup && parent.childCount > 0) {
            parent.removeAllViews()
        }
    }

    /**
     * 广告加载成功回调
     */
    override fun onBannerLoaded() {
        if (isDestroyed) return
        Log.e(logTag, "onBannerLoaded")

        isBannerLoaded = true
        BannerManager.updateRequestStatus(placementId, false)
        BannerCallback().apply(bannerCallback).onAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onBannerFailed(error: AdError?) {
        if (isDestroyed) return
        Log.e(logTag, "onBannerFailed:${error?.printStackTrace()}")

        BannerManager.updateRequestStatus(placementId, false)
        BannerCallback().apply(bannerCallback).onAdFailed?.invoke(error)
    }

    /**
     * 广告展示回调
     */
    override fun onBannerShow(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onBannerShow:${info.toString()}")

        BannerCallback().apply(bannerCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告点击
     */
    override fun onBannerClicked(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onBannerClicked:${info.toString()}")

        BannerCallback().apply(bannerCallback).onAdClicked?.invoke(info)
    }

    /**
     * 广告关闭回调
     */
    override fun onBannerClose(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onBannerClose:${info.toString()}")

        if (BannerCallback().apply(bannerCallback).onAdClose?.invoke(info) == true) {
            setVisibility(View.GONE)
        }
    }

    /**
     * 广告自动刷新回调
     */
    override fun onBannerAutoRefreshed(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onBannerAutoRefreshed:${info.toString()}")

        BannerCallback().apply(bannerCallback).onAdAutoRefreshed?.invoke(info)
    }

    /**
     * 广告自动刷新失败回调
     */
    override fun onBannerAutoRefreshFail(error: AdError?) {
        if (isDestroyed) return
        Log.e(logTag, "onBannerAutoRefreshFail:${error?.printStackTrace()}")

        BannerCallback().apply(bannerCallback).onAdAutoRefreshFail?.invoke(error)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        clearView()
        BannerManager.release(placementId)
        atBannerView.destroy()
    }
}