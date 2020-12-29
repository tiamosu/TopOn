package com.beemans.topon.reward

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * @author tiamosu
 * @date 2020/7/7.
 */
@Keep
@Parcelize
data class RewardAdConfig(
    val placementId: String,            //广告位ID
    val isUsePreload: Boolean = false,  //是否进行广告预加载
    val requestTimeOut: Long = 5000,    //请求超时时间
    val userId: String = "",            //设置用户的信息，主要用于激励下发
    val customData: String = "",
    val scenario: String = ""           //广告展示场景，可从后台创建场景参数
) : Parcelable