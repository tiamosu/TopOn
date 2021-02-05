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
    val requestTimeOut: Long = 5000,    //请求超时时间
    val userId: String = "",            //（可选）用于服务器激励，用户唯一ID
    val customData: String = "",        //（可选）用于服务器激励，用户自定义数据
    val scenario: String = ""           //广告展示场景，可从后台创建场景参数
) : Parcelable