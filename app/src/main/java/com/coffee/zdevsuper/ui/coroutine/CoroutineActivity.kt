package com.coffee.zdevsuper.ui.coroutine

import android.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.coffee.base.ui.BaseActivity
import com.coffee.base.utils.LogUtil
import com.coffee.zdevsuper.databinding.ActivityCoroutineBinding
import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.function.Supplier
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 *
 * @Author: zfensheng
 * @CreateDate: 2021/06/17 17:42
 */
class CoroutineActivity: BaseActivity<ActivityCoroutineBinding>() {

    private val threadPool = Executors.newFixedThreadPool(10)
    private val singleThreadPool = Executors.newSingleThreadExecutor()
    private var temp = 0
    private val colorList = arrayListOf("#FFC678FA", "#FFCFAD47", "#FF31D1C2", "#FFDF5F36")

    private val str: StringBuilder = StringBuilder()




    private var index = 0
    override fun initData() {
        /*lifecycleScope.launch {
            LogUtil.i("Coroutine --- start")
//            testCoroutine()
//            testCoroutine2()

            startFloat()
        }
        LogUtil.i("Coroutine --- do other things")*/

        lifecycleScope.apply {
            launch {
//                delay(200)
                for (i in 0..10000) {
                    index += i
                }
                LogUtil.d("第一个任务, index= $index")
            }

            LogUtil.d("第二个任务")
        }
    }


    private suspend fun startFloat() {
        val result = withTimeoutOrNull(2000L) {
            if (generateNum()) {
                LogUtil.i("可以开启")
                true
            } else {
                LogUtil.i("还不可以开启")
                delay(2000)
                false
            }
        }

        if (result == null || result.not()) {
            delay(1000L)
            startFloat()
        }
    }

    private fun generateNum(): Boolean {
        return Random.nextBoolean()
    }


    private suspend fun testCoroutine() {
        val time = measureTimeMillis {
            LogUtil.i("testCoroutine() --- start")
            val v1 = loadFromRoom()

            LogUtil.i("execute loadFromNetwork()")
            val v2 = loadFromNetwork()

            LogUtil.i("execute combineData()")
            val v3 = combineData(v1, v2)

            LogUtil.i("execute toUI()")
            toUI(v3)

            LogUtil.i("testCoroutine() --- end")
        }
        LogUtil.i("testCoroutine() --- time= $time")
    }

    /**
     * 并行执行异步任务
     */
    private suspend fun testCoroutine2() {
        val time = measureTimeMillis {
            LogUtil.i("testCoroutine2() --- start")
            val async1 = lifecycleScope.async { loadFromRoom() }

            LogUtil.i("testCoroutine2() --- 1")
            val async2 = lifecycleScope.async { loadFromNetwork() }

            LogUtil.i("testCoroutine2() --- 2")
            val async3 = lifecycleScope.async { combineData(async1.await(), async2.await()) }

            LogUtil.i("testCoroutine2() --- 3")
            toUI(async3.await())

            LogUtil.i("testCoroutine2() --- end")
        }
        LogUtil.i("testCoroutine2() --- time= $time")
    }

    private suspend fun loadFromRoom(): Int = withContext(Dispatchers.IO) {
        LogUtil.i("loadFromRoom --- 数据库加载数据 start")
        delay(4000)
        LogUtil.i("loadFromRoom --- 数据库加载数据完成")
        return@withContext 10
    }

    private suspend fun loadFromNetwork(): Int = withContext(Dispatchers.IO) {
        LogUtil.i("loadFromNetwork --- 网络请求数据 start")
        delay(2000)
        LogUtil.i("loadFromNetwork --- 网络请求数据完成")
        return@withContext 5
    }

    private suspend fun combineData(v1: Int, v2: Int): Int = withContext(Dispatchers.IO) {
        LogUtil.i("loadFromNetwork --- 组合数据完成 start")
        delay(3000)
        LogUtil.i("combineData --- 组合数据完成")
        return@withContext v1 + v2
    }

