package com.coffee.widget.motionlayout

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.updateBounds
import androidx.core.view.updateLayoutParams
import com.coffee.base.ext.setRoundCorner
import com.coffee.base.ext.viewTags
import com.coffee.base.utils.LogUtil

/**
 *
 * @Description: 带有Motion辅助的Transition 进度值，并改变外部的圆角
 * @Author: ly-zfensheng
 * @CreateDate: 2022/8/30 17:37
 */
class MotionCoordinatorLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr), MotionLayout.TransitionListener {

    private val defRoundScale = 200

    private val mOutlineProvider: MotionCornerOutlineProvider by lazy { MotionCornerOutlineProvider(Rect(0, 0, this.width, this.height)) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        takeIf { parent is MotionLayout }?.apply {
            (parent as MotionLayout).addTransitionListener(this)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        makeViewOutline()
    }

    private fun makeViewOutline() {
        clipToOutline = true
        outlineProvider = mOutlineProvider
    }

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
    }

    override fun onTransitionChange(
        motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float
    ) {
        mOutlineProvider.mRadius = progress * defRoundScale
        invalidateOutline()

        LogUtil.d("此时进度为 $progress")
    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
    }

    override fun onTransitionTrigger(
        motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float
    ) {}

}

class MotionCornerOutlineProvider(
    private val rect: Rect
) : ViewOutlineProvider() {

    var mRadius: Float = 0f

    override fun getOutline(view: View?, outline: Outline?) {
        outline?.setRoundRect(rect, mRadius)
    }
}
