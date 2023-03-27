package com.coffee.base.ext

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2022/9/6 9:43
 */


fun <T: Throwable> T.throwableToString(): String = this.stackTraceToString()


fun <T: Any> T.arrayToString(): String {
    return when(this) {
        is BooleanArray -> {
            this.contentToString()
        }
        is ByteArray -> {
            this.contentToString()
        }
        is CharArray -> {
            this.contentToString()
        }
        is DoubleArray -> {
            this.contentToString()
        }
        is FloatArray -> {
            this.contentToString()
        }
        is IntArray -> {
            this.contentToString()
        }
        is LongArray -> {
            this.contentToString()
        }
        is ShortArray -> {
            this.contentToString()
        }
        is Array<*> -> {
            this.contentDeepToString()
        }
        else -> {
            throw IllegalArgumentException("Array has incompatible type: " + this.javaClass)
        }
    }
}
