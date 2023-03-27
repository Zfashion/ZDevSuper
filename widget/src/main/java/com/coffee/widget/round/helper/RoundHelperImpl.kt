package com.coffee.widget.round.helper

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.coffee.base.utils.LogUtil
import com.coffee.widget.R
import kotlin.math.min

/**
 *
 * @Author: fensheng
 * @CreateDate: 2022/02/21 16:27
 */
class RoundHelperImpl: RoundHelper {

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private var isCircle: Boolean = false

    private lateinit var xFerMode: Xfermode

    private lateinit var paint: Paint

    private lateinit var roundRectF: RectF
    private lateinit var strokeRectF: RectF
    private lateinit var originRectF: RectF

    private lateinit var path: Path
    private lateinit var roundPath: Path

    private lateinit var radii: FloatArray
    private lateinit var strokeRadii: FloatArray

    private var strokeSize: Float = 0f
    private var strokeColor: Int = Color.WHITE
    private var colorStateList: ColorStateList? = null

    private var topLeftRadius: Float = 0f
    private var bottomRightRadius: Float = 0f
    private var bottomLeftRadius: Float = 0f
    private var topRightRadius: Float = 0f

    override fun init(context: Context, attrs: AttributeSet?, view: View) {
        context.obtainStyledAttributes(attrs, R.styleable.RoundHelper).apply {
            val radius = getDimension(R.styleable.RoundHelper_round_radius, 0f)
            topLeftRadius = getDimension(R.styleable.RoundHelper_round_top_left_radius, radius)
            topRightRadius = getDimension(R.styleable.RoundHelper_round_top_right_radius, radius)
            bottomLeftRadius = getDimension(R.styleable.RoundHelper_round_bottom_left_radius, radius)
            bottomRightRadius = getDimension(R.styleable.RoundHelper_round_bottom_right_radius, radius)
            strokeSize = getDimension(R.styleable.RoundHelper_stroke_size, 0f)
            strokeColor = getColor(R.styleable.RoundHelper_stroke_color, Color.WHITE)
            colorStateList = getColorStateList(R.styleable.RoundHelper_stroke_color)
            isCircle = getBoolean(R.styleable.RoundHelper_is_circle, false)
            recycle()
        }
        xFerMode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        path = Path()
        roundPath = Path()
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun onSizeChanged(width: Int, height: Int) {
        LogUtil.d("width = $width, height = $height")
        viewWidth = width
        viewHeight = height

        onTakeRadius()
        onTakeRectF()
    }

    override fun preDraw(canvas: Canvas) {
        canvas.saveLayer(roundRectF, null)
    }

    //TODO：如果在onDraw()中执行这部分的裁切逻辑是不生效的，必须在draw()中才能生效，具体原因暂未深入研究，先做个mark
    override fun draw(canvas: Canvas, drawableState: IntArray) {
        drawRound(canvas)
        drawStroke(canvas, drawableState)
        LogUtil.d("draw() end")
    }

    private fun onTakeRectF() {
        if (this::originRectF.isInitialized.not()) originRectF = RectF()
        originRectF.set(0f, 0f, viewWidth * 1f, viewHeight * 1f)

        if (this::roundRectF.isInitialized.not()) roundRectF = RectF()
        roundRectF.set(strokeSize, strokeSize, viewWidth - strokeSize, viewHeight - strokeSize)

        //同计算边框半径的逻辑一样
        if (this::strokeRectF.isInitialized.not()) strokeRectF = RectF()
        val halfStroke = strokeSize / 2
        strokeRectF.set(halfStroke, halfStroke, viewWidth - halfStroke, viewHeight - halfStroke)
    }

    private fun onTakeRadius() {
        if (isCircle) {
            val radius = min(viewWidth, viewHeight) * 1f / 2
            topLeftRadius = radius
            topRightRadius = radius
            bottomLeftRadius = radius
            bottomRightRadius = radius
        }
        val topLeftXY = topLeftRadius - strokeSize
        val topRightXY = topRightRadius - strokeSize
        val bottomLeftXY = bottomLeftRadius - strokeSize
        val bottomRightXY = bottomRightRadius - strokeSize
        radii = floatArrayOf(topLeftXY, topLeftXY, topRightXY, topRightXY, bottomLeftXY, bottomLeftXY, bottomRightXY, bottomRightXY)

        //边框的半径，得除以一半，因为画出来的边框实际上是沿着线中间开始画出来的，因此占线宽度一半
        val strokeTopLeftXY = topLeftRadius - strokeSize / 2
        val strokeTopRightXY = topRightRadius - strokeSize / 2
        val strokeBottomLeftXY = bottomLeftRadius - strokeSize / 2
        val strokeBottomRightXY = bottomRightRadius - strokeSize / 2
        strokeRadii = floatArrayOf(strokeTopLeftXY, strokeTopLeftXY, strokeTopRightXY, strokeTopRightXY, strokeBottomLeftXY, strokeBottomLeftXY, strokeBottomRightXY, strokeBottomRightXY)
    }


    private fun drawStroke(canvas: Canvas, drawableState: IntArray) {
        if (strokeSize == 0f) return

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeSize
        if (colorStateList != null) {
            strokeColor = colorStateList!!.getColorForState(drawableState, colorStateList!!.defaultColor)
        }
        paint.color = strokeColor

        path.reset()
        path.addRoundRect(strokeRectF, strokeRadii, Path.Direction.CCW)
        canvas.drawPath(path, paint)
    }

    private fun drawRound(canvas: Canvas) {
        paint.reset()
        path.reset()
        roundPath.reset()

        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.xfermode = xFerMode

        roundPath.addRoundRect(roundRectF, radii, Path.Direction.CCW)
        path.addRect(originRectF, Path.Direction.CCW)
        //将两路径相切出外层的圆角模板
        path.op(roundPath, Path.Op.XOR)

        canvas.drawPath(path, paint)

        paint.xfermode = null
        canvas.restore()
    }

}