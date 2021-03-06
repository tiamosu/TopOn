package com.beemans.topon.reward

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.anythink.rewardvideo.api.ATRewardVideoListener
import com.beemans.topon.ext.context
import com.tiamosu.fly.utils.post
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
class RewardAdLoader(
    private val owner: LifecycleOwner,
    private val rewardAdConfig: RewardAdConfig,
    private val rewardAdCallback: RewardAdCallback.() -> Unit
) : LifecycleObserver, ATRewardVideoListener {

    private var atRewardVideoAd: ATRewardVideoAd? = null

    private val logTag by lazy { this.javaClass.simpleName }
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    //广告位ID
    private val placementId by lazy { rewardAdConfig.placementId }

    //请求超时时间
    private val requestTimeOut by lazy { rewardAdConfig.requestTimeOut }

    //（可选）用于服务器激励，用户唯一ID
    private val userId by lazy { rewardAdConfig.userId }

    //（可选）用于服务器激励，用户自定义数据
    private val customData by lazy { rewardAdConfig.customData }

    //广告展示场景，可从后台创建场景参数
    private val scenario by lazy { rewardAdConfig.scenario }

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData by lazy {
        var liveData = RewardAdManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = MutableLiveData()
            RewardAdManager.loadedLiveDataMap[placementId] = liveData!!
        }
        liveData!!
    }

    //观察者
    private val observer by lazy {
        Observer<Boolean> {
            if (isShowAfterLoaded) {
                showAd(false)
            }
        }
    }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //是否超时
    private var isAdLoadTimeOut = false

    //页面是否已经销毁了
    private var isDestroyed = false

    //是否进行广告请求回调
    private var isRequestAdCallback = false

    //广告信息对象
    private var atAdInfo: ATAdInfo? = null

    //是否真正下发奖励
    private var isAdReward = false

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        atRewardVideoAd = ATRewardVideoAd(owner.context, placementId).apply {
            setAdListener(this@RewardAdLoader)

            mutableMapOf<String, Any>().apply {
                put(ATAdConst.KEY.USER_ID, userId)
                put(ATAdConst.KEY.USER_CUSTOM_DATA, customData)
            }.let(this::setLocalExtra)
        }
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)
        loadedLiveData.observe(owner, observer)
    }

    /**
     * 激励视频预加载
     */
    fun preloadAd() {
        makeAdRequest()
    }

    /**
     * 广告加载显示
     */
    fun show(): RewardAdLoader {
        return showAd()
    }

    /**
     * @param isManualShow 是否手动调用进行展示
     */
    private fun showAd(isManualShow: Boolean = true): RewardAdLoader {
        if (isManualShow) {
            isRequestAdCallback = true
        }
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }

        isShowAfterLoaded = false
        atRewardVideoAd?.show(owner.context, scenario)
        onAdRenderSuc()
        return this
    }

    /**
     * 广告加载请求
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting =
            RewardAdManager.isRequesting(placementId) || isDestroyed
        if (!isRequesting && isRequestAdCallback) {
            isRequestAdCallback = false
            onAdRequest()
        }

        val isAdReady = atRewardVideoAd?.isAdReady ?: false
        if (!isRequesting && !isAdReady) {
            RewardAdManager.updateRequestStatus(placementId, true)

            try {
                post(Schedulers.io()) {
                    atRewardVideoAd?.load()
                }
            } catch (e: Exception) {
            }

            //开始进行超时倒计时
            handler.postDelayed({
                onAdLoadTimeOut()
            }, requestTimeOut)
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

        isAdReward = false
        isAdLoadTimeOut = false
        RewardAdCallback().apply(rewardAdCallback).onAdRequest?.invoke()
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc:${atAdInfo?.toString()}")

        RewardAdCallback().apply(rewardAdCallback).onAdRenderSuc?.invoke(atAdInfo)
    }

    /**
     * 广告加载超时
     */
    private fun onAdLoadTimeOut() {
        if (isDestroyed) return
        Log.e(logTag, "onAdLoadTimeOut")

        isAdLoadTimeOut = true
        RewardAdManager.updateRequestStatus(placementId, false)
        RewardAdCallback().apply(rewardAdCallback).onAdLoadTimeOut?.invoke()
    }

    /**
     * 广告加载成功回调
     */
    override fun onRewardedVideoAdLoaded() {
        atAdInfo = atRewardVideoAd?.checkAdStatus()?.atTopAdInfo

        if (isDestroyed || isAdLoadTimeOut) return
        Log.e(logTag, "onAdLoadSuc:${atAdInfo?.toString()}")

        handler.removeCallbacksAndMessages(null)
        RewardAdManager.updateRequestStatus(placementId, false)
        RewardAdCallback().apply(rewardAdCallback).onAdLoadSuc?.invoke(atAdInfo)

        if (isShowAfterLoaded) {
            showAd(false)
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onRewardedVideoAdFailed(error: AdError?) {
        if (isDestroyed || isAdLoadTimeOut) return
        Log.e(logTag, "onAdLoadFail:${error?.fullErrorInfo}")

        handler.removeCallbacksAndMessages(null)
        RewardAdManager.updateRequestStatus(placementId, false)
        RewardAdCallback().apply(rewardAdCallback).onAdLoadFail?.invoke(error)
    }

    /**
     * 广告关闭回调，建议在此回调中调用load进行广告的加载，方便下一次广告的展示
     */
    override fun onRewardedVideoAdClosed(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClose:${info?.toString()}")

        RewardAdCallback().apply(rewardAdCallback).onAdClose?.invoke(info, isAdReward)
    }

    /**
     * 下发激励的时候会回调该接口
     */
    override fun onReward(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdReward:${info?.toString()}")

        isAdReward = true
        RewardAdCallback().apply(rewardAdCallback).onAdReward?.invoke(info)
    }

    /**
     * 广告播放失败回调
     */
    override fun onRewardedVideoAdPlayFailed(error: AdError?, info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoFail:${error?.fullErrorInfo}   info:${info?.toString()}")

        RewardAdCallback().apply(rewardAdCallback).onAdVideoFail?.invoke(error, info)
    }

    /**
     * 广告刷新回调
     */
    override fun onRewardedVideoAdPlayStart(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoStart:${info?.toString()}")

        RewardAdCallback().apply(rewardAdCallback).onAdVideoStart?.invoke(info)
    }

    /**
     * 广告播放结束
     */
    override fun onRewardedVideoAdPlayEnd(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdVideoEnd:${info?.toString()}")

        RewardAdCallback().apply(rewardAdCallback).onAdVideoEnd?.invoke(info)
    }

    /**
     * 广告点击
     */
    override fun onRewardedVideoAdPlayClicked(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onAdClick:${info?.toString()}")

        RewardAdCallback().apply(rewardAdCallback).onAdClick?.invoke(info)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onAdLoaderDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        loadedLiveData.removeObserver(observer)
        handler.removeCallbacksAndMessages(null)
        RewardAdManager.release(placementId)
        atRewardVideoAd?.setAdListener(null)
        atRewardVideoAd = null
    }
}