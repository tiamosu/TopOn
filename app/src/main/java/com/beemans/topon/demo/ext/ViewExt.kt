package com.beemans.topon.demo.ext

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.widget.NestedScrollView
import com.blankj.utilcode.util.ScreenUtils

private val scrollViewRect by lazy { Rect() }

/**
 * 判断 View 在 NestedScrollView 中是否可见
 */
fun setVisibleLocalListener(
    views: List<View>,
    scrollView: NestedScrollView,
    isAddScrollListener: Boolean = false,
    onCallback: (view: View, isVisible: Boolean) -> Unit = { _, _ -> }
) {
    scrollView.getHitRect(scrollViewRect)

    fun viewVisibleLocal() {
        views.forEach { view ->
            val isVisible = view.getLocalVisibleRect(scrollViewRect) //子控件至少有一个像素在可视范围内
            onCallback.invoke(view, isVisible)
        }
    }

    if (isAddScrollListener) {
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
            viewVisibleLocal()
        })
    } else {
        viewVisibleLocal()
    }
}

/**
 * 判断 View 在屏幕中是否可见
 */
fun View.isVisibleLocal(onCallback: () -> Unit) {
    val rect = Rect(0, 0, ScreenUtils.getAppScreenWidth(), ScreenUtils.getAppScreenHeight())
    val location = IntArray(2)
    getLocationOnScreen(location)
    if (getLocalVisibleRect(rect)) {
        onCallback.invoke()
        return
    }
    addOnGlobalLayoutListener {
        if (getLocalVisibleRect(rect)) {
            onCallback.invoke()
            return@addOnGlobalLayoutListener
        }
    }
}

/**
 * 视图变化监听
 */
fun View.addOnGlobalLayoutListener(onGlobalLayoutListener: () -> Unit) {
    val vto = viewTreeObserver
    if (!vto.isAlive) {
        return
    }
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        @Suppress("DEPRECATION")
        override fun onGlobalLayout() {
            val currentVto = viewTreeObserver
            if (currentVto.isAlive) {
                currentVto.removeOnGlobalLayoutListener(this)
            }
            onGlobalLayoutListener()
        }
    })
}