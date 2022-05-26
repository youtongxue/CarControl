package com.onestep.carcontrol.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.onestep.carcontrol.R
import com.onestep.carcontrol.databinding.FragmentMenuOneBinding
import com.onestep.carcontrol.databinding.FragmentMenuSecondBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MenuSecondFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    //视图
    private lateinit var _binding: FragmentMenuSecondBinding
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMenuSecondBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuSecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}