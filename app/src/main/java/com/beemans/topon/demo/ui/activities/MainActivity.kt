package com.beemans.topon.demo.ui.activities

import com.beemans.topon.TopOn
import com.beemans.topon.nativead.NativeAdConfig
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseActivity
import com.beemans.topon.demo.constant.Constant
import com.beemans.topon.demo.ext.dp2px

class MainActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_main

    override fun doBusiness() {
        val config = NativeAdConfig(Constant.NATIVE_AD_ID, 350.dp2px, 270.dp2px)
        TopOn.loadNativeAd(getContext(), config)
    }
}