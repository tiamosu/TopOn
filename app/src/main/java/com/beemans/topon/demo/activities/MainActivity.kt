package com.beemans.topon.demo.activities

import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseActivity
import com.blankj.utilcode.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getLayoutId() = R.layout.activity_main

    override fun initEvent() {
        main_nativeAd.setOnClickListener {
            ActivityUtils.startActivity(NativeAdActivity::class.java)
        }
    }

    override fun doBusiness() {}
}