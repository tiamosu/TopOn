package com.beemans.topon.demo

import android.os.Build
import android.webkit.WebView
import com.anythink.core.api.ATSDK
import com.beemans.topon.demo.constant.Constant
import com.tiamosu.fly.base.BaseFlyApplication

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Suppress("unused")
class MyApp : BaseFlyApplication() {

    override fun onCreate() {
        super.onCreate()

        // 多进程WebView的兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }

        //开启日志功能
        ATSDK.setNetworkLogDebug(true)
        ATSDK.integrationChecking(applicationContext)
        ATSDK.init(this, Constant.APP_ID, Constant.APP_KEY)
    }
}