package com.coffee.widget.shadow

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.coffee.base.utils.LogUtil
import com.coffee.widget.R

/**
 *
 * ShadowInnerConstraintLayout 的方案，是针对内部子View设置阴影，用到的api 是Paint.setShadowLayer()
 * 而该类实现的思路主要核心类是 BlurMaskFilter，它通过一个模糊的遮罩来实现
 * 几个重要参数：
 * mMaskRadius:扩散的半径
 * BlurMaskFilter.Blur.NORMAL：内外发光
 * BlurMaskFilter.Blur.SOLID：外发光
 * BlurMaskFilter.Blur.OUTER：仅发光部分可见
 * BlurMaskFilter.Blur.INNER：内发光
 *
 * @Description: 带阴影的ViewGroup
 * @Author: ly-zfensheng
 * @CreateDate: 2022/12/7 11:36
 */
class ShadowConstraintLayout: ConstraintLayout {

    private lateinit var paint: Paint
    private var rectF: RectF = RectF()

    private var xOffset = 0f
    private var yOffset = 0f
    private var shadowColor = 0
    private var shadowRadius = 0f
    private var shadowRoundRadius = 0f

    private lateinit var blurMaskFilter: BlurMaskFilter

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        context.apply {
            context.obtainStyledAttributes(attrs, R.styleable.ShadowConstraintLayout).apply {
                xOffset = getDimension(R.styleable.ShadowConstraintLayout_layout_xOffset, 0f)
                yOffset = getDimension(R.styleable.ShadowConstraintLayout_layout_yOffset, 0f)
                shadowRadius = getDimension(R.styleable.ShadowConstraintLayout_layout_shadowRadius, 0f)
                shadowColor = getColor(R.styleable.ShadowConstraintLayout_layout_shadowColor, 0)
                shadowRoundRadius = getDimension(R.styleable.ShadowConstraintLayout_layout_shadowRoundRadius, 0f)
                recycle()
            }
        }
        LogUtil.i("该阴影控件的 shadowRadius= $shadowRadius, shadowColor= $shadowColor, shadowRoundRadius= $shadowRoundRadius")
        setWillNotDraw(false)
        paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        blurMaskFilter= BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.SOLID)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        LogUtil.i("该阴影控件的 left= $left, top= $top, right= $right, bottom= $bottom")
        LogUtil.i("该阴影控件的 width= $w, height= $h")


        rectF.set(0f, 0f, w * 1f, h * 1f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (shadowRadius > 0 && shadowColor != Color.TRANSPARENT) {
            /*paint.setShadowLayer(shadowRadius, xOffset, yOffset, shadowColor)
            paint.color = shadowColor
            canvas?.drawRoundRect(rectF, shadowRoundRadius, shadowRoundRadius, paint)*/

            paint.maskFilter = blurMaskFilter
            paint.color = shadowColor
            canvas?.drawRoundRect(rectF, shadowRoundRadius, shadowRoundRadius, paint)
        }
    }

}