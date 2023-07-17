package com.coffee.base.ext

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.coffee.base.R
import com.coffee.base.utils.LogUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.*
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 *
 * @Description: 有关 View 类的扩展函数
 * @Author: ly-zfensheng
 * @CreateDate: 2022/6/17 9:18
 */


/**
 * 防抖机制更新
 * >>> Attention!!!
 * 该函数适合点击事件存在耗时任务的，action参数是suspend的
 */
fun View.onClickDebouncedAsync(
    lifecycleCoroutineScope: LifecycleCoroutineScope,
    delayMillis: Long = 500L,
    action: suspend () -> Unit
) {
    val debounce = lifecycleCoroutineScope.coroutineContext + Dispatchers.IO
    val events = Channel<Unit>(Channel.CONFLATED)
    val job = lifecycleCoroutineScope.launch(debounce) {
        events.consumeAsFlow()
            .onEach {
                action()
                delay(delayMillis)
            }
            .collect()
    }
    setOnClickListener {
        events.trySend(Unit)
    }
    //当带生命周期的协程作用域执行销毁时,取消所有点击事件的协程任务
    lifecycleCoroutineScope.coroutineContext.job.invokeOnCompletion {
        job.cancel()
    }
}

/**
 * 防抖机制更新
 * 该函数适合点击事件必须就在主线程上执行的
 */
fun View.onClickDebounced(
    lifecycleCoroutineScope: LifecycleCoroutineScope,
    delayMillis: Long = 500L,
    action: () -> Unit
) {
    val debounce = lifecycleCoroutineScope.coroutineContext + Dispatchers.IO
    val events = Channel<Unit>(Channel.CONFLATED)
    val job = lifecycleCoroutineScope.launch(debounce) {
        events.consumeAsFlow()
            .onEach {
                LogUtil.d("收到事件，执行处理")
                withContext(Dispatchers.Main.immediate) {
                    action()
                }
//                delay(delayMillis)
            }
            .debounce(delayMillis)
            .collect()
    }
    setOnClickListener {
        events.trySend(Unit)
    }
    //当带生命周期的协程作用域执行销毁时,取消所有点击事件的协程任务
    lifecycleCoroutineScope.coroutineContext.job.invokeOnCompletion {
        job.cancel()
    }
}

/**
 * 防止 控件 多次点击
 */
@OptIn(ObsoleteCoroutinesApi::class)
inline fun <reified V: View> V.setClick(crossinline action: (V) -> Unit) {
    val lifecycleOwner = findViewTreeLifecycleOwner()
    if (lifecycleOwner == null) {
        setClick2(action)
        return
    }
    lifecycleOwner.apply {
        val scope = this.lifecycleScope
        val eventActor = scope.actor<V>(Dispatchers.Main.immediate) {
            for (event in channel) {
                action.invoke(event)
                //延时 1s, 避免多次点击
                delay(1000)
            }
        }
        setOnClickListener {
            if (it is V) eventActor.trySend(it)
        }
    }
}

inline fun <reified V : View> V.setClick2(crossinline action: (V) -> Unit) {
    var lastClickTime = -1L
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime >= 1000L) {
            lastClickTime = currentTimeMillis
            if (it is V) action.invoke(it)
        }
    }
}

/**
 * View 添加标识Tag 的代理扩展
 */
fun <T> viewTags(key: Int) = object : ReadWriteProperty<View, T?> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: View, property: KProperty<*>) =
        thisRef.getTag(key) as? T

    override fun setValue(thisRef: View, property: KProperty<*>, value: T?) =
        thisRef.setTag(key, value)
}

@Suppress("UNCHECKED_CAST")
fun <T> viewTags(key: Int, block: View.() -> T) = ReadOnlyProperty<View, T> { thisRef, _ ->
    if (thisRef.getTag(key) == null) {
        thisRef.setTag(key, block(thisRef))
    }
    thisRef.getTag(key) as T
}


/**
 * ##自Api 30开始，官方不推荐使用 setSystemUiVisibility，建议使用 WindowInsetsController 来代替（传送门：https://developer.android.google.cn/reference/android/view/View?hl=en#setSystemUiVisibility(int)）
 * 通过 WindowInsetsController 可以更改 window 相关的设置，比如 show(), hide(), 检查是否是亮色，控制系统条的行为等 ~~~
 */
val View.windowInsetsControllerCompat: WindowInsetsControllerCompat? by viewTags(R.id.tag_window_insets_controller) {
    ViewCompat.getWindowInsetsController(this)
}

/**
 * 获取窗口的一个Insets类，Insets 可以理解为窗口的边框属性类，记录了窗口的 top, left, right, bottom 四个边框的偏移量
 */
val View.rootWindowInsetsCompat: WindowInsetsCompat? by viewTags(R.id.tag_root_window_insets) {
    ViewCompat.getRootWindowInsets(this)
}


/**
 * 设置圆角
 */
fun View.setRoundCorner(radius: Float) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, width, height, radius)
        }
    }
}