package com.coffee.base.utils

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.IntDef
import androidx.core.text.buildSpannedString
import com.coffee.base.BuildConfig
import com.coffee.base.ext.arrayToString
import com.coffee.base.ext.throwableToString
import java.util.*

/**
 *
 * @Description: Log工具类
 * @Author: ly-zfensheng
 * @CreateDate: 2022/6/10 14:39
 *
 * 部分设计参考Blankj的AndroidUtilCode
 */
object LogUtil {

    private const val LOG_VERBOSE = Log.VERBOSE
    private const val LOG_DEBUG = Log.DEBUG
    private const val LOG_INFO = Log.INFO
    private const val LOG_WARN = Log.WARN
    private const val LOG_ERROR = Log.ERROR

    @IntDef(value = [LOG_VERBOSE, LOG_DEBUG, LOG_INFO, LOG_WARN, LOG_ERROR])
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    internal annotation class LogLevel

    private const val NULL = "null"
    private const val BODY = "body= "
    private const val PLACEHOLDER = " "
    private const val CURRENT_THREAD = "CurrentThread= "
    private const val CALL_STACK = "CallStack= "

    private val LINE_SEP = System.getProperty("line.separator")
    private const val TOP_CORNER    = "┌"
    private const val MIDDLE_CORNER = "├"
    private const val LEFT_BORDER   = "│ "
    private const val BOTTOM_CORNER = "└"
    private const val SIDE_DIVIDER  =
        "────────────────────────────────────────────────────────————"
    private const val MIDDLE_DIVIDER =
        "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    private const val TOP_BORDER    = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
    private const val MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER
    private const val BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
    private const val MAX_LEN = 1100

    private var openLimits = true //针对release环境下是否开启Log限制

    fun v(vararg contents: Any?) {
        log(LOG_VERBOSE, *contents)
    }

    fun d(vararg contents: Any?) {
        log(LOG_DEBUG, *contents)
    }

    fun i(vararg contents: Any?) {
        log(LOG_INFO, *contents)
    }

    fun w(vararg contents: Any?) {
        log(LOG_WARN, *contents)
    }

    fun e(vararg contents: Any?) {
        log(LOG_ERROR, *contents)
    }

    /**
     * 关闭Release环境下的限制
     */
    fun closeLimitInRelease() { openLimits = false }

    /*private fun log(@LogLevel level: Int, msg: String) {
        if (!BuildConfig.DEBUG) return
        when (level) {
            LOG_VERBOSE -> Log.v(BuildConfig.GLOBAL_TAG, msg)
            LOG_DEBUG -> Log.d(BuildConfig.GLOBAL_TAG, msg)
            LOG_INFO -> Log.i(BuildConfig.GLOBAL_TAG, msg)
            LOG_WARN -> Log.w(BuildConfig.GLOBAL_TAG, msg)
            LOG_ERROR -> Log.e(BuildConfig.GLOBAL_TAG, msg)
        }
    }*/

    /**
     * Log调用生成的入口
     */
    private fun log(@LogLevel level: Int, vararg contents: Any?) {
        if (openLimits && !BuildConfig.DEBUG) return
        var globalTag = BuildConfig.GLOBAL_TAG
        if (globalTag.isEmpty()) globalTag = ""
        val tagHead = processTag(globalTag)
        val body = processContent(*contents)
        val headAndBody = combineHeadAndBody(tagHead.head, body)
        printAdjustBorder(level, tagHead.tag, headAndBody)
    }

    /**
     * 组合 Head 和 body 输出内容
     */
    private fun combineHeadAndBody(head: String?, body: String): String = buildSpannedString {
        append(PLACEHOLDER).append(LINE_SEP)
        append(TOP_BORDER).append(LINE_SEP)
        if (head != null) {
            append(LEFT_BORDER).append(head).append(LINE_SEP)
            append(MIDDLE_BORDER).append(LINE_SEP)
        }
        //分割换行的内容
        body.split(LINE_SEP).forEach {
            //分割超长的内容
            val len = it.length
            if (len > 200) {
                val i = len / 200
                repeat(i) { index ->
                    val begin = index * 200
                    val end = if (len - begin > 200) begin + 200 else len - begin
                    val substring = it.substring(begin, end)
                    append(LEFT_BORDER).append(substring).append(LINE_SEP)
                    if (index == i - 1 && len - end > 0) {
                        //补余
                        append(LEFT_BORDER).append(it.substring(end)).append(LINE_SEP)
                    }
                }
            } else {
                append(LEFT_BORDER).append(it).append(LINE_SEP)
            }
        }
        append(BOTTOM_BORDER)
    }.toString()

    /**
     * 输出合适范围
     */
    private fun printAdjustBorder(@LogLevel level: Int, tag: String, msg: String) {
        val length = msg.length
        val countSub = (length - BOTTOM_BORDER.length) / MAX_LEN
        if (countSub > 0) {
            printToLogCat(level, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER)
            var index = MAX_LEN
            repeat((1 until countSub).count()) {
                printToLogCat(level, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER +
                        LINE_SEP + LEFT_BORDER + msg.substring(index, index + MAX_LEN)
                        + LINE_SEP + BOTTOM_BORDER)
                index += MAX_LEN
            }
            if (index != length - BOTTOM_BORDER.length) {
                printToLogCat(level, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                        + LEFT_BORDER + msg.substring(index, length))
            }
        } else {
            printToLogCat(level, tag, msg)
        }
    }

