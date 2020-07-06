package com.beemans.topon.nativead

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.anythink.nativead.unitgroup.api.CustomNativeAd
import com.beemans.topon.R
import com.beemans.topon.utils.Utils

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class NativeAdRender : BaseNativeAdRender() {
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
        val tvTitle: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvTitle)
        val tvDesc: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvDesc)
        val tvCta: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvInstall)
        val tvFrom: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvFrom)
        val flAd: FrameLayout = view.findViewById(R.id.nativeAdItem_flAd)
        val flIcon: FrameLayout = view.findViewById(R.id.nativeAdItem_flImage)
        val ivChoiceLogo: AppCompatImageView = view.findViewById(R.id.nativeAdItem_ivChoiceLogo)

        if (flAd.childCount > 0) {
            flAd.removeAllViews()
        }
        if (flIcon.childCount > 0) {
            flIcon.removeAllViews()
        }

        //个性化模板
        val isNativeExpress = ad.isNativeExpress
        tvTitle.isVisible = !isNativeExpress
        tvDesc.isVisible = !isNativeExpress
        tvCta.isVisible = !isNativeExpress
        flIcon.isVisible = !isNativeExpress
        ivChoiceLogo.isVisible = !isNativeExpress

        if (tvTitle.isVisible) {
            //获取广告标题
            tvTitle.text = ad.title
        }
        if (tvDesc.isVisible) {
            //获取广告描述
            tvDesc.text = ad.descriptionText
        }
        if (tvCta.isVisible) {
            //获取广告CTA按钮文字
            tvCta.text = ad.callToActionText
        }
        if (flIcon.isVisible) {
            //获取广告IconView
            val adIconView = ad.adIconView
            if (adIconView != null) {
                flIcon.addView(adIconView)
            } else {
                val iconView = AppCompatImageView(view.context)
                flIcon.addView(iconView)
                //获取广告图标url
                Utils.loadImage(ad.iconImageUrl, iconView)
            }
        }
        if (ivChoiceLogo.isVisible) {
            //获取广告商的标识的图标url
            Utils.loadImage(ad.adChoiceIconUrl, ivChoiceLogo)
        }

        //获取广告来源
        tvFrom.isVisible = ad.adFrom?.isNotBlank() == true
        if (tvFrom.isVisible) {
            tvFrom.text = ad.adFrom
        }

        //获取广告大图的渲染容器（仅部分广告平台会存在），有可能是静态图和视频。
        //参数描述：view：广告父容器, width: MediaView的宽度配置 (目前仅Inmobi需要这两个参数，其他可以传null)
        val mediaView = ad.getAdMediaView(flAd, flAd.width)
        Log.e("xia", "mediaView:$mediaView   imageUrl:${ad.mainImageUrl}")

        val mediaLp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
        if (flAd.childCount > 0) {
            flAd.removeAllViews()
        }
        if (mediaView != null) {
            flAd.addView(mediaView, mediaLp)
        } else {
            val imageView = AppCompatImageView(view.context)
            flAd.addView(imageView, mediaLp)
            //获取大图Url
            Utils.loadImage(ad.mainImageUrl, imageView)
        }

        clickView.clear()
        clickView.add(tvCta)
    }
}