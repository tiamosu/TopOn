package com.beemans.topon.demo.config

import android.content.Context
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.di.module.GlobalConfigModule
import com.tiamosu.fly.imageloader.glide.GlideImageLoaderStrategy
import com.tiamosu.fly.integration.ConfigModule

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Suppress("unused")
class GlobalConfiguration : ConfigModule {

    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
        builder.imageLoaderStrategy(GlideImageLoaderStrategy())
    }

    override fun injectAppLifecycle(context: Context, lifecycles: MutableList<IFlyAppLifecycles>) {
    }
}