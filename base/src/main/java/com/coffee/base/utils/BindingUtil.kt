package com.coffee.base.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.coffee.base.ui.BaseComponentView
import com.coffee.base.ui.BaseView
import java.lang.reflect.ParameterizedType

/**
 *
 * @Description: 针对组件 ViewDataBinding 编译类实现反射的工具类
 * @Author: ly-zfensheng
 * @CreateDate: 2022/6/9 11:25
 */


fun <VB: ViewDataBinding> BaseView<VB>.reflectBinding(layoutInflater: LayoutInflater): VB =
    reflectGenericBindingClass(this) {
        it.getDeclaredMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
    }.withLifecycleOwner(this)


fun <VB: ViewDataBinding> BaseView<VB>.reflectBinding(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean): VB =
    reflectGenericBindingClass(this) {
        it.getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
            .invoke(null, layoutInflater, parent, attachToParent) as VB
    }.withLifecycleOwner(this)




/**
 * 将 ViewDataBinding 类与组件绑定生命周期
 */
internal fun <VB: ViewBinding> VB.withLifecycleOwner(component: Any) = apply {
    when {
        this is ViewDataBinding && component is ComponentActivity -> lifecycleOwner = component
        this is ViewDataBinding && component is Fragment -> lifecycleOwner = component.viewLifecycleOwner
    }
}

/**
 * 具体针对 ViewDataBinding 泛型的类进行反射
 */
inline fun <VB: ViewDataBinding> BaseView<VB>.reflectGenericBindingClass(component: Any, block: (Class<VB>) -> VB): VB {
    var superclass = component.javaClass.superclass
    var genericSuperclass = component.javaClass.genericSuperclass

    typeCheck@ while (superclass != null) {
        if (genericSuperclass is ParameterizedType) {
            // 过滤此 Class 的 Type 数组，找到我们想要的 VB Type
            val vbList = genericSuperclass.actualTypeArguments.filterIsInstance<Class<VB>>()
            if (vbList.isEmpty()) continue@typeCheck
            runCatching {
                block.invoke(vbList[0])
            }.onSuccess {
                return it
            }.onFailure {
                throw it
            }
        }
        genericSuperclass = superclass.genericSuperclass
        superclass = superclass.superclass
    }

    throw IllegalArgumentException("ViewDataBinding 或者 ViewBinding 反射出错，先检查下是否已编译")
}