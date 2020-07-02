package com.beemans.topon.demo

import android.app.Application
import android.os.Build
import android.webkit.WebView
import com.anythink.core.api.ATSDK

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
@Suppress("unused")
class MyApp : Application() {

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
        ATSDK.init(this, "a5aa1f9deda26d", "4f7b9ac17decb9babec83aac078742c7")
    }
}