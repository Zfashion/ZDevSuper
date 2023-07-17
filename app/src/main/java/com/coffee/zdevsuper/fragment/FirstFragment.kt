package com.coffee.zdevsuper.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.coffee.base.ui.BaseFragment
import com.coffee.zdevsuper.databinding.FragmentFirstBinding
import com.coffee.zdevsuper.ui.dialog.GuideVideoDialog

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment: BaseFragment<FragmentFirstBinding>() {



    override fun initData() {
    }

    override fun initView(vb: FragmentFirstBinding) {



        vb.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)



        }
    }
}