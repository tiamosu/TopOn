package com.beemans.topon.demo.ui.fragments

import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.beemans.topon.demo.R
import com.beemans.topon.demo.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * @author tiamosu
 * @date 2020/7/5.
 */
class MainFragment : BaseFragment() {

    private val fragments by lazy {
        arrayListOf<Class<out Fragment>>().apply {
            add(HomeFragment::class.java)
            add(NativeAdFragment::class.java)
        }
    }

    override fun getLayoutId() = R.layout.fragment_main

    override fun initView(rootView: View?) {
        main_viewPager.apply {
            //是否可滑动
            this.isUserInputEnabled = isUserInputEnabled
            this.offscreenPageLimit = offscreenPageLimit
            //设置适配器
            adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun createFragment(position: Int) = fragments[position].newInstance()
                override fun getItemCount() = fragments.size
            }
        }.let(main_tabBarLayout::setViewPager2)
    }

    override fun initEvent() {
        main_tabBarLayout.setOnItemSelectedListener(onItemSelected = { position, prePosition ->
            Log.e("xia", "onItemSelected:$position  prePosition:$prePosition")
        }, onItemUnselected = { position ->
            Log.e("xia", "onItemUnselected:$position")
        }, onItemReselected = { position ->
            Log.e("xia", "onItemReselected:$position")
        })
    }
}