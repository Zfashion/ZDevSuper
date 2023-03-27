package com.coffee.widget.round

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.coffee.widget.round.helper.RoundHelper
import com.coffee.widget.round.helper.RoundHelperImpl

/**
 *
 * @Author: ly-zfensheng
 * @CreateDate: 2022/02/21 16:40
 */
class RoundImageView: AppCompatImageView {

    private val roundHelper: RoundHelper = RoundHelperImpl()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        roundHelper.init(context, attrs, this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        roundHelper.onSizeChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        roundHelper.preDraw(canvas)
        super.draw(canvas)
        roundHelper.draw(canvas, drawableState)
    }

}