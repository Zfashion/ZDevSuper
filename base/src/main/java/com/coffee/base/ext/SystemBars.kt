package com.coffee.base.ext

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.core.view.*
import androidx.core.view.WindowInsetsCompat.Type
import androidx.fragment.app.Fragment
import com.coffee.base.R
import com.coffee.base.utils.LogUtil


/**
 *
 * @Description: 系统条扩展方法
 * @Author: ly-zfensheng
 * @CreateDate: 2022/8/27 14:47
 */


private var View.isAddedMarginTop: Boolean? by viewTags(R.id.tag_is_added_margin_top)
private var View.isAddedPaddingTop: Boolean? by viewTags(R.id.tag_is_added_padding_top)
private var View.isAddedMarginBottom: Boolean? by viewTags(R.id.tag_is_added_margin_bottom)


/******************************************
 * Fragment 调用沉浸式
 ******************************************/
fun Fragment.immersionStatusBar(lightMode: Boolean = true) {
    activity?.immersionStatusBar(lightMode)
}

/*******************************************
 * Activity 调用沉浸式
 ******************************************/
fun Activity.immersionStatusBar(lightMode: Boolean = true) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.decorView.windowInsetsControllerCompat?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    transparentStatusBar()
    isLightStatusBar = lightMode
    contentView.addNavigationBarHeightToMarginBottom(navigationBarHeight)
}

/**
 * 针对自己添加的ToolBar适配状态栏高度
 */
fun View.adapterStatusBarTopPadding(statusBarHeight: Int) {
    addStatusBarHeightToPaddingTop(statusBarHeight)
//    addStatusBarHeightToMarginTop(statusBarHeight)
}



/*******************************************
 * 状态条颜色设置
 ******************************************/
inline var Fragment.isLightStatusBar: Boolean
    get() = activity?.isLightStatusBar == true
    set(value) {
        view?.post { activity?.isLightStatusBar = value }
    }

inline var Activity.isLightStatusBar: Boolean
    get() = window.decorView.windowInsetsControllerCompat?.isAppearanceLightStatusBars == true
    set(value) {
        window.decorView.windowInsetsControllerCompat?.isAppearanceLightStatusBars = value
    }

inline var Fragment.statusBarColor: Int
    get() = activity?.statusBarColor ?: -1
    set(value) {
        activity?.statusBarColor = value
    }

@setparam:ColorInt
inline var Activity.statusBarColor: Int
    get() = window.statusBarColor
    set(value) {
        window.statusBarColor = value
    }

fun Fragment.transparentStatusBar() {
    activity?.transparentStatusBar()
}

fun Activity.transparentStatusBar() {
    statusBarColor = Color.TRANSPARENT
}



/*******************************************
 * 状态条可见状态
 ******************************************/
inline var Fragment.isStatusBarVisible: Boolean
    get() = activity?.isStatusBarVisible == true
    set(value) {
        activity?.isStatusBarVisible = value
    }

inline var Activity.isStatusBarVisible: Boolean
    get() = window.decorView.isStatusBarVisible
    set(value) {
        window.decorView.isStatusBarVisible = value
    }

inline var View.isStatusBarVisible: Boolean
    get() = rootWindowInsetsCompat?.isVisible(Type.statusBars()) == true
    set(value) {
        windowInsetsControllerCompat?.run {
            if (value) show(Type.statusBars()) else hide(Type.statusBars())
        }
    }



/*******************************************
 * 状态栏高度设置
 ******************************************/
inline val Activity.statusBarHeight: Int
    get() = window.decorView.rootWindowInsetsCompat?.getInsets(Type.statusBars())?.top
        ?: application.resources.getIdentifier("status_bar_height", "dimen", "android")
            .let { if (it > 0) application.resources.getDimensionPixelSize(it) else 0 }


/**
 * 给控件的顶部外边距增加状态栏高度
 */
fun View.addStatusBarHeightToMarginTop(statusBarHeight: Int) = post {
    if (isStatusBarVisible && isAddedMarginTop != true) {
        LogUtil.d("添加外边距，高度为状态栏高度")
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(top = topMargin + statusBarHeight)
            isAddedMarginTop = true
        }
    }
}

