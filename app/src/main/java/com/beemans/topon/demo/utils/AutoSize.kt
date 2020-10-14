package com.beemans.topon.demo.utils

import android.app.Activity
import android.content.res.Resources
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ScreenUtils

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
object AutoSize {
    const val DESIGN_WIDTH = 375

    fun adaptScreen(resources: Resources): Resources {
        return if (ScreenUtils.isPortrait()) {
            AdaptScreenUtils.adaptWidth(resources, DESIGN_WIDTH)
        } else {
            AdaptScreenUtils.adaptHeight(resources, DESIGN_WIDTH)
        }
    }

    fun cancelAdaptScreen(activity: Activity) {
        AdaptScreenUtils.closeAdapt(activity.resources)
    }
}