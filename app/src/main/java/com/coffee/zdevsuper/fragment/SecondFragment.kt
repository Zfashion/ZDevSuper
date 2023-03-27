package com.coffee.zdevsuper.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.coffee.base.ui.BaseFragment
import com.coffee.zdevsuper.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment: BaseFragment<FragmentSecondBinding>() {

    override fun initData() {

    }

    override fun initView(vb: FragmentSecondBinding) {
        /*vb.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//            findNavController().popBackStack()
        }*/
    }
}