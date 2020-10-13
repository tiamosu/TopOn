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
import com.beemans.topon.ext.loadAdImage

/**
 * @author tiamosu
 * @date 2020/7/2.
 */
class DefaultNativeAdRender : BaseNativeAdRender() {
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
        clickView.clear()

        val flContentArea: FrameLayout = view.findViewById(R.id.nativeAdItem_flAd)
        val mediaView: View? = ad.getAdMediaView(flContentArea, flContentArea.width)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
        //个性化模板
        if (ad.isNativeExpress) {
            if (mediaView != null) {
                if (flContentArea.childCount > 0) {
                    flContentArea.removeAllViews()
                }
                flContentArea.addView(mediaView, params)
            }
            return
        }

        val tvTitle: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvTitle)
        val tvDesc: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvDesc)
        val tvCta: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvInstall)
        val tvAdFrom: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvFrom)
        val flIconArea: FrameLayout = view.findViewById(R.id.nativeAdItem_flImage)
        val ivChoiceLogo: AppCompatImageView = view.findViewById(R.id.nativeAdItem_ivChoiceLogo)

        //获取广告标题
        tvTitle.isVisible = true
        tvTitle.text = ad.title

        //获取广告描述
        tvDesc.isVisible = true
        tvDesc.text = ad.descriptionText

        //获取广告CTA按钮文字
        tvCta.isVisible = true
        tvCta.text = ad.callToActionText

        flIconArea.isVisible = true
        if (flIconArea.childCount > 0) {
            flIconArea.removeAllViews()
        }
        //获取广告IconView
        val adIconView = ad.adIconView
        if (adIconView != null) {
            flIconArea.addView(adIconView)
        } else {
            Log.e("xia", "iconImageUrl:${ad.iconImageUrl}")

            //获取广告图标url
            AppCompatImageView(view.context).apply {
                loadAdImage(ad.iconImageUrl)
            }.let(flIconArea::addView)
        }

        //获取广告商的标识的图标url
        if (ad.adChoiceIconUrl?.isNotBlank() == true) {
            ivChoiceLogo.isVisible = true
            ivChoiceLogo.loadAdImage(ad.adChoiceIconUrl)
        }

        //获取广告来源
        tvAdFrom.isVisible = ad.adFrom?.isNotBlank() == true
        if (tvAdFrom.isVisible) {
            tvAdFrom.text = ad.adFrom
        }

        //获取广告大图的渲染容器（仅部分广告平台会存在），有可能是静态图和视频。
        //参数描述：view：广告父容器, width: MediaView的宽度配置 (目前仅Inmobi需要这两个参数，其他可以传null)
        if (flContentArea.childCount > 0) {
            flContentArea.removeAllViews()
        }
        if (mediaView != null) {
            flContentArea.addView(mediaView, params)
        } else {
            Log.e("xia", "mainImageUrl:${ad.mainImageUrl}")

            //获取大图Url
            AppCompatImageView(view.context).apply {
                loadAdImage(ad.mainImageUrl)
                layoutParams = params
                flContentArea.addView(this, params)
            }
        }

        clickView.add(tvTitle)
        clickView.add(tvDesc)
        clickView.add(tvCta)
    }
}