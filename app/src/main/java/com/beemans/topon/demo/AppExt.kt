package com.beemans.topon.demo

import com.blankj.utilcode.util.SizeUtils

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
val Int.dp2px: Int
    get() = SizeUtils.dp2px(this.toFloat())