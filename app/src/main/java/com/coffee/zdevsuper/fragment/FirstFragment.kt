package com.coffee.zdevsuper.fragment

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.coffee.base.ui.BaseFragment
import com.coffee.zdevsuper.R
import com.coffee.zdevsuper.ui.coroutine.CoroutineActivity
import com.coffee.zdevsuper.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment: BaseFragment<FragmentFirstBinding>() {

    override fun initData() {
    }

    override fun initView(vb: FragmentFirstBinding) {
        vb.buttonFirst.setOnClickListener {
//            val intent = Intent(requireContext(), CoroutineActivity::class.java)
//            startActivity(intent)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }
}