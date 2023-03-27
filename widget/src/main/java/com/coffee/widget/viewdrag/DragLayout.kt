package com.coffee.widget.viewdrag

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.customview.widget.ViewDragHelper
import kotlin.math.max
import kotlin.math.min

/**
 *
 * @Description: ViewDragHelper 的简单使用
 * 可让所有子View 跟随手指滑动
 * @Author: ly-zfensheng
 * @CreateDate: 2022/9/9 15:29
 */
class DragLayout: ConstraintLayout {

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        //1.创建ViewDragHelper实例
        initDrag()
    }

    private lateinit var dragHelper: ViewDragHelper

    private lateinit var mDragView: View

    //2.创建ViewDragHelper.Callback的实例
    private val dragCallback = object: ViewDragHelper.Callback() {

        /**
         * @param child 捕获的子View
         * @param pointerId 触摸手指位
         * @return 返回true表示该子View可被捕获
         */
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            mDragView = child
            return true
        }

        /**
         * 可对捕获的子View在水平方向上进行边界控制
         * @param left 表示子View即将移动到的x轴方向上的位置
         */
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            val minX = paddingLeft
            val maxX = width - paddingRight - mDragView.width
            return if (left in minX..maxX) {
                left
            } else if (left < minX){
                minX
            } else {
                maxX
            }
        }

        /**
         * 可对捕获的子View在垂直方向上进行边界控制
         * @param top 表示子View即将移动到的y轴方向上的位置
         */
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val minY = paddingTop
            val maxY = height - paddingBottom - mDragView.height
            return if (top in minY..maxY) {
                top
            } else if (top < minY) {
                minY
            } else {
                maxY
            }
        }



    }

    private fun initDrag() {
        dragHelper = ViewDragHelper.create(this, dragCallback)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        //3.调用ViewDragHelper的 shouldInterceptTouchEvent()
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //4.调用ViewDragHelper的 processTouchEvent()
        dragHelper.processTouchEvent(event)
        return true
    }

}