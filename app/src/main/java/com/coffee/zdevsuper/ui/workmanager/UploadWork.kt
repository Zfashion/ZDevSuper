package com.coffee.zdevsuper.ui.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.coffee.base.utils.LogUtil
import kotlinx.coroutines.delay

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2023/7/14 16:48
 */
class UploadWork(context: Context, workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {

    private var index = 0

    override suspend fun doWork(): Result {
        doDegressJob()
        return Result.success()
    }

    private tailrec suspend fun doDegressJob() {
        index++
        delay(500)
        if (index == 4) {
            LogUtil.d("满足条件，结束")
            return
        }
        else {
            LogUtil.d("不满足，递归")
            doDegressJob()
        }
    }

}