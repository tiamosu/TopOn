@file:Suppress("unused", "SpellCheckingInspection")

object Android {
    //com.superapp.filemanager //com.beemans.weather.live
    const val applicationId = "com.superapp.filemanager"

    const val compileSdkVersion = 30
    const val buildToolsVersion = "30.0.2"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30

    const val versionName = "1.5.8"
    const val versionCode = 1
}

object Versions {
    const val kotlin = "1.4.10"
    const val fly = "1.6.6"
    const val leakcanary = "2.5"
    const val constraintlayout = "2.0.4"
    const val viewpager2 = "1.0.0"
    const val support = "28.0.0"
    const val browser = "1.2.0"
}

object Deps {
    // fly Android 快速集成框架
    const val fly = "me.tiamosu:fly:${Versions.fly}"
    const val fly_http = "me.tiamosu:fly-http:${Versions.fly}"
    const val fly_imageloader_glide = "me.tiamosu:fly-imageloader-glide:${Versions.fly}"

    // androidx
    const val androidx_constraint_layout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    const val androidx_viewpager2 = "androidx.viewpager2:viewpager2:${Versions.viewpager2}"
    const val browser = "androidx.browser:browser:${Versions.browser}"

    // leakcanary 检测内存泄漏
    const val leakcanary_android =
        "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"

    //快手需配置
    const val design = "com.android.support:design:${Versions.support}"
}