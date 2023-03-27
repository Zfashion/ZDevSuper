package com.coffee.widget.round.helper

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 *
 * @Author: zfensheng
 * @CreateDate: 2022/02/21 16:24
 */
interface RoundHelper {

    fun init(context: Context, attrs: AttributeSet?, view: View)

    fun onSizeChanged(width: Int, height: Int)

    fun preDraw(canvas: Canvas)

    fun draw(canvas: Canvas, drawableState: IntArray)
}