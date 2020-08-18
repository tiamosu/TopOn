package com.beemans.topon.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.tiamosu.fly.base.BaseFlyActivity
import com.tiamosu.fly.base.BaseFlyFragment

/**
 * @author tiamosu
 * @date 2020/8/18.
 */
val LifecycleOwner.context: FragmentActivity
    get() {
        return when (this) {
            is BaseFlyFragment -> context
            is Fragment -> this.requireActivity()
            is BaseFlyActivity -> this
            is FragmentActivity -> this
            else -> throw IllegalArgumentException("owner must is Fragment or FragmentActivity")
        }
    }