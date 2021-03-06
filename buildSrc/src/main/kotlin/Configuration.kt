@file:Suppress("unused", "SpellCheckingInspection")

object Android {
    //com.superapp.filemanager //com.beemans.weather.live
    const val applicationId = "com.superapp.filemanager"

    const val compileSdkVersion = 30
    const val buildToolsVersion = "30.0.3"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30

    const val versionName = "1.5.8"
    const val versionCode = 1
}

object Versions {
    const val kotlin = "1.4.21"
    const val fly = "1.8.1"
    const val leakcanary = "2.6"
    const val constraintlayout = "2.0.4"
    const val viewpager2 = "1.0.0"
    const val browser = "1.2.0"
    const val material = "1.2.1"
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

    // leakcanary 检测内存泄漏
    const val leakcanary_android =
        "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"

    //topon 需添加
    const val browser = "androidx.browser:browser:${Versions.browser}"
    const val material = "com.google.android.material:material:${Versions.material}"
}