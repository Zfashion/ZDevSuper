package com.coffee.widget.weather

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.coffee.base.utils.DensityUtil
import com.coffee.base.utils.LogUtil
import com.coffee.widget.R
import java.util.ArrayList

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2022/11/4 16:54
 */
class WeatherLineGraphView: View {

    //原始数据
    private var dataList: ArrayList<WeatherLineGraphBean> = arrayListOf()

    //单折线图坐标值数据（双折线图的坐标值数据）
    private lateinit var pointsList: MutableList<PointValue<WeatherLineGraphBean>>

    //画点的笔
    private lateinit var pointPaint: Paint

    //画线的笔
    private lateinit var linePaint: Paint

    //文字的笔
    private lateinit var textPaint: Paint

    //画图的笔
    private lateinit var picturePaint: Paint

    //X轴每个坐标的间距
    private val pointSpaceX: Float = DensityUtil.dp2pxF(140f)
    //折线图顶部和底部的垂直内边距
    private val graphVerticalPadding: Float = DensityUtil.dp2pxF(60f)
    //折线图距离View顶部的距离
    private val lineGraphMarginTop: Float = DensityUtil.dp2pxF(270f)

    //单折线坐标点的范围限制（双折线图时作为上区域坐标点的范围限制）
    private var pointTopY: Float = -1f
    private var pointBottomY: Float = -1f
    //双折线图时作为下区域坐标点的范围限制
    private var secondPointTopY: Float = -1f
    private var secondPointBottomY: Float = -1f

    //单折线图范围内Y轴中间值（双折线图时作为上区域Y轴中间值）
    private var lineGraphAverageY: Float = -1f
    //双折线图下区域Y轴中间值
    private var secondLineGraphAverageY: Float = -1f

    private var viewHeight: Int = 0

    private var maxTempValue: Float = -1f
    private var minTempValue: Float = -1f

    private var highMaxTempValue: Float = -1f
    private var highMinTempValue: Float = -1f



    //时段、星期的文本大小
    private var weekTimeTextSize: Float = -1f
    //时段、星期的文本颜色
    private var weekTimeTextColor: Int = Color.BLACK
    //日期与时段、星期的垂直间距
    private var dateAndWeekTextMargin: Float = -1f
    //日期的文本大小
    private var dateTextSize: Float = -1f
    //日期的文本颜色
    private var dateTextColor: Int = Color.BLACK

    //图片与时段、日期的垂直间距
    private var imgWithDateTimeMargin: Float = -1f

    //图片与天气文字的垂直间距
    private var imgWithWeatherTextMargin: Float = -1f

    //天气文本大小
    private var weatherTextSize: Float = -1f
    //天气文本颜色
    private var weatherTextColor: Int = Color.BLACK

    //温度坐标点的文本大小
    private var tempPointTextSize: Float = -1f
    //温度坐标点的文本颜色
    private var tempPointTextColor: Int = Color.BLACK
    //温度坐标点的半径
    private var tempPointRadius: Float = -1f
    //温度坐标点的颜色
    private var tempPointColor: Int = Color.BLUE
    //温度坐标点与温度文本的垂直间距
    private var tempPointAndTextMargin: Float = -1f

    //折线的颜色
    private var lineColor: Int = Color.GRAY
    //折线的粗细
    private var lineSize: Float = -1f

