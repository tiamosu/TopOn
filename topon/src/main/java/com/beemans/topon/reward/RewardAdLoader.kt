package com.beemans.topon.reward

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.anythink.rewardvideo.api.ATRewardVideoListener
import com.beemans.topon.ext.context
import com.tiamosu.fly.callback.EventLiveData
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
            atRewardVideoAd = ATRewardVideoAd(owner.context, placementId).apply {
                setAdListener(this@RewardAdLoader)

                val localMap: MutableMap<String, Any> = mutableMapOf()
                localMap.apply {
                    put(ATAdConst.KEY.USER_ID, rewardAdConfig.userId)
                    put(ATAdConst.KEY.USER_CUSTOM_DATA, rewardAdConfig.customData)
                }.let(this::setLocalExtra)
            }
        }

        preloadAd()
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
     * 激励视频预加载
     */
    private fun preloadAd() {
        if (isUsePreload) {
            makeAdRequest()
        }
    }

    /**
     * 广告加载请求
     */
    private fun makeAdRequest(): Boolean {
        val isRequesting = RewardAdManager.isRequesting(placementId) || isAdPlaying || isDestroyed
        val isAdReady = atRewardVideoAd?.isAdReady ?: false
        if (!isRequesting && !isAdReady) {
            RewardAdManager.updateRequestStatus(placementId, true)

            post(Schedulers.io()) {
                atRewardVideoAd?.load()
            }

            handler.postDelayed({
                onAdTimeOut()
            }, requestTimeOut)
            return true
        }
        return isRequesting
    }

    /**
     * 广告加载显示
     */
    fun show(): RewardAdLoader {
        isShowAfterLoaded = true
        if (makeAdRequest()) {
            return this
        }

        isTimeOut = false
        isShowAfterLoaded = false
        if (rewardAdConfig.scenario.isNotBlank()) {
            atRewardVideoAd?.show(owner.context, rewardAdConfig.scenario)
        } else {
            atRewardVideoAd?.show(owner.context)
        }
        onAdRenderSuc()
        return this
    }

    /**
     * 广告渲染成功
     */
    private fun onAdRenderSuc() {
        if (isDestroyed) return
        Log.e(logTag, "onAdRenderSuc")

        RewardAdCallback().apply(rewardAdCallback).onAdRenderSuc?.invoke()
    }

    /**
     * 广告加载超时
     */
    private fun onAdTimeOut() {
        if (isDestroyed) return
        Log.e(logTag, "onAdTimeOut")

        isTimeOut = true
        RewardAdManager.updateRequestStatus(placementId, false)
        RewardAdCallback().apply(rewardAdCallback).onAdTimeOut?.invoke()
    }

    /**
     * 广告加载成功回调
     */
    override fun onRewardedVideoAdLoaded() {
        if (isDestroyed || isTimeOut) return
        Log.e(logTag, "onRewardedVideoAdLoaded")

        handler.removeCallbacksAndMessages(null)
        RewardAdManager.updateRequestStatus(placementId, false)
        RewardAdCallback().apply(rewardAdCallback).onAdVideoLoaded?.invoke()

        if (isShowAfterLoaded) {
            show()
        }
        loadedLiveData.value = true
    }

    /**
     * 广告加载失败回调
     */
    override fun onRewardedVideoAdFailed(error: AdError?) {
        if (isDestroyed || isTimeOut) return
        Log.e(logTag, "onRewardedVideoAdFailed:${error?.printStackTrace()}")

        handler.removeCallbacksAndMessages(null)
        RewardAdManager.updateRequestStatus(placementId, false)
        RewardAdCallback().apply(rewardAdCallback).onAdVideoFailed?.invoke(error)
    }

    /**
     * 广告关闭回调，建议在此回调中调用load进行广告的加载，方便下一次广告的展示
     */
    override fun onRewardedVideoAdClosed(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onRewardedVideoAdClosed:${info.toString()}")

        isAdPlaying = false
        RewardAdCallback().apply(rewardAdCallback).onAdVideoClosed?.invoke(info)
        preloadAd()
    }

    /**
     * 下发激励的时候会回调该接口
     */
    override fun onReward(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onReward:${info.toString()}")

        RewardAdCallback().apply(rewardAdCallback).onAdReward?.invoke(info)
    }

    /**
     * 广告播放失败回调
     */
    override fun onRewardedVideoAdPlayFailed(error: AdError?, info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(
            logTag,
            "onRewardedVideoAdPlayFailed:${error?.printStackTrace()}   info:${info.toString()}"
        )

        isAdPlaying = false
        RewardAdCallback().apply(rewardAdCallback).onAdVideoPlayFailed?.invoke(error, info)
    }

    /**
     * 广告刷新回调
     */
    override fun onRewardedVideoAdPlayStart(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onRewardedVideoAdPlayStart:${info.toString()}")

        isAdPlaying = true
        RewardAdCallback().apply(rewardAdCallback).onAdVideoPlayStart?.invoke(info)
    }

    /**
     * 广告播放结束
     */
    override fun onRewardedVideoAdPlayEnd(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onRewardedVideoAdPlayEnd:${info.toString()}")

        isAdPlaying = false
        RewardAdCallback().apply(rewardAdCallback).onAdVideoPlayEnd?.invoke(info)
    }

    /**
     * 广告点击
     */
    override fun onRewardedVideoAdPlayClicked(info: ATAdInfo?) {
        if (isDestroyed) return
        Log.e(logTag, "onRewardedVideoAdPlayClicked:${info.toString()}")

        RewardAdCallback().apply(rewardAdCallback).onAdVideoPlayClicked?.invoke(info)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy(owner: LifecycleOwner) {
        Log.e(logTag, "onDestroy")

        isDestroyed = true
        owner.lifecycle.removeObserver(this)
        handler.removeCallbacksAndMessages(null)
        RewardAdManager.release(placementId)
        atRewardVideoAd = null
    }
}