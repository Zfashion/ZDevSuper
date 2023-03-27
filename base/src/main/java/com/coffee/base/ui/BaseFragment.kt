package com.coffee.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.coffee.base.utils.reflectBinding
import kotlinx.coroutines.launch

/**
 *
 * @Description: 抽象类，供外部 Fragment 继承
 * @Author: ly-zfensheng
 * @CreateDate: 2022/6/9 14:26
 */
abstract class BaseFragment<VB: ViewDataBinding>: Fragment(), BaseComponentView<VB> {

    lateinit var mBinding: VB
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = reflectBinding(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(mBinding)
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mBinding.isInitialized) mBinding.unbind()
    }

}