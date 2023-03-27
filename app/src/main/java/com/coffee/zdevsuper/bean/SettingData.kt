package com.coffee.zdevsuper.bean

import androidx.activity.ComponentActivity
import androidx.databinding.BaseObservable

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2022/9/14 17:04
 */


data class SettingData(
    val title: String, val clazz: Class<out ComponentActivity>?
): BaseObservable()