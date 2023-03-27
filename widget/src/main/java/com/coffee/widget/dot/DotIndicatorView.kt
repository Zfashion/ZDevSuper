package com.coffee.widget.dot

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.coffee.base.utils.DensityUtil
import com.coffee.base.utils.LogUtil
import com.coffee.widget.R

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2022/12/15 18:01
 */
class DotIndicatorView: View {

    private lateinit var mPaint: Paint

    private var count: Int = 0

    //当前选择的下标
    private var currentIndex: Int = 0

    private var radius: Float = -1f

    private var dotVerticalSpacing: Float = -1f

    private var chooseDotHeight: Float = -1f

    private var chooseDotCorner: Float = -1f

    private var chooseColor = Color.GREEN
    private var unChooseColor = Color.BLACK

    private var centerX: Float = -1f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.DotIndicatorView).apply {
            count = getInteger(R.styleable.DotIndicatorView_dot_count, 0)
            radius = getDimension(R.styleable.DotIndicatorView_dot_radius, DensityUtil.dp2pxF(4f))
            dotVerticalSpacing = getDimension(R.styleable.DotIndicatorView_dot_vertical_spacing, DensityUtil.dp2pxF(14f))
            chooseDotHeight = getDimension(R.styleable.DotIndicatorView_dot_selected_height, DensityUtil.dp2pxF(22f))
            chooseDotCorner = getDimension(R.styleable.DotIndicatorView_dot_selected_corner, DensityUtil.dp2pxF(6f))
            chooseColor = getColor(R.styleable.DotIndicatorView_dot_selected_color, Color.parseColor("#ff7c25"))
            unChooseColor = getColor(R.styleable.DotIndicatorView_dot_unselected_color, Color.parseColor("#696c72"))
            recycle()
        }
        mPaint = Paint().apply {
            isAntiAlias = true
            color = unChooseColor
            style = Paint.Style.FILL
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minHeight = (count - 1) * radius * 2 + (count - 1) * dotVerticalSpacing + chooseDotHeight + paddingTop + paddingBottom
        val minWidth = radius * 2 + paddingLeft + paddingRight
        val resultHeight = resolveSize(minHeight.toInt(), heightMeasureSpec)
        val resultWidth = resolveSize(minWidth.toInt(), widthMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
        LogUtil.i("resultWidth= $resultWidth, resultHeight= $resultHeight")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w * 1f / 2
        LogUtil.i("onSizeChanged(), centerX= $centerX")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (i in 0 until count) {
            if (i == currentIndex) {
                //选中的点
                drawChooseDot(canvas, i)
            } else if (i > currentIndex) {
                //在选中位之后的圆点
                drawAfterChooseDot(canvas, i)
            } else {
                //选中位之前的圆点
                drawUnChooseDot(canvas, i)
            }
        }
    }

    fun updateChooseIndex(index: Int) {
        if (index == currentIndex) return

        currentIndex = index
        invalidate()
    }

    private fun drawAfterChooseDot(canvas: Canvas?, index: Int) {
        mPaint.color = unChooseColor
        canvas?.let {
            val dotY =  paddingTop + (index * (radius * 2 + dotVerticalSpacing)) + chooseDotHeight - radius
            LogUtil.i("当前第${index+1}个，在选中点之后的圆点，y轴坐标：$dotY")
            //画圆点
            it.drawCircle(centerX, dotY, radius, mPaint)
        }
    }

    private fun drawUnChooseDot(canvas: Canvas?, index: Int) {
        mPaint.color = unChooseColor
        canvas?.let {
            val dotY = paddingTop + radius + (index * (radius * 2 + dotVerticalSpacing))
            LogUtil.i("当前第${index+1}个，在选中点之前的圆点，y轴坐标：$dotY")
            //画圆点
            it.drawCircle(centerX, dotY, radius, mPaint)
        }
    }

    private fun drawChooseDot(canvas: Canvas?, index: Int) {
        mPaint.color = chooseColor
        canvas?.let {
            val dotY = paddingTop + chooseDotHeight + (index * (radius * 2 + dotVerticalSpacing))
            val left = centerX - radius
            val right = centerX + radius
            val rectF = RectF(left, dotY - chooseDotHeight, right, dotY)
            LogUtil.i("选中了第${index+1}个")
            //画矩形椭圆点
            it.drawRoundRect(rectF, chooseDotCorner, chooseDotCorner, mPaint)
        }
    }

}