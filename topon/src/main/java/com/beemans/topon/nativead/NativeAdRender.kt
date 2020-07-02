package com.beemans.topon.nativead

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.anythink.nativead.api.ATNativeAdRenderer
import com.anythink.nativead.unitgroup.api.CustomNativeAd
import com.beemans.topon.R
import java.util.ArrayList

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdRender : ATNativeAdRenderer<CustomNativeAd> {
    private var clickView: MutableList<View> = ArrayList()
    private var developView: View? = null
    private var networkType = 0

    /**
     * 用于创建自定义的Native广告布局的View
     */
    override fun createView(context: Context, networkType: Int): View {
        this.networkType = networkType
        if (developView == null) {
            developView = View.inflate(context, R.layout.native_ad_item, null)
        }
        (developView?.parent as? ViewGroup)?.removeView(developView)
        return developView!!
    }

    /**
     * 用于实现广告内容渲染的方法，其中customNativeAd是广告素材的对象，可提供素材进行渲染
     */
    override fun renderAdView(view: View, ad: CustomNativeAd) {
        val titleView: TextView = view.findViewById(R.id.native_ad_title)
        val descView: TextView = view.findViewById(R.id.native_ad_desc)
        val ctaView: TextView = view.findViewById(R.id.native_ad_install_btn)
        val adFromView: TextView = view.findViewById(R.id.native_ad_from)
        val contentArea: FrameLayout = view.findViewById(R.id.native_ad_content_image_area)
        val iconArea: FrameLayout = view.findViewById(R.id.native_ad_image)

        titleView.text = ""
        descView.text = ""
        ctaView.text = ""
        adFromView.text = ""
        titleView.text = ""

        if (contentArea.childCount > 0) {
            contentArea.removeAllViews()
        }
        if (iconArea.childCount > 0) {
            iconArea.removeAllViews()
        }

        //个性化模板
        if (ad.isNativeExpress) {
            titleView.visibility = View.GONE
            descView.visibility = View.GONE
            ctaView.visibility = View.GONE
            iconArea.visibility = View.GONE
        } else { //自渲染
            titleView.visibility = View.VISIBLE
            descView.visibility = View.VISIBLE
            ctaView.visibility = View.VISIBLE
            iconArea.visibility = View.VISIBLE
        }
        val mediaView = ad.getAdMediaView(contentArea, contentArea.width)
        val adIconView = ad.adIconView
        Log.e("xia", "mediaView:$mediaView   adIconView:$adIconView   imageUrl:${ad.mainImageUrl}")

        if (adIconView != null) {
            iconArea.addView(adIconView)
        } else {
//            val imageView = ImageView(view.context)
//            imageView.setImageURI(Uri.parse(ad.iconImageUrl))
//            iconArea.addView(imageView)
        }

        val mediaLp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
        if (mediaView != null) {
            (mediaView.parent as? ViewGroup)?.removeView(mediaView)
            contentArea.addView(mediaView, mediaLp)
        } else {
//            val imageView = ImageView(view.context)
//            imageView.setImageURI(Uri.parse(ad.mainImageUrl))
//            iconArea.addView(imageView, mediaLp)
        }

        titleView.text = ad.title
        descView.text = ad.descriptionText
        ctaView.text = ad.callToActionText
        if (!TextUtils.isEmpty(ad.adFrom)) {
            adFromView.text = if (ad.adFrom != null) ad.adFrom else ""
            adFromView.visibility = View.VISIBLE
        } else {
            adFromView.visibility = View.GONE
        }
        clickView.clear()
        clickView.add(ctaView)
    }
}