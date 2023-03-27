package com.coffee.base.ui

import android.widget.Toast


/**
 *
 * @Description: 组件视图的抽象接口
 * @Author: ly-zfensheng
 * @CreateDate: 2022/6/9 14:21
 */
interface BaseComponentView<VB>: BaseView<VB> {

    fun initData()

    open fun BaseComponentView<VB>.toast(msg: CharSequence) = showToast(msg, Toast.LENGTH_SHORT)

    open fun BaseComponentView<VB>.toastLong(msg: CharSequence) = showToast(msg, Toast.LENGTH_LONG)

    private fun showToast(msg: CharSequence, flag: Int) {
        when(this) {
            is BaseActivity -> Toast.makeText(this, msg, flag).show()
            is BaseFragment -> Toast.makeText(this.requireContext(), msg, flag).show()
        }
    }

}