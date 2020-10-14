package com.beemans.topon.demo.ext

import com.beemans.topon.demo.utils.AutoSize
import com.blankj.utilcode.util.ScreenUtils
import kotlin.math.roundToInt

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
val Int.pt2px: Int
    get() {
        val screenWidth = ScreenUtils.getAppScreenWidth()
        return (screenWidth / AutoSize.DESIGN_WIDTH.toFloat() * this).roundToInt()
    }