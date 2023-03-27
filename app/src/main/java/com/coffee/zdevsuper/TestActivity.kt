package com.coffee.zdevsuper

import android.view.View
import android.view.View.OnClickListener
import androidx.lifecycle.LifecycleCoroutineScope
import com.coffee.base.ui.BaseActivity
import com.coffee.base.utils.LogUtil
import com.coffee.zdevsuper.databinding.ActivityTestBinding

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2022/12/13 17:22
 */
class TestActivity: BaseActivity<ActivityTestBinding>() {

    private var i = 0

    override fun initData() {
    }

    override fun initView(vb: ActivityTestBinding) {
        vb.update.setOnClickListener {
           LogUtil.d("test")
        }
        vb.update.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                LogUtil.d("test")
            }
        })
    }
}