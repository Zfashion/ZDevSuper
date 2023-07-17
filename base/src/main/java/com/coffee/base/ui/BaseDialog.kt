package com.coffee.base.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.databinding.ViewDataBinding
import com.coffee.base.R
import com.coffee.base.utils.reflectBinding

/**
 * @Description: dialog基类，抽象类，供外部 dialog继承
 */
abstract class BaseDialog<VB: ViewDataBinding>(context: Context) : Dialog(context, R.style.Base_Default_Dialog_Style), BaseView<VB> {

    val mBinding: VB by lazy { reflectBinding(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(defineCancelable)
        setCanceledOnTouchOutside(defineCanceledOnTouchOutside)
        setContentView(mBinding.root)

        window?.let {
//            it.setWindowAnimations(defineWindowAnimationsStyle!!)
//            it.attributes.width = getWidth()
//            it.attributes.height = getHeight()
//            it.attributes.gravity = getGravity()
            it.attributes.gravity = getGravity()
//            onWindowAttributesChanged(it.attributes)

            if (defineFullScreen) {
                //设置状态栏沉浸式
                setImmersionBar(it)
                //设置遮罩背景颜色
                it.setBackgroundDrawable(ColorDrawable(context.resources.getColor(R.color.mask_color)))
                //设置占满全屏
                it.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            } else {
                it.setWindowAnimations(defineWindowAnimationsStyle!!)
                it.setDimAmount(0.4f)
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            //添加为系统全局弹框标识
            if (defineSystemAlertType) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                it.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
            }

        }



    }

    /*private fun dealWithHotWords() {
        VoiceHelper.getInstance().setDataPenetrateListener {
            CommonLogUtils.logD(TAG,"dealWithHotWords text="+it.title)
            findViewByText(it.title)?.performClick()
        }
    }*/

    override fun onStart() {
        super.onStart()
        initView(mBinding)
        initData()
        //dealWithHotWords()
    }

    override fun onStop() {
        super.onStop()
    }

    open val defineFullScreen: Boolean = true

    /**
     * 定义点击返回键是否可取消
     * 默认为 false
     */
    open var defineCancelable: Boolean = false

    /**
     * 定义点击弹框外部是否可取消
     * 默认为 false
     */
    open var defineCanceledOnTouchOutside: Boolean = false

    /**
     * 定义弹窗动画
     * 默认使用 安卓弹框的动画
     */
    @DrawableRes
    open var defineWindowAnimationsStyle: Int? = null
        get() {
            return field ?: android.R.style.Animation_Dialog
        }

    /**
     * 弹窗Gravity
     */
    open val defineGravity: Int = Gravity.CENTER

    /**
     * 是否设置为系统弹框
     */
    open val defineSystemAlertType: Boolean = false

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 弹窗宽度
     */
    open fun getWidth(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    /**
     * 弹窗高度
     */
    open fun getHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    /**
     * 弹窗Gravity
     */
    open fun getGravity(): Int {
        return Gravity.CENTER
    }

    private fun setImmersionBar(window: Window) {
        window.apply {
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.fitsSystemWindows = true
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }
}