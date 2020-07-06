package com.beemans.topon.nativead.banner

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.anythink.nativead.banner.api.ATNativeBannerView

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
class NativeBannerLoader(
    private val owner: LifecycleOwner
) {
    private val activity by lazy {
        when (owner) {
            is Fragment -> {
                owner.requireActivity()
            }
            is FragmentActivity -> {
                owner
            }
            else -> {
                throw IllegalArgumentException("owner must instanceof Fragment or FragmentActivity！")
            }
        }
    }

    private val atNativeBannerView by lazy { ATNativeBannerView(activity) }

    init {
        initNative()
    }

    private fun initNative() {

    }
}