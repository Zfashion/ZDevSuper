package com.coffee.base.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.JsonUtils
import com.coffee.base.utils.reflectBinding
import kotlinx.coroutines.launch

/**
 *
 * @Description: 抽象类，供外部 Activity 继承
 * @Author: ly-zfensheng
 * @CreateDate: 2022/6/9 14:22
 */
abstract class BaseActivity<VB: ViewDataBinding>: AppCompatActivity(), BaseComponentView<VB> {

    val mBinding: VB by lazy {
        reflectBinding(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
//        immersionStatusBar()
        initView(mBinding)
        initData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /**
     * 提供跳转
     */
    inline fun <reified T : ComponentActivity> Activity.launch() {
        startActivity(Intent(this, T::class.java))
    }

    /**
     * 提供跳转，带 finish() 动作
     */
    inline fun <reified T : ComponentActivity> Activity.launchWithFinish() {
        startActivity(Intent(this, T::class.java))
        finish()
    }

    /**
     * 提供跳转(带参数)
     */
    inline fun <reified T: ComponentActivity> Activity.launch(vararg pairs: Pair<String, Any?>) {
        val intent = Intent(this, T::class.java)
        intent.putExtras(bundleOf(*pairs))
        startActivity(intent)
    }

}
