package com.coffee.base.common

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.coffee.base.databinding.CommonDialogBinding
import com.coffee.base.ext.onClickDebounced
import com.coffee.base.ui.BaseDialog

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2023/5/4 09:02
 */
class CommonDialog(context: Context): BaseDialog<CommonDialogBinding>(context) {

    init {
        if (context is ComponentActivity) {
            setOwnerActivity(context)
        }
    }

    override fun initView(vb: CommonDialogBinding) {
        if (ownerActivity is ComponentActivity) {
            val scope = (ownerActivity as ComponentActivity).lifecycleScope

            vb.close.onClickDebounced(scope) {
                dismiss()
            }
        }
    }

    override fun initData() {

    }

}