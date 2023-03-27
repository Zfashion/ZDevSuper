package com.coffee.zdevsuper

import android.app.Application
import com.drake.brv.utils.BRV
import kotlin.coroutines.suspendCoroutine

/**
 *
 * @Description:
 * @Author: ly-zfensheng
 * @CreateDate: 2022/9/14 16:52
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        //初始化BindingAdapter 的默认绑定ID
        BRV.modelId = BR.data

    }

}