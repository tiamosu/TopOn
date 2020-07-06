package com.beemans.topon.nativead

import android.view.View
import com.anythink.nativead.api.ATNativeAdRenderer
import com.anythink.nativead.unitgroup.api.CustomNativeAd
import java.util.ArrayList

/**
 * @author tiamosu
 * @date 2020/7/6.
 */
abstract class BaseNativeAdRender : ATNativeAdRenderer<CustomNativeAd> {
    val clickView: MutableList<View> = ArrayList()
}