    //双折线开关
    private var doubleLineOpen: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.WeatherLineGraphView).apply {
            weekTimeTextSize = getDimension(R.styleable.WeatherLineGraphView_graph_weekTimeTextSize, DensityUtil.sp2pxF(28f))
            weekTimeTextColor = getColor(R.styleable.WeatherLineGraphView_graph_weekTimeTextColor, Color.parseColor("#E05F687A"))
            if (hasValue(R.styleable.WeatherLineGraphView_graph_dateMarginTop)) {
                dateAndWeekTextMargin = getDimension(R.styleable.WeatherLineGraphView_graph_dateMarginTop, DensityUtil.dp2pxF(13f))
            }
            if (hasValue(R.styleable.WeatherLineGraphView_graph_dateTextSize)) {
                dateTextSize = getDimension(R.styleable.WeatherLineGraphView_graph_dateTextSize, DensityUtil.sp2pxF(24f))
            }
            if (hasValue(R.styleable.WeatherLineGraphView_graph_dateTextColor)) {
                dateTextColor = getColor(R.styleable.WeatherLineGraphView_graph_dateTextColor, Color.parseColor("#E05F687A"))
            }
            imgWithDateTimeMargin = getDimension(R.styleable.WeatherLineGraphView_graph_imgMarginTop, DensityUtil.dp2pxF(90f))
            imgWithWeatherTextMargin = getDimension(R.styleable.WeatherLineGraphView_graph_weatherTextMarginTop, DensityUtil.dp2pxF(18f))
            weatherTextSize = getDimension(R.styleable.WeatherLineGraphView_graph_weatherTextSize, DensityUtil.sp2pxF(28f))
            weatherTextColor = getColor(R.styleable.WeatherLineGraphView_graph_weatherTextColor, Color.parseColor("#171C2C"))
            tempPointTextSize = getDimension(R.styleable.WeatherLineGraphView_graph_tempPointTextSize, DensityUtil.sp2pxF(32f))
            tempPointTextColor = getColor(R.styleable.WeatherLineGraphView_graph_tempPointTextColor, Color.parseColor("#E05F687A"))
            tempPointRadius = getDimension(R.styleable.WeatherLineGraphView_graph_tempPointRadius, DensityUtil.dp2pxF(4f))
            tempPointColor = getColor(R.styleable.WeatherLineGraphView_graph_tempPointColor, Color.parseColor("#2C7CEF"))
            tempPointAndTextMargin = getDimension(R.styleable.WeatherLineGraphView_graph_tempPointTextMargin, DensityUtil.dp2pxF(15f))
            lineColor = getColor(R.styleable.WeatherLineGraphView_graph_lineColor, Color.parseColor("#E05F687A"))
            lineSize = getDimension(R.styleable.WeatherLineGraphView_graph_lineSize, DensityUtil.dp2pxF(1f))
            doubleLineOpen = getBoolean(R.styleable.WeatherLineGraphView_graph_openDoubleLine, false)
            recycle()
        }
        linePaint = Paint().apply {
            color = lineColor
            style = Paint.Style.STROKE
            strokeWidth = lineSize
            isAntiAlias = true
        }
        pointPaint = Paint().apply {
            color = tempPointColor
            style = Paint.Style.FILL
            strokeWidth = 1f
            isAntiAlias = true
        }
        textPaint = Paint().apply {
            style = Paint.Style.FILL
            strokeWidth = 0f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        picturePaint = Paint().apply {
            isAntiAlias = true
        }
        //测试数据
        produceTestSingleData()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //计算出最小宽度值，保证不低于该宽度
        val minWidth = (dataList.size + 1) * pointSpaceX
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthResult = resolveSize(minWidth.toInt(), widthMeasureSpec)
        setMeasuredDimension(widthResult, resolveSize(DensityUtil.dp2px(600f), heightMeasureSpec))
        LogUtil.i("onMeasure: widthSize= $widthSize, minWidth= $minWidth, ---widthResult= $widthResult")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewHeight = h
        if (doubleLineOpen) {
            val averageY = (h * 1f + lineGraphMarginTop) / 2
            //这里思路是将折线图划分为八等份，取一等份作为上下区域的平均线间隔
            val doubleLineSpace = (h * 1f - lineGraphMarginTop) / 8
            //双折线图上区域的Y轴中间值
            lineGraphAverageY = averageY - doubleLineSpace
            ////双折线图下区域的Y轴中间值
            secondLineGraphAverageY = averageY + doubleLineSpace
            LogUtil.i("双折线图，上折线Y轴中间值：$lineGraphAverageY，下折线Y轴中间值：$secondLineGraphAverageY")

            //双折线上区域高度范围限制
            pointTopY = lineGraphMarginTop + graphVerticalPadding
            pointBottomY = averageY
            //双折线下区域高度范围限制
            secondPointTopY = averageY
            secondPointBottomY = h - graphVerticalPadding
            LogUtil.i("双折线图，上区域高度限制：pointTopY= $pointTopY, pointBottomY= $pointBottomY")
            LogUtil.i("双折线图，下区域高度限制：secondPointTopY= $secondPointTopY, secondPointBottomY= $secondPointBottomY")
        } else {
            //计算单折线图范围Y轴中间值
            lineGraphAverageY = (h * 1f + lineGraphMarginTop) / 2
            //单折线高度范围限制
            pointTopY = lineGraphMarginTop + graphVerticalPadding
            pointBottomY = h - graphVerticalPadding
            LogUtil.i("view高度：${viewHeight}, 单折线图范围Y轴中间值：$lineGraphAverageY")
            LogUtil.i("单折线高度限制：pointTopY= $pointTopY, pointBottomY= $pointBottomY")
        }
        producePointValue()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawText(canvas)
        drawLine(canvas)
        drawPoint(canvas)
    }

    private fun drawText(canvas: Canvas?) {
        if (doubleLineOpen) {
            pointsList.forEach {
                //星期
                textPaint.apply {
                    textSize = weekTimeTextSize
                    color = weekTimeTextColor
                }
                val weekTextY = paddingTop + weekTimeTextSize
                canvas?.drawText(it.data.weekTime, it.x, weekTextY, textPaint)

                //日期
                textPaint.apply {
                    textSize = dateTextSize
                    color = dateTextColor
                }
                val dateTextY = weekTextY + dateAndWeekTextMargin + dateTextSize
                canvas?.drawText(it.data.dateTime ?: "", it.x, dateTextY, textPaint)

                //天气图片
                val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.big_rain)
                val dstTop = dateTextY + imgWithDateTimeMargin
                val dstRect = RectF(it.x - bitmap.width / 2, dstTop, it.x + bitmap.width / 2, dstTop + bitmap.height)
                canvas?.drawBitmap(bitmap, null, dstRect, picturePaint)

                //天气文案
                textPaint.apply {
                    textSize = weatherTextSize
                    color = weatherTextColor
                }
                val weatherTextY = dstTop + bitmap.height + imgWithWeatherTextMargin + weatherTextSize
                canvas?.drawText(it.data.weather, it.x, weatherTextY, textPaint)

                //坐标上的温度文字
                textPaint.apply {
                    textSize = tempPointTextSize
                    color = tempPointTextColor
                }
                //上折线区域坐标点上的温度值
                canvas?.drawText(it.data.highTemp?.toInt().toString(), it.x, it.firstLineY - tempPointRadius - tempPointAndTextMargin, textPaint)
                //下折线区域坐标点上的温度值
                canvas?.drawText(it.data.lowTemp?.toInt().toString(), it.x, it.secondLineY + tempPointRadius + tempPointAndTextMargin + tempPointTextSize, textPaint)
            }
        } else {
            pointsList.forEach {
                //时段、日期
                textPaint.apply {
                    textSize = weekTimeTextSize
                    color = weekTimeTextColor
                }
                val weekTextY = paddingTop + weekTimeTextSize
                canvas?.drawText(it.data.weekTime, it.x, weekTextY, textPaint)

                //天气图片
                val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.big_rain)
                val dstTop = weekTextY + imgWithDateTimeMargin
                val dstRect = RectF(it.x - bitmap.width / 2, dstTop, it.x + bitmap.width / 2, dstTop + bitmap.height)
                canvas?.drawBitmap(bitmap, null, dstRect, picturePaint)

                //天气文案
                textPaint.apply {
                    textSize = weatherTextSize
                    color = weatherTextColor
                }
                val weatherTextY = dstTop + bitmap.height + imgWithWeatherTextMargin + weatherTextSize
                canvas?.drawText(it.data.weather, it.x, weatherTextY, textPaint)

                //折线坐标点上的温度值
                textPaint.apply {
                    textSize = tempPointTextSize
                    color = tempPointTextColor
                }
                canvas?.drawText(it.data.temp?.toInt().toString(), it.x, it.firstLineY - tempPointRadius - tempPointAndTextMargin, textPaint)
            }
        }
    }

    private fun drawPoint(canvas: Canvas?) {
        pointsList.forEach {
            canvas?.drawCircle(it.x, it.firstLineY, tempPointRadius, pointPaint)
            if (doubleLineOpen) {
                canvas?.drawCircle(it.x, it.secondLineY, tempPointRadius, pointPaint)
            }
        }
    }

    private fun drawLine(canvas: Canvas?) {
        var count = pointsList.size - 1
        var index = 0
        while (count > 0) {
            val firstValue = pointsList[index]
            val secondValue = pointsList[index + 1]
            canvas?.drawLine(firstValue.x, firstValue.firstLineY, secondValue.x, secondValue.firstLineY, linePaint)
            if (doubleLineOpen) {
                canvas?.drawLine(firstValue.x, firstValue.secondLineY, secondValue.x, secondValue.secondLineY, linePaint)
            }
            count--
            index++
        }
    }

    //生成时段的测试数据
    private fun produceTestSingleData() {
        if (doubleLineOpen) {
            dataList.add(WeatherLineGraphBean(10f, 32f,20f,"今天", "8/12", "0", "多云"))
            dataList.add(WeatherLineGraphBean(8f, 35f,24f,"明天", "8/13","0", "多云"))
            dataList.add(WeatherLineGraphBean(16f, 21f,17f,"周六", "8/14","0", "雷阵雨"))
            dataList.add(WeatherLineGraphBean(20f, 22f,15f,"周日", "8/15","0", "阵雨"))
            dataList.add(WeatherLineGraphBean(22f, 36f,22f,"周一", "8/16","0", "晴天"))
            dataList.add(WeatherLineGraphBean(19f, 28f,20f,"周二", "8/17","0", "雨天"))
            dataList.add(WeatherLineGraphBean(4f, 30f,18f,"周三", "8/18","0", "雾霾"))
            dataList.add(WeatherLineGraphBean(10f, 21f,16f,"周四", "8/19", "0", "雾霾"))
            dataList.add(WeatherLineGraphBean(15f, 16f,8f,"周五", "8/20","0", "雾霾"))
            dataList.add(WeatherLineGraphBean(18f, 15f,5f,"周六", "8/21", "0", "雾霾"))
            dataList.add(WeatherLineGraphBean(11f, 10f,5f,"周日", "8/22", "0", "雾霾"))

            val sortList: ArrayList<WeatherLineGraphBean> = arrayListOf()
            sortList.addAll(dataList)
            //高温度值的最大值和最小值
            sortList.sortBy { it.highTemp }
            highMinTempValue = sortList.first().highTemp!!
            highMaxTempValue = sortList.last().highTemp!!
            LogUtil.d("双折线图，高温度值的最小温度:$highMinTempValue")
            LogUtil.d("双折线图，高温度值的最大温度:$highMaxTempValue")
            //低温度值的最大值和最小值
            sortList.sortBy { it.lowTemp }
            minTempValue = sortList.first().lowTemp!!
            maxTempValue = sortList.last().lowTemp!!
            LogUtil.d("双折线图，低温度值的最小温度:$minTempValue")
            LogUtil.d("双折线图，低温度值的最大温度:$maxTempValue")

        } else {
            dataList.add(WeatherLineGraphBean(10f, null,null,"现在", null,"0", "多云"))
            dataList.add(WeatherLineGraphBean(8f, null,null,"9:00", null,"0", "多云"))
            dataList.add(WeatherLineGraphBean(16f, null,null,"11:00", null,"0", "雷阵雨"))
            dataList.add(WeatherLineGraphBean(20f, null,null,"13:00", null,"0", "阵雨"))
            dataList.add(WeatherLineGraphBean(22f, null,null,"15:00", null,"0", "晴天"))
            dataList.add(WeatherLineGraphBean(19f, null,null,"17:00", null,"0", "雨天"))
            dataList.add(WeatherLineGraphBean(4f, null,null,"19:00", null,"0", "雾霾"))
            dataList.add(WeatherLineGraphBean(10f, null,null,"21:00", null,"0", "雾霾"))
            dataList.add(WeatherLineGraphBean(15f, null,null,"23:00", null,"0", "雾霾"))
            dataList.add(WeatherLineGraphBean(18f, null,null,"1:00", null,"0", "雾霾"))
            dataList.add(WeatherLineGraphBean(11f, null,null,"3:00", null,"0", "雾霾"))

            val sortList: ArrayList<WeatherLineGraphBean> = arrayListOf()
            sortList.addAll(dataList)
            sortList.sortBy { it.temp }
            minTempValue = sortList.first().temp!!
            maxTempValue = sortList.last().temp!!
            LogUtil.d("单折线图，maxValue:$maxTempValue")
            LogUtil.d("单折线图，minValue:$minTempValue")
        }
    }

    //生成坐标数据
    private fun producePointValue() {
        if (doubleLineOpen) {
            //上折线平均温度
            val averageTemp = (highMaxTempValue + highMinTempValue) / 2
            //下折线平均温度
            val secondAverageTemp = (maxTempValue + minTempValue) / 2
            LogUtil.i("双折线图，上折线平均温度为：$averageTemp, 下折线平均温度为：$secondAverageTemp")
            //生成双折线区域的坐标点数据
            pointsList = dataList.mapIndexed { index, bean ->
                val firstPointY = lineGraphAverageY + ((pointBottomY - pointTopY) * 1f / (highMaxTempValue - highMinTempValue) * (averageTemp - bean.highTemp!!))
                val secondPointY = secondLineGraphAverageY + ((secondPointBottomY - secondPointTopY) * 1f / (maxTempValue - minTempValue) * (secondAverageTemp - bean.lowTemp!!))
                PointValue(bean, firstPointY, secondPointY, (index + 1) * pointSpaceX)
            }.toMutableList()
            for (value in pointsList) {
                LogUtil.i("双折线图，计算出的坐标数组：$value")
            }
        } else {
            //先计算出平均温度差
            val averageTemp = (maxTempValue + minTempValue) / 2
            LogUtil.i("单折线图，平均温度为：$averageTemp")

            //循环，取值，生成单折线的坐标点数据
            pointsList = dataList.mapIndexed { index, bean ->
                val pointY = lineGraphAverageY + ((pointBottomY - pointTopY) * 1f / (maxTempValue - minTempValue) * (averageTemp - bean.temp!!))
                PointValue(bean, pointY, -1f,(index + 1) * pointSpaceX)
            }.toMutableList()
            for (value in pointsList) {
                LogUtil.i("单折线图，计算出的坐标数组：$value")
            }
        }
    }

    internal data class PointValue<V>(
        var data: V,
        var firstLineY: Float,
        var secondLineY: Float,
        var x: Float
    )

}