package com.coffee.zdevsuper.ui.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.coffee.base.ext.onClickDebounced
import com.coffee.base.ui.BaseActivity
import com.coffee.base.utils.LogUtil
import com.coffee.zdevsuper.databinding.DialogTestActivityBinding


/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2023/7/13 18:13
 */
class TestDialogActivity: BaseActivity<DialogTestActivityBinding>(), ActivityResultCallback<Boolean> {

    private lateinit var resultLauncher: ActivityResultLauncher<Unit>

    inner class SettingResultContract : ActivityResultContract<Unit, Boolean>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            return Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            LogUtil.d("resultCode= $resultCode, intent= $intent")
            return resultCode == 0
        }

    }


    private val guideVideoDialog: GuideVideoDialog by lazy {
        GuideVideoDialog(this, true, lifecycleScope)
    }

    override fun initData() {
//        resultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), this)
        resultLauncher = registerForActivityResult(SettingResultContract(), this)
    }

    override fun initView(vb: DialogTestActivityBinding) {
        vb.btn1.setOnClickListener {
            resultLauncher.launch(Unit)
        }
        vb.btn2.onClickDebounced(lifecycleScope) {
            if (guideVideoDialog.isShowing) guideVideoDialog.dismiss()
            guideVideoDialog.show()
        }
    }

    override fun onActivityResult(result: Boolean?) {
        LogUtil.d("result= $result")
    }
}