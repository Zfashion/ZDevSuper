package com.coffee.zdevsuper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 *
 * @Description: 类作用描述
 * @Author: ly-zfensheng
 * @CreateDate: 2022/12/20 11:46
 */
class MainViewModel: ViewModel() {

    fun test() {
        viewModelScope.launch {
        }
    }
}