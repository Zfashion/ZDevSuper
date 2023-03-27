package com.coffee.widget.motionlayout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.coffee.base.utils.LogUtil

/**
 *
 * @Description: DrawerLayout 的子View Layout，主要将DrawerLayout 的滑动偏移值传递给 MotionLayout 的 progress
 * @Author: ly-zfensheng
 * @CreateDate: 2022/8/23 18:38
 */
open class DrawerContent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): MotionLayout(context, attrs, defStyleAttr), DrawerLayout.DrawerListener {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? DrawerLayout)?.addDrawerListener(this)
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        progress = slideOffset
    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerStateChanged(newState: Int) {
    }

}