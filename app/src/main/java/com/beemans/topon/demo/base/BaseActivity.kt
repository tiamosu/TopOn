package com.beemans.topon.demo.base

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import com.beemans.topon.demo.utils.AutoSize
import com.tiamosu.fly.base.BaseFlyActivity

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
abstract class BaseActivity : BaseFlyActivity() {
    override fun initParameters(bundle: Bundle?) {}
    override fun initView(rootView: View?) {}
    override fun initEvent() {}
    override fun createObserver() {}

    override fun getResources(): Resources {
        return AutoSize.adaptScreen(super.getResources())
    }
}