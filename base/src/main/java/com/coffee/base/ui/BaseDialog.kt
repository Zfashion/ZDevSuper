package com.coffee.base.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.databinding.ViewDataBinding
import com.coffee.base.R
import com.coffee.base.utils.reflectBinding

/**
 * @Description: dialog基类，抽象类，供外部 dialog继承
 * @Author: liwei
 * @CreateDate: 2022/6/16
 * @Fix by ly-zfensheng，修改Dialog基类的实现
 */
abstract class BaseDialog<VB: ViewDataBinding>(context: Context) : Dialog(context, R.style.Base_Default_Dialog_Style), BaseView<VB> {

    val mBinding: VB by lazy { reflectBinding(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(defineCancelable)
        setCanceledOnTouchOutside(defineCanceledOnTouchOutside)
        setContentView(mBinding.root)

        window?.let {
            it.setWindowAnimations(defineWindowAnimationsStyle!!)
            if (openDimEnabled) {
                it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            } else {
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            //it.setBackgroundDrawableResource(getBackgroundDrawableResourceId())
            it.attributes.width = getWidth()
            it.attributes.height = getHeight()
            it.attributes.gravity = getGravity()
            onWindowAttributesChanged(it.attributes)
        }

        initView(mBinding)
    }


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
     * 窗口暗淡
     */
    open var openDimEnabled: Boolean = false

    /**
     * 弹窗宽度
     */
    open fun getWidth(): Int {
        return WRAP_CONTENT
    }

    /**
     * 弹窗高度
     */
    open fun getHeight(): Int {
        return WRAP_CONTENT
    }

    /**
     * 弹窗Gravity
     */
    open fun getGravity(): Int {
        return CENTER
    }

    /**
     * 窗口背景资源
     */
    open fun getBackgroundDrawableResourceId(): Int {
        return Color.TRANSPARENT
    }


    /**
     * 初始化视图
     */
//    abstract fun initView(binding: V)

    /**
     * 初始化数据
     */
//    abstract fun initData()
}