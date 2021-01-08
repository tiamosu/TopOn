package com.beemans.topon.nativead

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
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
    private var backgroundColor: Int? = null

    fun setBackground(@ColorInt backgroundColor: Int) {
        this.backgroundColor = backgroundColor
    }

    /**
     * 用于创建自定义的Native广告布局的View
     */
    override fun createView(context: Context, networkType: Int): View {
        if (developView == null) {
            developView = View.inflate(context, R.layout.default_native_ad_item, null)
        }
        (developView?.parent as? ViewGroup)?.removeView(developView)
        return developView!!
    }

    /**
     * 用于实现广告内容渲染的方法，其中customNativeAd是广告素材的对象，可提供素材进行渲染
     */
    override fun renderAdView(view: View, ad: CustomNativeAd) {
        val rlContent: RelativeLayout = view.findViewById(R.id.nativeAdItem_rlContent)
        val flContentArea: FrameLayout = view.findViewById(R.id.nativeAdItem_flAd)
        val tvTitle: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvTitle)
        val tvDesc: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvDesc)
        val tvCta: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvInstall)
        val tvAdFrom: AppCompatTextView = view.findViewById(R.id.nativeAdItem_tvFrom)
        val flIconArea: FrameLayout = view.findViewById(R.id.nativeAdItem_flImage)
        val ivChoiceLogo: AppCompatImageView = view.findViewById(R.id.nativeAdItem_ivChoiceLogo)
        val ivLogo: AppCompatImageView = view.findViewById(R.id.nativeAdItem_ivLogo)

        backgroundColor?.let { rlContent.setBackgroundColor(it) }

        val isNativeExpress = ad.isNativeExpress
        tvTitle.isVisible = !isNativeExpress
        tvDesc.isVisible = !isNativeExpress
        tvCta.isVisible = !isNativeExpress
        flIconArea.isVisible = !isNativeExpress
        ivChoiceLogo.isVisible = !isNativeExpress
        ivLogo.isVisible = !isNativeExpress

        val mediaView: View? = ad.getAdMediaView(flContentArea, flContentArea.width)
        val params = FrameLayout.LayoutParams(-1, -2)

        //个性化模板
        if (isNativeExpress) {
            if (mediaView != null) {
                if (flContentArea.childCount > 0) {
                    flContentArea.removeAllViews()
                }
                flContentArea.addView(mediaView, params)
            }
            return
        }

        //获取广告标题
        if (tvTitle.isVisible) {
            tvTitle.text = ad.title
        }

        //获取广告描述
        if (tvDesc.isVisible) {
            tvDesc.text = ad.descriptionText
        }

        //获取广告CTA按钮文字
        if (tvCta.isVisible) {
            tvCta.text = ad.callToActionText
        }

        //获取广告图标
        if (flIconArea.isVisible) {
            if (flIconArea.childCount > 0) {
                flIconArea.removeAllViews()
            }
            val adIconView: View? = ad.adIconView
            if (adIconView != null) {
                flIconArea.addView(adIconView)
            } else {
                AppCompatImageView(view.context).apply {
                    loadAdImage(ad.iconImageUrl)
                }.let(flIconArea::addView)
            }
        }

        //获取广告商的标识的图标url
        ivChoiceLogo.isVisible = ad.adChoiceIconUrl?.isNotBlank() == true
        if (ivChoiceLogo.isVisible) {
            ivChoiceLogo.loadAdImage(ad.adChoiceIconUrl)
        }

        //获取广告来源
        tvAdFrom.isVisible = ad.adFrom?.isNotBlank() == true
        if (tvAdFrom.isVisible) {
            tvAdFrom.text = ad.adFrom
        }

        //获取AdLogo的Bitmap
        if (ivLogo.isVisible) {
            ivLogo.setImageBitmap(ad.adLogo)
        }

        //获取广告大图的渲染容器（仅部分广告平台会存在），有可能是静态图和视频。
        //参数描述：view：广告父容器, width: MediaView的宽度配置 (目前仅Inmobi需要这两个参数，其他可以传null)
        if (flContentArea.childCount > 0) {
            flContentArea.removeAllViews()
        }
        if (mediaView != null) {
            flContentArea.addView(mediaView, params)
        } else {
            //获取大图Url
            AppCompatImageView(view.context).apply {
                loadAdImage(ad.mainImageUrl)
                layoutParams = params
                flContentArea.addView(this, params)
            }
        }

        clickView.apply {
            clear()
            add(flContentArea)
            add(flIconArea)
            add(tvTitle)
            add(tvDesc)
            add(tvCta)
            add(ivLogo)
        }
    }
}