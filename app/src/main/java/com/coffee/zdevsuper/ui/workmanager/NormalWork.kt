package com.coffee.zdevsuper.ui.workmanager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import com.coffee.base.utils.LogUtil
import com.coffee.zdevsuper.R
import com.google.common.util.concurrent.ListenableFuture

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2023/7/14 11:53
 */
class NormalWork(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {

    override fun doWork(): Result {

        val size = inputData.keyValueMap.size
        if (size > 0) {
            val flag = inputData.getInt("flag", 0)
            when (flag) {
                0 -> {
                    LogUtil.d("带参数的Work，解析参数")
                    val band = inputData.getString("params_1")
                    val song = inputData.getString("params_2")
                    LogUtil.d("最爱听的乐队=$band, 最爱的歌=$song")
                }
                1 -> {
                    LogUtil.d("加急任务")
                    val song = inputData.getString("song")
                    val lyric = inputData.getString("lyric")
                    LogUtil.d("song=$song, lyric=$lyric")
                }
            }
        } else {
            LogUtil.d("work curThread= ${Looper.getMainLooper().isCurrentThread}")
            //模拟异步耗时10s
            Thread.sleep(10 * 1000)
            LogUtil.d("normal work-> doWork...")
        }

        return Result.success()
    }

    /**
     * Android 11及以下，设置setExpedited 加急属性时，要注意实现 getForegroundInfoAsync()，否则报异常
     */
    @SuppressLint("RestrictedApi")
    override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
        val future = SettableFuture.create<ForegroundInfo>()
        future.set(getForegroundInfo())
        return future
    }

    private fun getForegroundInfo(): ForegroundInfo {
        //前台服务的通知
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Expedited-1",
                "ExpeditedWork",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, "Expedited-1")
            .setSmallIcon(R.drawable.agriculture)
            .setContentTitle("ZDevSuper-Expedited-Task")
            .setContentText("这是一个加急任务")
            .build()

        return ForegroundInfo(2023, notification)
    }

}