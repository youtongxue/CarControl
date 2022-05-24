//package com.onestep.carcontrol.broadcastreceiver
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import com.onestep.carcontrol.adapter.ScanBluetoothDeviceListAdapter
//import java.util.*
//
///**
// * @author OneStep
// * @description 扫描到外部蓝牙设备的BroadCastReceiver
// * @doc https://developer.android.google.cn/guide/topics/connectivity/bluetooth/find-bluetooth-devices
// * @date 2022/05/22
// * */
//class BluetoothBroadcastReceiver : BroadcastReceiver() {
//    private val logTAG = "broadCast"
//    private lateinit var mCurDevice: BluetoothDevice
//    //蓝牙搜索对话框相关
//    private val scanDialog: AlertDialog? = null
//    private val scanBluetoothDeviceListAdapter: ScanBluetoothDeviceListAdapter? = null
//    private val scanBluetoothDeviceList: MutableList<BluetoothDevice> = mutableListOf()//存放扫描到的蓝牙设备对象
//    //在Kotlin中listOf()为不可变列表，mutableListOf()为可变列表
//    private val scanBluetoothDeviceRssiList: MutableList<Short> = mutableListOf()//扫描到的蓝牙设备Rssi值
//
//    @SuppressLint("MissingPermission")
//    override fun onReceive(context: Context, intent: Intent) {
//        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
//            val action = intent.action
//            Log.d(logTAG, "Action received is $action")
//            //蓝牙搜索
//            if (BluetoothDevice.ACTION_FOUND == action) {
//                //实例化扫描到的设备，如果为空则结束
//                val scanDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
//
//                //获取到的扫描到的设备类型
//                val btType = scanDevice.type
//                //这儿设定的是，如果蓝牙设备为 低功耗设备，或者未知类型的蓝牙设备则return
//                if (btType == BluetoothDevice.DEVICE_TYPE_LE || btType == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {
//                    return
//                }
//                Log.d(logTAG, "scanBluetoothDevice name=" + scanDevice.name + " address=" + scanDevice.address
//                )
//                //向数列表添加蓝牙设备对象
//                scanBluetoothDeviceList.add(scanDevice)
//                val rssi = intent.extras!!.getShort(BluetoothDevice.EXTRA_RSSI)
//                scanBluetoothDeviceRssiList.add(rssi)
//                scanBluetoothDeviceListAdapter?.notifyDataSetChanged()
//            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
//                val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                if (mCurDevice != null && btDevice!!.address == mCurDevice.getAddress()) {
//                    val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
//                    if (state == BluetoothDevice.BOND_NONE) {
//                        showTip("已取消与设备" + btDevice!!.name + "的配对")
//                        mFlag = -1
//                    } else if (state == BluetoothDevice.BOND_BONDED) {
//                        showTip("与设备" + btDevice!!.name + "配对成功")
//                        mFlag = 1
//                    }
//                }
//            } else if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
//                val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
//                when (blueState) {
//                    BluetoothAdapter.STATE_TURNING_ON -> Log.i(
//                        com.example.bluetoothtool.Activity.BTClientActivity.TAG,
//                        "onReceive---------STATE_TURNING_ON"
//                    )
//                    BluetoothAdapter.STATE_ON -> {
//                        Log.i(
//                            com.example.bluetoothtool.Activity.BTClientActivity.TAG,
//                            "onReceive---------STATE_ON"
//                        )
//                        showTip("蓝牙当前状态：ON")
//                    }
//                    BluetoothAdapter.STATE_TURNING_OFF -> Log.i(
//                        com.example.bluetoothtool.Activity.BTClientActivity.TAG,
//                        "onReceive---------STATE_TURNING_OFF"
//                    )
//                    BluetoothAdapter.STATE_OFF -> {
//                        Log.i(
//                            com.example.bluetoothtool.Activity.BTClientActivity.TAG,
//                            "onReceive---------STATE_OFF"
//                        )
//                        showTip("蓝牙当前状态：OFF")
//                    }
//                }
//            }
//    }
//}