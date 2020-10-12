@file:Suppress("unused", "SpellCheckingInspection")

object Android {
    const val compileSdkVersion = 30
    const val buildToolsVersion = "30.0.2"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30

    const val versionName = "1.5.8"
    const val versionCode = 1
}

object Versions {
    const val kotlin = "1.4.10"
    const val fly = "1.5.4"
    const val leakcanary = "2.4"
}

object Deps {
    // fly Android 快速集成框架
    const val fly = "me.tiamosu:fly:${Versions.fly}"
    const val fly_http = "me.tiamosu:fly-http:${Versions.fly}"
    const val fly_imageloader_glide = "me.tiamosu:fly-imageloader-glide:${Versions.fly}"

    // androidx
    const val androidx_constraint_layout = "androidx.constraintlayout:constraintlayout:2.0.2"
    const val androidx_viewpager2 = "androidx.viewpager2:viewpager2:1.0.0"

    // leakcanary 检测内存泄漏
    const val leakcanary_android =
        "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"
}