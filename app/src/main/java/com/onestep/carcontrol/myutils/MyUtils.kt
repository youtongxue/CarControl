package com.onestep.carcontrol.myutils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi


/**
 * @author OneStep
 * @description 工具类
 * @date 2022/05/24
 * */
object MyUtils {
    private const val logTAG = "MyUtils"
    //设置蓝牙
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null

    /**
     * 判断定位功能是否开启
     * */
    fun locationOpen(mContext: Context): Boolean {
        val locationManager: LocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network: Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (gps || network){
            return true
        }

        return false
    }

    /**
     * 判断设备是否支持蓝牙功能
     * https://developer.android.google.cn/guide/topics/connectivity/bluetooth/setup
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkBluetoothAbility(mContext: Context): Boolean {
        bluetoothManager = mContext.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            return true
        }

        return false
    }

    /**
     * 判断是否开启蓝牙功能
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    fun bluetoothOpen(mContext: Context): Boolean {
        bluetoothManager = mContext.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter?.isEnabled!!) {
            return true
        }

        return false
    }

    //dp, px 转换工具
    fun dp2px(context: Context, dp: Double): Double =dp * context.resources.displayMetrics.density
    fun px2dp(context: Context,px:Float):Float =px / context.resources.displayMetrics.density

    fun utilToast(context: Context, string: String) {
        Toast.makeText(context,string, Toast.LENGTH_SHORT).show()
    }

    /**
     * 蓝牙配对绑定
     * @param dev
     * @return
     */
    fun createBond(dev: BluetoothDevice?): Boolean {
        try {
            val createBondMethod = BluetoothDevice::class.java.getMethod("createBond")
            return createBondMethod.invoke(dev) as Boolean
        } catch (e: Exception) {
            Log.e(logTAG, "建立配对方法失败 > > > ${e.printStackTrace().toString()}")
        }
        return false
    }




}