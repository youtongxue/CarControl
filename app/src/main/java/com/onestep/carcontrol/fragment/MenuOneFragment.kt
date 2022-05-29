package com.onestep.carcontrol.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.onestep.carcontrol.MainActivity
import com.onestep.carcontrol.R
import com.onestep.carcontrol.adapter.MenuItemAdapter
import com.onestep.carcontrol.adapter.PairedBluetoothAdapter
import com.onestep.carcontrol.adapter.ScanBluetoothDeviceAdapter
import com.onestep.carcontrol.databinding.FragmentMenuOneBinding
import com.onestep.carcontrol.entity.ScanBluetoothDevice
import com.onestep.carcontrol.myutils.MyUtils

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MenuOneFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapterScan: ScanBluetoothDeviceAdapter
    private lateinit var adapterPaired: PairedBluetoothAdapter


    //视图
    private lateinit var _binding: FragmentMenuOneBinding
    private val binding get() = _binding


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
        _binding = FragmentMenuOneBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuOneFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    /**
     * 初始化扫描到的新设备布局
     * */
    fun initScanDeviceRec(scanBluetoothDevice: MutableList<ScanBluetoothDevice>, context: Context) {
        //设置 RecyclerView的 布局方式（方向）
        val layManager = LinearLayoutManager(context)//实例化 LayoutManager
        layManager.orientation = LinearLayoutManager.VERTICAL
        _binding.scanDeviceRec.layoutManager = layManager
        //设置adapter
        adapterScan = ScanBluetoothDeviceAdapter(scanBluetoothDevice)
        _binding.scanDeviceRec.adapter = adapterScan


        //扫描列表设备点击监听事件
        adapterScan.setOnMyItemClickListener(object : ScanBluetoothDeviceAdapter.OnMyItemClickListener{
            override fun myClick(pos: Int) {
                Log.e("click", "点击了 $pos")
                if (activity != null) {
                    val mainActivity = activity as MainActivity
                    mainActivity.pairDevice(pos)
                }

            }

        })

    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshUI() {
        adapterScan.notifyDataSetChanged()

    }

    /**
     * 初始化已配对设备
     * */
    fun initPairedDeviceRec(pairedBluetoothDevice: MutableList<BluetoothDevice>, context: Context) {
        //设置 RecyclerView的 布局方式（方向）
        val layManager = LinearLayoutManager(context)//实例化 LayoutManager
        layManager.orientation = LinearLayoutManager.VERTICAL
        _binding.pairRec.layoutManager = layManager
        //设置adapter
        adapterPaired = PairedBluetoothAdapter(pairedBluetoothDevice)
        _binding.pairRec.adapter = adapterPaired



    }



}