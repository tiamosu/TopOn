package com.beemans.topon.banner

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.anythink.banner.api.ATBannerListener
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

/**
 * @author tiamosu
 * @date 2020/7/8.
 */
class BannerLoader(
    private val owner: LifecycleOwner,
    private val bannerConfig: BannerConfig,
    private val bannerCallback: BannerCallback.() -> Unit
) : LifecycleObserver, ATBannerListener {

    init {

    }

    /**
     * 广告加载成功回调
     */
    override fun onBannerLoaded() {

    }

    /**
     * 广告加载失败回调
     */
    override fun onBannerFailed(error: AdError?) {

    }

    /**
     * 广告点击
     */
    override fun onBannerClicked(info: ATAdInfo?) {

    }

    /**
     * 广告展示回调
     */
    override fun onBannerShow(info: ATAdInfo?) {

    }

    /**
     * 广告关闭回调
     */
    override fun onBannerClose(info: ATAdInfo?) {

    }

    /**
     * 广告自动刷新回调
     */
    override fun onBannerAutoRefreshed(info: ATAdInfo?) {

    }

    /**
     * 广告自动刷新失败回调
     */
    override fun onBannerAutoRefreshFail(error: AdError?) {

    }
}