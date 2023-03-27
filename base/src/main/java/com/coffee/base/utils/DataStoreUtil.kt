package com.coffee.base.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 *
 * @Description: 数据存储工具类，平替 SharedPreferences
 * @Author: ly-zfensheng
 * @CreateDate: 2022/6/21 09:34
 */

class DataStoreUtil internal constructor() {

    companion object {

        //供外部调用入口
        @JvmStatic
        fun getInstance() = INSTANCE

        private val INSTANCE : DataStoreUtil by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DataStoreUtil() }

        private const val STORE_NAME = "RoarSmall"
    }

    //通过Context来委托代理，实例化获得DataStore对象
    private val Context.innerDataStore: DataStore<Preferences> by preferencesDataStore(name = STORE_NAME)

    private lateinit var outerDataStore: DataStore<Preferences>

    /**
     * 初始化, 外部无需再调用，已放在 StartUp 中处理
     */
    fun DataStoreUtil.initStore(context: Context) {
        if (this::outerDataStore.isInitialized) return
        outerDataStore = context.innerDataStore
    }


    /**
     * 读取数据，可带默认值
     * @return Flow<T> 数据流
     */
    fun <T> parse(key: Preferences.Key<T>, defaultValue: T?): Flow<T?> {
        return outerDataStore.data
            .map {
                if (it.contains(key)) {
                    it[key]
                } else {
                    defaultValue
                }
            }
            .conflate() //接收端只需接收最新的值
            .catch { e ->
                LogUtil.e("getPrivacyValue() -> 上游异常，message: ${e.message}")
                throw e
            }
            .flowOn(Dispatchers.IO) //指定发送端的协程上下文
    }

    /**
     * 读取数据，默认值为null
     */
    fun <T> parse(key: Preferences.Key<T>): Flow<T?> = parse(key, null)


    /**
     * 单个数据写入
     */
    suspend fun <T> edit(key: Preferences.Key<T>, value: T) {
        outerDataStore.edit {
            it[key] = value
        }
    }

    /**
     * 多个数据写入
     */
    suspend fun editAll(vararg pair: Preferences.Pair<*>) {
        outerDataStore.edit {
            it.putAll(*pair)
        }
    }

}