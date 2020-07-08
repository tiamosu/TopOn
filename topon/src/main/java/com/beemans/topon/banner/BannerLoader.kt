package com.beemans.topon.banner

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.anythink.banner.api.ATBannerListener
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.tiamosu.fly.callback.EventLiveData

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerLoader(
    private val owner: LifecycleOwner,
    private val bannerConfig: BannerConfig,
    private val bannerCallback: BannerCallback.() -> Unit
) : LifecycleObserver, ATBannerListener {

    private var atBannerView: ATBannerView? = null

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

    //已经渲染添加
    private var isRenderAdded = false

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

    private val flAd by lazy { FrameLayout(activity) }

    private val layoutParams by lazy { ViewGroup.LayoutParams(nativeWidth, nativeHeight) }

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        if (atBannerView == null) {
            atBannerView = ATBannerView(activity).apply {
                setUnitId(placementId)
                setBannerAdListener(this@BannerLoader)
            }
        }

        preLoadAd()
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)

        loadedLiveData.observe(owner) {
            if (isShowAfterLoaded && !isDestroyed) {
                show()
            }
        }
    }

    /**
     * 广告预加载
     */
    private fun preLoadAd() {
        if (isUsePreload) {
            load()
        }
    }

    /**
     * 广告显示
     */
    fun show(): BannerLoader {
        isShowAfterLoaded = true
        if (load()) {
            return this
        }
        isShowAfterLoaded = false
        adRenderSuc()
        return this
    }

    /**
     * 控制广告显隐
     */
    fun setVisibility(visible: Int): BannerLoader {
        atBannerView?.visibility = visible
        return this
    }

    /**
     * 广告渲染成功，在已渲染添加到 View 容器上时，通过 [setVisibility] 来控制广告显隐
     */
    private fun adRenderSuc() {
        if (isRenderAdded) {
            if (atBannerView?.isVisible == false) {
                setVisibility(View.VISIBLE)
            }
            return
        }
        isRenderAdded = true
        clearView()
        flAd.addView(atBannerView, layoutParams)
        BannerCallback().apply(bannerCallback).onRenderSuc?.invoke(flAd)
    }

    private fun load(): Boolean {
        val isRequesting = BannerManager.isRequesting(placementId) || isDestroyed
        if (!isRequesting && !isBannerLoaded) {
            BannerManager.updateRequestStatus(placementId, loaderTag, true)
            atBannerView?.loadAd()
            return true
        }
        return isRequesting
    }

    private fun clearView() {
        (flAd.parent as? ViewGroup)?.removeView(flAd)
        if (flAd.childCount > 0) {
            flAd.removeAllViews()
        }
    }

    /**
     * 广告加载成功回调
     */
    override fun onBannerLoaded() {
        Log.e(logTag, "onBannerLoaded")
        if (isDestroyed) return
        isBannerLoaded = true
        BannerManager.updateRequestStatus(placementId, loaderTag, false)
        BannerCallback().apply(bannerCallback).onBannerLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onBannerFailed(error: AdError?) {
        Log.e(logTag, "onBannerFailed:${error?.printStackTrace()}")
        if (isDestroyed) return
        isShowAfterLoaded = true
        BannerManager.updateRequestStatus(placementId, loaderTag, false)
        BannerCallback().apply(bannerCallback).onBannerFailed?.invoke(error)
    }

    /**
     * 广告展示回调
     */
    override fun onBannerShow(info: ATAdInfo?) {
        Log.e(logTag, "onBannerShow:${info.toString()}")
        BannerCallback().apply(bannerCallback).onBannerShow?.invoke(info)
    }

    /**
     * 广告点击
     */
    override fun onBannerClicked(info: ATAdInfo?) {
        Log.e(logTag, "onBannerClicked:${info.toString()}")
        BannerCallback().apply(bannerCallback).onBannerClicked?.invoke(info)
    }

    /**
     * 广告关闭回调
     */
    override fun onBannerClose(info: ATAdInfo?) {
        Log.e(logTag, "onBannerClose:${info.toString()}")
        if (BannerCallback().apply(bannerCallback).onBannerClose?.invoke(info) == true) {
            setVisibility(View.GONE)
        }
    }

    /**
     * 广告自动刷新回调
     */
    override fun onBannerAutoRefreshed(info: ATAdInfo?) {
        Log.e(logTag, "onBannerAutoRefreshed:${info.toString()}")
        BannerCallback().apply(bannerCallback).onBannerAutoRefreshed?.invoke(info)
    }

    /**
     * 广告自动刷新失败回调
     */
    override fun onBannerAutoRefreshFail(error: AdError?) {
        Log.e(logTag, "onBannerAutoRefreshFail:${error?.printStackTrace()}")
        BannerCallback().apply(bannerCallback).onBannerAutoRefreshFail?.invoke(error)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")
        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        clearView()
        BannerManager.release(placementId)
        atBannerView = null
    }
}