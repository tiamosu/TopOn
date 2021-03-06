package com.beemans.topon.demo.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import com.beemans.topon.demo.databinding.FragmentMainBinding

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class MainFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentMainBinding }

    private val fragments by lazy {
        arrayListOf<Class<out Fragment>>().apply {
            add(NativeAdFragment::class.java)
            add(NativeBannerFragment::class.java)
            add(NativeSplashFragment::class.java)
            add(RewardAdFragment::class.java)
            add(InterstitialAdFragment::class.java)
            add(BannerFragment::class.java)
            add(SplashFragment::class.java)
        }
    }

    override fun getLayoutId() = R.layout.fragment_main

    override fun initView(rootView: View?) {
        dataBinding.mainViewPager.apply {
            //是否可滑动
            this.isUserInputEnabled = true
            this.offscreenPageLimit = fragments.size
            //设置适配器
            adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun createFragment(position: Int) = fragments[position].newInstance()
                override fun getItemCount() = fragments.size
            }
        }.let(dataBinding.mainTabBarLayout::setViewPager2)
    }
}