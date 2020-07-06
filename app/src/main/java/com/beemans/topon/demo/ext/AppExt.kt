package com.beemans.topon.demo.ext

import com.blankj.utilcode.util.AdaptScreenUtils

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
val Int.pt2px: Int
    get() = AdaptScreenUtils.pt2Px(this.toFloat())