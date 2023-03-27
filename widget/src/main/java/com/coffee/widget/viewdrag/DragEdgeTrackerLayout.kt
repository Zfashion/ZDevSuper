package com.coffee.widget.viewdrag

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.customview.widget.ViewDragHelper

/**
 * @Description: 指定边界移动时，才可对子View捕获
 * 关键回调：
 *      1.ViewDragHelper.setEdgeTrackingEnabled()指定哪个边可以开启捕获
 *      2.ViewDragHelper.Callback()中的 onEdgeDragStarted()中，调用 ViewDragHelper.captureChildView() 指定要捕获的子View
 * @Author: ly-zfensheng
 * @CreateDate: 2022/9/13 17:38
 */
class DragEdgeTrackerLayout: ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initDrag()
    }

    private var mEdgeTrackerView: View? = null

    private lateinit var dragHelper: ViewDragHelper

    private val dragCallBack = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            //禁止捕获到该子控件
            return false
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

        /**
         * 在指定边缘开始滑动时，执行该回调
         */
        override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
            mEdgeTrackerView?.apply {
                //在此处指定要捕获的子View
                dragHelper.captureChildView(this, pointerId)
            }
        }

    }

    private fun initDrag() {
        dragHelper = ViewDragHelper.create(this, dragCallBack)
        //开启DragHelper 的边缘跟踪
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT) //此处指定右边边缘
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mEdgeTrackerView = getChildAt(0)
    }

}