    /**
     * 最终输出调用Log.println()
     */
    private fun printToLogCat(@LogLevel level: Int, tag: String, msg: String) {
        Log.println(level, tag, msg)
    }

    /**
     * 处理外部输入的内容，转为String
     */
    private fun processContent(vararg contents: Any?): String {
        return if (contents.size == 1) {
            formatContent(contents[0])
        } else {
            buildSpannedString {
                contents.forEachIndexed { index, any ->
                    append("$BODY[$index] = ${formatContent(any)}")
                    if (index != contents.lastIndex) append(LINE_SEP)
                }
            }.toString()
        }
    }

    /**
     * 处理Tag，转为当前调用log的类方法栈和行数
     */
    private fun processTag(tag: String): TagHead {
        val stackTrace = Throwable().stackTrace
        val stackIndex = 3 //本方法向上的方法栈目前为3层
        if (stackIndex >= stackTrace.size) {
            val traceElement = stackTrace[3]
            var fileName = getTraceFileName(traceElement)
            val index = fileName.indexOf('.')
            fileName = if (index != -1) fileName.substring(0, index) else fileName
            return TagHead(tag + fileName, null)
        }
        val traceElement = stackTrace[stackIndex]
        val fileName = getTraceFileName(traceElement)
        val tName = "$CURRENT_THREAD${Thread.currentThread().name}"
        val head = Formatter().format("[%s], [${CALL_STACK}%s.%s(%s:%d)]",
            tName,
            traceElement.className,
            traceElement.methodName,
            fileName,
            traceElement.lineNumber
        ).toString()
        return TagHead(tag, head)
    }

    private fun formatContent(content: Any?): String {
        return if (content == null) NULL
        else anyToString(content)
    }

    private fun anyToString(content: Any): String {
        return if (content.javaClass.isArray) content.arrayToString()
        else if (content is Throwable) content.throwableToString()
        else if (content is Bundle) bundle2String(content)
        else if (content is Intent) intent2String(content)
        else content.toString()
    }

    private fun getTraceFileName(element: StackTraceElement): String {
        return element.fileName ?: getTraceClassName(element)
    }

    private fun getTraceClassName(element: StackTraceElement): String {
        var className = element.className
        val classNameInfo = className.split("\\.")
        if (classNameInfo.isNotEmpty()) {
            className = classNameInfo.last()
        }
        val index = className.indexOf('$')
        if (index != -1) {
            className = className.substring(0, index)
        }
        return "${className}.-"
    }

    private fun bundle2String(bundle: Bundle): String {
        val iterator = bundle.keySet().iterator()
        if (iterator.hasNext().not()) return "Bundle {null}"

        val sb = StringBuilder()
        sb.append("Bundle { ")
        while(true) {
            val key = iterator.next()
            sb.append("key= ").append(key).append(" : ")
            val value = bundle.get(key)
            val printValue = if (value is Bundle) {
                if (value == bundle) "(this Bundle)" else bundle2String(value)
            } else {
                formatContent(value)
            }
            sb.append("value= $printValue")
            if (iterator.hasNext().not()) return sb.append(" }").toString()
            sb.append(", ")
        }
    }

    private fun intent2String(intent: Intent): String = buildSpannedString {
        append("Intent { ")
        var first = true
        val mAction = intent.action
        if (mAction != null) {
            append("act=").append(mAction)
            first = false
        }
        val mCategories = intent.categories
        if (mCategories != null) {
            if (!first) append(' ')
            first = false
            append("cat=[")
            var firstCategory = true
            for (c in mCategories) {
                if (!firstCategory) append(',')
                append(c)
                firstCategory = false
            }
            append("]")
        }
        val mData = intent.data
        if (mData != null) {
            if (!first) append(' ')
            first = false
            append("dat=$mData")
        }
        val mType = intent.type
        if (mType != null) {
            if (!first) append(' ')
            first = false
            append("typ=$mType")
        }
        val mFlags = intent.flags
        if (mFlags != 0) {
            if (!first) append(' ')
            first = false
            append("flg=0x").append(Integer.toHexString(mFlags))
        }
        val mPackage = intent.getPackage()
        if (mPackage != null) {
            if (!first) append(' ')
            first = false
            append("pkg=$mPackage")
        }
        val mComponent = intent.component
        if (mComponent != null) {
            if (!first) append(' ')
            first = false
            append("cmp=").append(mComponent.flattenToShortString())
        }
        val mSourceBounds = intent.sourceBounds
        if (mSourceBounds != null) {
            if (!first) append(' ')
            first = false
            append("bnds=").append(mSourceBounds.toShortString())
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val mClipData = intent.clipData
            if (mClipData != null) {
                if (!first) {
                    append(' ')
                }
                first = false
                clipData2String(mClipData, sb)
            }
        }*/
        val mExtras = intent.extras
        if (mExtras != null) {
            if (!first) append(' ')
            first = false
            append("extras={")
            append(bundle2String(mExtras))
            append('}')
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            val mSelector = intent.selector
            if (mSelector != null) {
                if (!first) append(' ')
                append("sel={")
                append(if (mSelector === intent) "(this Intent)" else intent2String(mSelector))
                append("}")
            }
        }
        append(" }")
    }.toString()

    internal class TagHead constructor(val tag: String, val head: String?)

}