    private suspend fun toUI(data: Int) = withContext(Dispatchers.Main.immediate) {
        LogUtil.i("toUI --- UI更新，data = $data")
    }



    override fun initView(vb: ActivityCoroutineBinding) {
        vb.btnOne.setOnClickListener {
            vb.tvOne.setBackgroundColor(Color.parseColor(colorList[temp]))
            if (temp < colorList.size - 1) { ++temp }
            else temp = 0
        }

        vb.output.setOnClickListener {
            val str = compose2?.get()
            vb.output.text = str
        }
    }


    /*********************************************
     * 协程的异常处理
     *********************************************/
    private fun testCoroutineExceptionHandler() {
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            LogUtil.e("CoroutineExceptionHandler --- e: $throwable")
        }

        //如果不加CoroutineExceptionHandler，则异常抛到外层，并根据异常类型，部分异常可导致应用异常退出
//        lifecycleScope.launch {
        //上下文加异常处理CoroutineExceptionHandler，根协程可捕获到异常并处理
        lifecycleScope.launch(handler) {
            LogUtil.i("coroutineScope --- start")
            launch(Dispatchers.Default) {
                delay(2000)
                LogUtil.i("coroutineScope launch 2 --- ${Thread.currentThread().name}")
            }

            launch(Dispatchers.Default) {
                delay(4000)
                LogUtil.i("coroutineScope launch 4 --- ${Thread.currentThread().name}")
                throw AssertionError()
            }
            LogUtil.i("coroutineScope --- end")
        }
    }



    /**********************************************
     * Promise/Future
     *********************************************/
    private var compose2: CompletableFuture<String>? = null
    private fun testPromise() {
        val future1 = CompletableFuture.supplyAsync(object : Supplier<Int> {
            override fun get(): Int {
                Thread.sleep(3000)
                LogUtil.i("future1 current thread name= ${Thread.currentThread().name}")
                return 3
            }
        })

        val compose1 =
            future1.thenCompose(object : java.util.function.Function<Int, CompletionStage<Int>> {
                override fun apply(value: Int): CompletionStage<Int> {
                    val future2 = CompletableFuture.supplyAsync(object : Supplier<Int> {
                        override fun get(): Int {
                            Thread.sleep(4000)
                            LogUtil.i("compose1 current thread name= ${Thread.currentThread().name}")
                            return value + 4
                        }
                    })
                    return future2
                }
            })

        compose2 =
            compose1.thenCompose(object : java.util.function.Function<Int, CompletionStage<String>> {
                override fun apply(value: Int): CompletionStage<String> {
                    val future3 = CompletableFuture.supplyAsync(object : Supplier<String> {
                        override fun get(): String {
                            LogUtil.i("compose2 current thread name= ${Thread.currentThread().name}")
                            return "result = $value"
                        }
                    })
                    return future3
                }
            })
    }


    /**********************************************
     * 测试开启多协程执行
     *********************************************/
    private fun testMultipleCoroutine() {
        var x = 0
        val start = System.currentTimeMillis()
        repeat(100_000) {
            lifecycleScope.launch(Dispatchers.Default) {
                delay(10)
                LogUtil.i("ThreadName= ${Thread.currentThread().name}")
                x++
                println("$x")
                if (it == 999) {
                    val timeMillis = System.currentTimeMillis() - start
                    LogUtil.i("testJavaExecutor timeMillis $timeMillis")
                }
            }
        }
    }

    /**
     * 测试多线程
     */
    private fun testJavaExecutor() {
        //100_000
        var x = 0
        val start = System.currentTimeMillis()
        repeat(100_000) {
            threadPool.execute {
                try {
                    Thread.sleep(10)
                    LogUtil.i("ThreadName= ${Thread.currentThread().name}")
                    x++
                    println("$x")
                    if (it == 999) {
                        val timeMillis = System.currentTimeMillis() - start
                        LogUtil.i("testJavaExecutor timeMillis $timeMillis")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}