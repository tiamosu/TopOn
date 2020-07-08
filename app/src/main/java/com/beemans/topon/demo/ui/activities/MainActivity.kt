package com.beemans.topon.demo.ui.activities

import com.beemans.topon.TopOn
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseActivity
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.pt2px
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.rewardad.RewardAdConfig

class MainActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_main

    override fun doBusiness() {
        //预加载信息流
        val config = NativeAdConfig(Constant.NATIVE_AD_ID, 350.pt2px, 270.pt2px)
        TopOn.loadNativeAd(this, config)

        //预加载激励视频
        TopOn.loadRewardAd(this, RewardAdConfig(Constant.REWARD_ID))
    }
}