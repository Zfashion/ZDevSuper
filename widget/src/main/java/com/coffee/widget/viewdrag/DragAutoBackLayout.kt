package com.coffee.widget.viewdrag

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.customview.widget.ViewDragHelper
import com.coffee.base.utils.LogUtil

/**
 *
 * @Description: 子View移动后，重新返回到原来的位置
 * 关键回调：
 *      1.ViewDragHelper.Callback()中的 onViewReleased();
 *      2.onLayout()中记录原始坐标
 *      3.computeScroll()中调用 ViewDragHelper.continueSettling(true)处理并 invalidate()
 * @Author: ly-zfensheng
 * @CreateDate: 2022/9/13 14:02
 */
class DragAutoBackLayout: ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initDrag()
    }

    private var mDragView: View? = null

    //指定某个子View会自动返回原位置
    private var mAutoBackDragView: View? = null
    //记录原来的坐标位置
    private val backViewPoint by lazy { Point() }

    private lateinit var dragHelper: ViewDragHelper

    private val dragCallback = object: ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return mDragView?.id == child.id || mAutoBackDragView?.id == child.id
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }


        //实现关键回调，当手指放开子View，便会执行该回调
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (releasedChild.id == mAutoBackDragView?.id) {
                //第一种：可以调用ViewDragHelper的 smoothSlideViewTo(child View, finalLeft Int, finalTop: Int)
                dragHelper.smoothSlideViewTo(releasedChild, backViewPoint.x, backViewPoint.y)

                //第二种，调用ViewDragHelper的 settleCapturedViewAt(finalLeft Int, finalTop: Int)
//                dragHelper.settleCapturedViewAt(backViewPoint.x, backViewPoint.y)

                //接着调用invalidate()，必须调用，调用后会执行回调 computeScroll()
                invalidate()
            }
        }

    }

    private fun initDrag() {
        dragHelper = ViewDragHelper.create(this, dragCallback)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        LogUtil.d("回调onLayout, 记录AutoBackDragView的初始坐标")
        //记录初始坐标
        mAutoBackDragView?.apply {
            backViewPoint.x = this.left
            backViewPoint.y = this.top
        }
    }

    //实现关键回调，通过ViewDragHelper.continueSettling(true)，并在允许条件下调用 invalidate()
    override fun computeScroll() {
        super.computeScroll()
        if (dragHelper.continueSettling(true)) {
            LogUtil.d("回调computeScroll()")
            invalidate()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        LogUtil.d("xml加载完成")
        mDragView = getChildAt(0)
        mAutoBackDragView = getChildAt(1)
    }

}