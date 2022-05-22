package com.onestep.carcontrol

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.style.MaterialStyle
import com.onestep.carcontrol.broadcastreceiver.BluetoothBroadcastReceiver
import com.onestep.carcontrol.databinding.ActivityMainBinding


/**
 *@author OneStep
 * @description 蓝牙配置、连接、建立socket通讯...
 * @date 2022/05/22
 * */

class MainActivity : AppCompatActivity() {
    private val logTAG = "CarControl"
    private lateinit var binding: ActivityMainBinding
    //设置蓝牙
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothAbility: Boolean = false
    private var bluetoothOpen:Boolean = false
    private val requestCode = 0
    private lateinit var bluetoothBroadcastReceiver: BluetoothBroadcastReceiver


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //初始化DialogX弹窗
        initDialogX()
        //判断设备是否具有蓝牙功能
        checkBluetoothAbility()
        //扫描设备
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    /**
     * @description DialogX 弹窗全局配置初始化（主题样式...）
     * @doc https://github.com/kongzue/DialogX/wiki
     * */
    private fun initDialogX() {
        DialogX.init(this)
        DialogX.globalStyle = MaterialStyle.style()
    }

    /**
     * @description 判断设备是否支持蓝牙功能
     * @doc https://developer.android.google.cn/guide/topics/connectivity/bluetooth/setup
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBluetoothAbility(){
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            bluetoothAbility = true
            bluetoothOpen()//检查蓝牙是否开启
        } else {
            MessageDialog
                .show("错误", "此设备不具备蓝牙功能", "退出")
                .setOkButton { _, _ ->
                    finish()
                    return@setOkButton false
                }
                .isCancelable = false

        }
        Log.d(logTAG, "设备是否支持蓝牙功能： > > > $bluetoothAbility")
    }

    /**
     * @description 判断是否开启蓝牙，未开启则主动请求开启蓝牙功能
     * */
    @SuppressLint("MissingPermission")
    private fun bluetoothOpen(){
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, requestCode)
        } else {
            bluetoothOpen = true
            getPairedDevices()
        }
    }


    /**
     * @description 开启蓝牙Activity回调函数，判断是否开启成功
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(logTAG, "返回代码： > > > requestCode:$requestCode resultCode:$resultCode data:$data")
        when (resultCode) {
            -1 -> {
                //显示 2 秒
                PopTip.show("蓝牙开启成功").showLong()
            }
            0 -> {
                PopTip.show("蓝牙打开失败", "重试").showLong()
                    .setButton { _, _ -> //点击“重试”按钮回调
                        bluetoothOpen()
                        return@setButton false
                    }
            }

        }
    }

    /**
     *@description 查询已经配对的设备
     * */
    @SuppressLint("MissingPermission")
    private fun getPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            Log.d(logTAG, "已经配对的设备名: $deviceName  MAC地址： $deviceHardwareAddress")
        }
    }

    /**
     * @description 利用BroadcastReceiver扫描蓝牙设备
     * */
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }







}