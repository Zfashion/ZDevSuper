package com.coffee.widget.motionlayout

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import com.coffee.base.utils.LogUtil
import com.google.android.material.appbar.AppBarLayout

/**
 *
 * @Description:带有MotionEvent的ToolBar
 * @Author: ly-zfensheng
 * @CreateDate: 2022/8/23 15:42
 */
class CollapsibleMotionToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): MotionLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? AppBarLayout)?.addOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        progress = -verticalOffset / appBarLayout?.totalScrollRange?.toFloat()!!
        LogUtil.d("AppBarLayout Offset Changed verticalOffset=$verticalOffset, progress=$progress")
    }

}