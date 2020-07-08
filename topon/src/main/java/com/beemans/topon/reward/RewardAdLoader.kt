package com.beemans.topon.reward

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.anythink.rewardvideo.api.ATRewardVideoListener
import com.tiamosu.fly.callback.EventLiveData

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
    private val loaderTag by lazy { this.toString() }
    private val handler by lazy { Handler(Looper.getMainLooper()) }

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

    //广告位ID
    private val placementId by lazy { rewardAdConfig.placementId }

    //是否进行广告预加载
    private val isUsePreload by lazy { rewardAdConfig.isUsePreload }

    //请求超时时间
    private val requestTimeOut by lazy { rewardAdConfig.requestTimeOut }

    //同时请求相同广告位ID时，会报错提示正在请求中，用于请求成功通知展示广告
    private val loadedLiveData: EventLiveData<Boolean> by lazy {
        var liveData = RewardAdManager.loadedLiveDataMap[placementId]
        if (liveData == null) {
            liveData = EventLiveData()
            RewardAdManager.loadedLiveDataMap[placementId] = liveData
        }
        liveData
    }

    //是否在广告加载完成进行播放
    private var isShowAfterLoaded = false

    //广告请求中
    private var isRequesting = false

    //广告正在播放
    private var isAdPlaying = false

    //是否超时
    private var isTimeOut = false

    private var isDestroyed = false

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {
        if (atRewardVideoAd == null) {
            atRewardVideoAd = ATRewardVideoAd(activity, placementId).apply {
                setAdListener(this@RewardAdLoader)
                setUserData(rewardAdConfig.userId, rewardAdConfig.customData)
            }
        }

        preloadAd()
    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)

        loadedLiveData.observe(owner) {
            if (isShowAfterLoaded && !isDestroyed && !isTimeOut) {
                show()
            }
        }
    }

    /**
     * 激励视频预加载
     */
    private fun preloadAd() {
        if (isUsePreload) {
            load()
        }
    }

    /**
     * 广告加载请求
     */
    private fun load(): Boolean {
        isRequesting = RewardAdManager.isRequesting(placementId)
        if (!isRequesting && atRewardVideoAd?.isAdReady == false && !isDestroyed && !isAdPlaying) {
            RewardAdManager.updateRequestStatus(placementId, loaderTag, true)
            atRewardVideoAd?.load()

            handler.postDelayed({
                onRewardedVideoAdTimeOut()
            }, requestTimeOut)
            return true
        }
        return isRequesting || isAdPlaying
    }

    /**
     * 广告加载显示
     */
    fun show(): RewardAdLoader {
        isShowAfterLoaded = true
        if (load()) {
            return this
        }
        isTimeOut = false
        isShowAfterLoaded = false

        if (rewardAdConfig.scenario.isNotBlank()) {
            atRewardVideoAd?.show(activity, rewardAdConfig.scenario)
        } else {
            atRewardVideoAd?.show(activity)
        }
        adRenderSuc()
        return this
    }

    /**
     * 广告渲染成功
     */
    private fun adRenderSuc() {
        RewardAdCallback().apply(rewardAdCallback).onRewardRenderSuc?.invoke()
    }

    /**
     * 广告加载超时
     */
    private fun onRewardedVideoAdTimeOut() {
        Log.e(logTag, "onRewardedVideoAdTimeOut")
        if (isDestroyed) return
        isTimeOut = true
        isShowAfterLoaded = true
        RewardAdManager.updateRequestStatus(placementId, loaderTag, false)
        RewardAdCallback().apply(rewardAdCallback).onRewardedVideoAdTimeOut?.invoke()
    }

    /**
     * 广告加载成功回调
     */
    override fun onRewardedVideoAdLoaded() {
        Log.e(logTag, "onRewardedVideoAdLoaded")
        if (isDestroyed || isTimeOut) return
        handler.removeCallbacksAndMessages(null)
        RewardAdManager.updateRequestStatus(placementId, loaderTag, false)
        RewardAdCallback().apply(rewardAdCallback).onRewardedVideoAdLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onRewardedVideoAdFailed(error: AdError?) {
        Log.e(logTag, "onRewardedVideoAdFailed:${error?.printStackTrace()}")
        if (isDestroyed) return
        handler.removeCallbacksAndMessages(null)
        isShowAfterLoaded = true
        RewardAdManager.updateRequestStatus(placementId, loaderTag, false)
        RewardAdCallback().apply(rewardAdCallback).onRewardedVideoAdFailed?.invoke(error)
    }

    /**
     * 广告关闭回调，建议在此回调中调用load进行广告的加载，方便下一次广告的展示
     */
    override fun onRewardedVideoAdClosed(info: ATAdInfo?) {
        Log.e(logTag, "onRewardedVideoAdClosed:${info.toString()}")
        isAdPlaying = false
        RewardAdCallback().apply(rewardAdCallback).onRewardedVideoAdClosed?.invoke()
        preloadAd()
    }

    /**
     * 下发激励的时候会回调该接口
     */
    override fun onReward(info: ATAdInfo?) {
        Log.e(logTag, "onReward:${info.toString()}")
        if (isDestroyed) return
        RewardAdCallback().apply(rewardAdCallback).onReward?.invoke()
    }

    /**
     * 广告播放失败回调
     */
    override fun onRewardedVideoAdPlayFailed(error: AdError?, info: ATAdInfo?) {
        Log.e(
            logTag,
            "onRewardedVideoAdPlayFailed:${error?.printStackTrace()}   info:${info.toString()}"
        )
        if (isDestroyed) return
        isAdPlaying = false
        RewardAdCallback().apply(rewardAdCallback).onRewardedVideoAdPlayFailed?.invoke(error)
    }

    /**
     * 广告刷新回调
     */
    override fun onRewardedVideoAdPlayStart(info: ATAdInfo?) {
        Log.e(logTag, "onRewardedVideoAdPlayStart:${info.toString()}")
        if (isDestroyed) return
        isAdPlaying = true
        RewardAdCallback().apply(rewardAdCallback).onRewardedVideoAdPlayStart?.invoke()
    }

    /**
     * 广告播放结束
     */
    override fun onRewardedVideoAdPlayEnd(info: ATAdInfo?) {
        Log.e(logTag, "onRewardedVideoAdPlayEnd:${info.toString()}")
        if (isDestroyed) return
        isAdPlaying = false
        RewardAdCallback().apply(rewardAdCallback).onRewardedVideoAdPlayEnd?.invoke()
        preloadAd()
    }

    /**
     * 广告点击
     */
    override fun onRewardedVideoAdPlayClicked(info: ATAdInfo?) {
        Log.e(logTag, "onRewardedVideoAdPlayClicked:${info.toString()}")
        RewardAdCallback().apply(rewardAdCallback).onRewardedVideoAdPlayClicked?.invoke()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        handler.removeCallbacksAndMessages(null)
        RewardAdManager.release(placementId)
    }
}