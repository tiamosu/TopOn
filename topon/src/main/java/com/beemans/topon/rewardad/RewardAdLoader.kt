package com.beemans.topon.rewardad

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
class RewardAdLoader(
    private val owner: LifecycleOwner,
    private val rewardAdConfig: RewardAdConfig,
    private val rewardAdCallback: RewardAdCallback.() -> Unit
) : LifecycleObserver {

    init {
        initAd()
        createObserve()
    }

    private fun initAd() {

    }

    private fun createObserve() {
        owner.lifecycle.addObserver(this)

    }
}