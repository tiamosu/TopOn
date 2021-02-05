package com.beemans.topon.demo.ui.fragments

import android.util.Log
import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.databinding.FragmentRewardAdBinding
import com.beemans.topon.reward.RewardAdConfig
import com.beemans.topon.reward.RewardAdLoader

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
class RewardAdFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentRewardAdBinding }
    private var rewardAdLoader: RewardAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_reward_ad

    override fun initEvent() {
        dataBinding.rewardAdBtnLoad.setOnClickListener {
            if (rewardAdLoader == null) {
                val config = RewardAdConfig(Constant.REWARD_ID)
                rewardAdLoader = TopOn.loadRewardAd(this, config) {
                    onAdRequest {
                        Log.e("xia", "onAdRequest")
                    }
                    onAdRenderSuc { info ->
                        Log.e("xia", "onAdRenderSuc:$info")
                    }
                    onAdReward {
                        Log.e("xia", "onAdReward")
                    }
                    onAdClose { _, isReward ->
                        Log.e("xia", "isReward:$isReward")
                    }
                }
            }
            rewardAdLoader?.show()
        }
    }
}