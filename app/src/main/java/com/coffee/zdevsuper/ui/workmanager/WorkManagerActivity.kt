package com.coffee.zdevsuper.ui.workmanager

import android.annotation.SuppressLint
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.coffee.base.ext.onClickDebounced
import com.coffee.base.ui.BaseActivity
import com.coffee.zdevsuper.databinding.ActivityWorkmanagerBinding
import java.util.concurrent.TimeUnit

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2023/7/14 11:29
 */
class WorkManagerActivity: BaseActivity<ActivityWorkmanagerBinding>() {

    //常规的一次性任务
    private lateinit var oneTimeWorkRequest: WorkRequest
    //带参数的一次性任务
    private lateinit var oneTimeWorkRequestWithParams: WorkRequest
    /**
     * 加急任务（WorkManager 2.7 才开放该api）
     * Android 12上可以直接调用setExpedited来告诉系统
     * Android 11及以下的，必须实现getForegroundInfoAsync()方法，否则报错
     */
    private lateinit var expeditedWorkRequest: WorkRequest


    //定时任务
    //官方规定可以定义的最短重复间隔就是 15 分钟
    private lateinit var periodicWorkRequest: WorkRequest


    override fun initData() {
        oneTimeWorkRequest = OneTimeWorkRequestBuilder<NormalWork>().build()

        oneTimeWorkRequestWithParams = OneTimeWorkRequestBuilder<NormalWork>()
            .setInputData(workDataOf("flag" to 0, "params_1" to "告五人", "params_2" to "爱人错过～"))
            .build()

        expeditedWorkRequest = OneTimeWorkRequestBuilder<NormalWork>()
            .setInputData(workDataOf("flag" to 1, "song" to "在这座城市遗失了你", "lyric" to "在这座城市遗失了你，顺便遗失了自己～"))
             //OutOfQuotaPolicy有两个枚举{
            //  RUN_AS_NON_EXPEDITED_WORK_REQUEST -> 如果系统无法加急该任务，则转为常规任务
            //  DROP_WORK_REQUEST   -> 如果无法加急，则删除该任务
            // }
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        periodicWorkRequest = PeriodicWorkRequestBuilder<UploadWork>(15, TimeUnit.MICROSECONDS)
            .build()
    }

    override fun initView(vb: ActivityWorkmanagerBinding) {
        var i = 0
        vb.button.onClickDebounced(lifecycleScope) {
            WorkManager.getInstance(this).enqueue(oneTimeWorkRequest)
        }
        vb.buttonParams.onClickDebounced(lifecycleScope) {
            WorkManager.getInstance(this).enqueue(oneTimeWorkRequestWithParams)
        }
        vb.buttonExpedited.onClickDebounced(lifecycleScope) {
            WorkManager.getInstance(this).enqueue(expeditedWorkRequest)
        }
        vb.buttonPeriodic.onClickDebounced(lifecycleScope) {
            WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        }
        vb.buttonLongWork.onClickDebounced(lifecycleScope) {
            i +=1
            vb.buttonDelayWork.text = "咖啡量+${i}"
        }
    }
}