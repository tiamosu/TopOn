package com.beemans.topon.demo.ui.fragments

import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.rewardad.RewardAdConfig
import com.beemans.topon.rewardad.RewardAdLoader
import kotlinx.android.synthetic.main.fragment_reward_ad.*

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
class RewardAdFragment : BaseFragment() {
    private var rewardAdLoader: RewardAdLoader? = null

    override fun getLayoutId() = R.layout.fragment_reward_ad

    override fun initEvent() {
        rewardAd_btnLoad.setOnClickListener {
            if (rewardAdLoader == null) {
                val config = RewardAdConfig(Constant.REWARD_ID, requestTimeOut = 200)
                rewardAdLoader = TopOn.loadRewardAd(this, config) {}
            }
            rewardAdLoader?.show()
        }
    }
}