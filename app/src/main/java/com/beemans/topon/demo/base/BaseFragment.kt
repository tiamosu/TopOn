package com.beemans.topon.demo.base

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.base.BaseFlyFragment

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
abstract class BaseFragment : BaseFlyFragment() {
    override fun initParameters(bundle: Bundle?) {}
    override fun initView(rootView: View?) {}
    override fun initEvent() {}
    override fun createObserver() {}
    override fun doBusiness() {}
}