/**
 * 给控件的顶部外边距减少状态栏高度
 */
fun View.subtractStatusBarHeightToMarginTop(statusBarHeight: Int) = post {
    if (isStatusBarVisible && isAddedMarginTop == true) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(top = topMargin - statusBarHeight)
            isAddedMarginTop = false
        }
    }
}

/**
 * 给控件的顶部内边距增加状态栏高度
 */
fun View.addStatusBarHeightToPaddingTop(statusBarHeight: Int) = post {
    if (isAddedPaddingTop != true) {
        updatePadding(top = paddingTop + statusBarHeight)
        updateLayoutParams {
            LogUtil.d("measuredHeight is $measuredHeight, statusBarHeight is $statusBarHeight")
            height = measuredHeight + statusBarHeight
            LogUtil.d("height is $height")
        }
        isAddedPaddingTop = true
    }
}

/**
 * 给控件的顶部内边距减少状态栏高度
 */
fun View.subtractStatusBarHeightToPaddingTop(statusBarHeight: Int) = post {
    if (isAddedPaddingTop == true) {
        updatePadding(top = paddingTop - statusBarHeight)
        updateLayoutParams {
            height = measuredHeight - statusBarHeight
        }
        isAddedPaddingTop = false
    }
}





/*******************************************
 * 	导航栏颜色, 可见性，亮色模式等的设置和获取
 ******************************************/
inline var Fragment.isLightNavigationBar: Boolean
    get() = activity?.isLightNavigationBar == true
    set(value) {
        activity?.isLightNavigationBar = value
    }

inline var Activity.isLightNavigationBar: Boolean
    get() = window.decorView.windowInsetsControllerCompat?.isAppearanceLightNavigationBars == true
    set(value) {
        window.decorView.windowInsetsControllerCompat?.isAppearanceLightNavigationBars = value
    }

fun Fragment.transparentNavigationBar() {
    activity?.transparentNavigationBar()
}

fun Activity.transparentNavigationBar() {
    navigationBarColor = Color.TRANSPARENT
}

inline var Fragment.navigationBarColor: Int
    get() = activity?.navigationBarColor ?: -1
    set(value) {
        activity?.navigationBarColor = value
    }

inline var Activity.navigationBarColor: Int
    get() = window.navigationBarColor
    set(value) {
        window.navigationBarColor = value
    }

inline var Fragment.isNavigationBarVisible: Boolean
    get() = activity?.isNavigationBarVisible == true
    set(value) {
        activity?.isNavigationBarVisible = value
    }

inline var Activity.isNavigationBarVisible: Boolean
    get() = window.decorView.isNavigationBarVisible
    set(value) {
        window.decorView.isNavigationBarVisible = value
    }

inline var View.isNavigationBarVisible: Boolean
    get() = rootWindowInsetsCompat?.isVisible(Type.navigationBars()) == true
    set(value) {
        windowInsetsControllerCompat?.run {
            if (value) show(Type.navigationBars()) else hide(Type.navigationBars())
        }
    }

/*******************************************
 * 	导航栏高度设置
 ******************************************/
inline val Activity.navigationBarHeight: Int
    get() = window.decorView.rootWindowInsetsCompat?.getInsets(Type.navigationBars())?.bottom
        ?: application.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            .let { if (it > 0) application.resources.getDimensionPixelSize(it) else 0 }

/**
 * 给控件的底部外边距增加虚拟导航栏高度
 */
fun View.addNavigationBarHeightToMarginBottom(navigationBarHeight: Int) = post {
    if (isNavigationBarVisible && isAddedMarginBottom != true) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(bottom = bottomMargin + navigationBarHeight)
            isAddedMarginBottom = true
        }
    }
}

/**
 * 给控件的底部外边距减少虚拟导航栏高度
 */
fun View.subtractNavigationBarHeightToMarginBottom(navigationBarHeight: Int) = post {
    if (isNavigationBarVisible && isAddedMarginBottom == true) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(bottom = bottomMargin - navigationBarHeight)
            isAddedMarginBottom = false
        }
    }
}