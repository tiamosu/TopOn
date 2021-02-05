package com.beemans.topon.banner

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.contains
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.anythink.banner.api.ATBannerListener
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.beemans.topon.ext.context
import com.tiamosu.fly.utils.post
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerLoader(
    private val owner: LifecycleOwner,
    private val bannerConfig: BannerConfig,
    private var flContainer: FrameLayout,
    private val bannerCallback: BannerCallback.() -> Unit
) : LifecycleObserver, ATBannerListener {

    private var atBannerView: ATBannerView? = null

    private val logTag by lazy { this.javaClass.simpleName }

    private val nativeWidth by lazy { bannerConfig.nativeWidth }
    private val nativeHeight by lazy { bannerConfig.nativeHeight }

    //广告位ID
    private val placementId by lazy { bannerConfig.placementId }

    //本地参数
    private val localExtra by lazy { bannerConfig.localExtra }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告加载成功
    private var isBannerLoaded = false

    //页面是否已经销毁了
    private var isDestroyed = false

    //是否进行广告请求回调
    private var isRequestAdCallback = false

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData by lazy {
        var liveData = BannerManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = MutableLiveData()
            BannerManager.loadedLiveDataMap[placementId] = liveData!!
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

    private val layoutParams by lazy { ViewGroup.LayoutParams(nativeWidth, nativeHeight) }

    //是否有在页面真实不可见时隐藏广告
    private var isHideAdFromPageInvisible = false

    //广告信息对象
    private var atAdInfo: ATAdInfo? = null

    //加载广告成功是否进行渲染
    private var isRenderAd = false

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        atBannerView = ATBannerView(owner.context).apply {
            setPlacementId(placementId)
            setLocalExtra(localExtra)
            setBannerAdListener(this@BannerLoader)
        }.apply {
            layoutParams = this@BannerLoader.layoutParams
            flContainer.addView(this)
        }
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)
        loadedLiveData.observe(owner, observer)
    }

    /**
     * 广告预加载
     */
    fun preLoadAd() {
        makeAdRequest()
    }

    /**
     * 广告加载显示
     *
     * @param isReload 是否重新加载广告，默认为false
     */
    fun show(isReload: Boolean = false): BannerLoader {
        return showAd(isReload)
    }

    /**
     * 页面真实可见时，进行广告展示
     */
    fun onFlySupportVisible() {
        if (isHideAdFromPageInvisible) {
            isHideAdFromPageInvisible = false
            showAd()
        }
    }

    /**
     * 页面真实不可见时，进行广告隐藏，防止广告自动刷新浪费资源
     */
    fun onFlySupportInvisible() {
        if (atBannerView?.isVisible == true) {
            isHideAdFromPageInvisible = true
            hideAd()
        }
    }

    /**
     * 显示广告
     */
    private fun showAd() {
        setVisibility(true)
    }

    /**
     * 隐藏广告
     */
    private fun hideAd() {
        setVisibility(false)
    }

    /**
     * 广告加载显示
     *
     * @param isManualShow 是否手动调用进行展示
     */
    private fun showAd(isReload: Boolean = false, isManualShow: Boolean = true): BannerLoader {
        if (isReload) {
            isBannerLoaded = false
        }
        if (isManualShow) {
            isRequestAdCallback = true
        }
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }

        isShowAfterLoaded = false
        if (isRenderAd) {
            isRenderAd = false
            onAdRenderSuc()
        }
        return this
    }

    /**
     * 广告请求加载
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting = BannerManager.isRequesting(placementId) || isDestroyed
        if (!isRequesting && !isBannerLoaded) {
            //某些平台广告要求ATBannerView为VISIBLE状态才能正常获取广告
            showAd()

            if (isRequestAdCallback) {
                isRequestAdCallback = false
                onAdRequest()
            }
            BannerManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atBannerView?.loadAd()
            }
            return true
        }
        return isRequesting
    }

    /**
     * 控制广告显隐
     */
    private fun setVisibility(isVisible: Boolean): BannerLoader {
        atBannerView?.isVisible = isVisible
        return this
    }

    /**
     * 广告请求
     */
    private fun onAdRequest() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRequest")

        BannerCallback().apply(bannerCallback).onAdRequest?.invoke()
    }

    /**
     * 广告渲染成功，在已渲染添加到 View 容器上时，通过 [setVisibility] 来控制广告显隐
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc:${atAdInfo?.toString()}")

        if (atBannerView != null && !flContainer.contains(atBannerView!!)) {
            flContainer.addView(atBannerView)
        }
        BannerCallback().apply(bannerCallback).onAdRenderSuc?.invoke(atAdInfo)
    }

    /**
     * 广告加载成功回调
     */
    override fun onBannerLoaded() {
        if (isDestroyed) return
        atAdInfo = atBannerView?.checkAdStatus()?.atTopAdInfo
        Log.e(logTag, "onAdLoadSuc:${atAdInfo?.toString()}")

        isBannerLoaded = true
        isRenderAd = true
        BannerManager.updateRequestStatus(placementId, false)
        BannerCallback().apply(bannerCallback).onAdLoadSuc?.invoke(atAdInfo)

        if (isShowAfterLoaded) {
            showAd(isManualShow = false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onBannerFailed(error: AdError?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdLoadFail:${error?.fullErrorInfo}")

        BannerManager.updateRequestStatus(placementId, false)
        BannerCallback().apply(bannerCallback).onAdLoadFail?.invoke(error)
    }

    /**
     * 广告展示回调
     */
    override fun onBannerShow(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdShow:${info.toString()}")

        BannerCallback().apply(bannerCallback).onAdShow?.invoke(info)
    }

    /**
     * 广告点击
     */
    override fun onBannerClicked(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClick:${info.toString()}")

        BannerCallback().apply(bannerCallback).onAdClick?.invoke(info)
    }

    /**
     * 广告关闭回调
     */
    override fun onBannerClose(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClose:${info.toString()}")

        if (BannerCallback().apply(bannerCallback).onAdClose?.invoke(info) == true) {
            clearView()
        }
    }

    /**
     * 广告自动刷新回调
     */
    override fun onBannerAutoRefreshed(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdAutoRefresh:${info.toString()}")

        BannerCallback().apply(bannerCallback).onAdAutoRefresh?.invoke(info)
    }

    /**
     * 广告自动刷新失败回调
     */
    override fun onBannerAutoRefreshFail(error: AdError?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdAutoRefreshFail:${error?.fullErrorInfo}")

        BannerCallback().apply(bannerCallback).onAdAutoRefreshFail?.invoke(error)
    }

    private fun clearView() {
        isBannerLoaded = false
        if (atBannerView != null && flContainer.contains(atBannerView!!)) {
            flContainer.removeView(atBannerView)
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
        BannerManager.release(placementId)
        atBannerView?.setBannerAdListener(null)
        atBannerView?.destroy()
        atBannerView = null
    }
}