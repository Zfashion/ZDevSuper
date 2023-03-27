package com.coffee.base.ext

import android.app.Activity
import android.view.View
import android.view.ViewGroup

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2022/8/27 16:28
 */


inline val Activity.contentView: View
    get() = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)