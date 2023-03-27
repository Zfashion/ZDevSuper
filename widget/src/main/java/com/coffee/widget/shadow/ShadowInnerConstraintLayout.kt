package com.coffee.widget.shadow

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.coffee.widget.R


/**
 *
 * @Description: 该View的阴影效果是针对子View的
 * @Author: ly-zfensheng
 * @CreateDate: 2022/12/7 10:59
 */
class ShadowInnerConstraintLayout : ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initPaint()
    }

    private lateinit var paint: Paint
    var rectF = RectF()

    private fun initPaint() {
        setWillNotDraw(false)
        paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val childCount = childCount
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            val layoutParams: ViewGroup.LayoutParams = child.layoutParams
            if (layoutParams is LayoutParams) {
                if (layoutParams.shadowRadius > 0 && layoutParams.shadowColor != Color.TRANSPARENT) {
                    rectF.left = child.left * 1f
                    rectF.right = child.right * 1f
                    rectF.top = child.top * 1f
                    rectF.bottom = child.bottom * 1f
                    paint.style = Paint.Style.FILL
                    paint.setShadowLayer(
                        layoutParams.shadowRadius,
                        layoutParams.xOffset,
                        layoutParams.yOffset,
                        layoutParams.shadowColor
                    )
                    paint.color = layoutParams.shadowColor
                    canvas.drawRoundRect(rectF, layoutParams.shadowRoundRadius, layoutParams.shadowRoundRadius, paint)
                }
            }
        }
    }

    class LayoutParams : ConstraintLayout.LayoutParams {

        var xOffset = 0f
        var yOffset = 0f
        var shadowColor = 0
        var shadowRadius = 0f
        var shadowRoundRadius = 0f

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.LayoutParams?) : super(source)

        constructor(source: LayoutParams) : super(source) {
            xOffset = source.xOffset
            yOffset = source.yOffset
            shadowColor = source.shadowColor
            shadowRadius = source.shadowRadius
            shadowRoundRadius = source.shadowRoundRadius
        }

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            context.obtainStyledAttributes(attrs, R.styleable.ShadowConstraintLayout).apply {
                xOffset = getDimension(R.styleable.ShadowConstraintLayout_layout_xOffset, 0f)
                yOffset = getDimension(R.styleable.ShadowConstraintLayout_layout_yOffset, 0f)
                shadowRadius = getDimension(R.styleable.ShadowConstraintLayout_layout_shadowRadius, 0f)
                shadowColor = getColor(R.styleable.ShadowConstraintLayout_layout_shadowColor, 0)
                shadowRoundRadius = getDimension(R.styleable.ShadowConstraintLayout_layout_shadowRoundRadius, 0f)
                recycle()
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(this.context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is LayoutParams
    }
}