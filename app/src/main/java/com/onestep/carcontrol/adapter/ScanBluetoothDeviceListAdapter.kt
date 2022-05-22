package com.onestep.carcontrol.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.onestep.carcontrol.R

/***
 *@author OneStep
 * @date 2022/05/22
 * @description 扫描出蓝牙设备需要显示到ListView或者RecyclerView的Adapter
 */

class ScanBluetoothDeviceListAdapter: BaseAdapter() {
    private var scanBluetoothDeviceList: List<BluetoothDevice>? = null//用来存放扫描到的蓝牙设备
    private var scanBluetoothDeviceRssiList: List<Short>? = null//存放扫描到的蓝牙设备Rssi信号强度值
    private var mContext: Context? = null
    private val curPosition = 0


    @SuppressLint("NotConstructor")
    fun ScanBluetoothDeviceListAdapter(scanBluetoothDeviceList: List<BluetoothDevice>?, scanBluetoothDeviceRssiList: List<Short>?, mContext: Context?) {
        this.scanBluetoothDeviceList = scanBluetoothDeviceList
        this.scanBluetoothDeviceRssiList = scanBluetoothDeviceRssiList
        this.mContext = mContext
    }

    override fun getCount(): Int {
        return scanBluetoothDeviceList?.size ?: 0
    }

    override fun getItem(position: Int): Any? {
        return scanBluetoothDeviceList?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "MissingPermission")
    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val convertView = LayoutInflater.from(mContext).inflate(R.layout.item_scan,viewGroup,false)
        val txtName: TextView = convertView.findViewById(R.id.txtName)
        val txtMac: TextView = convertView.findViewById(R.id.txtMac)
        val txtState: TextView = convertView.findViewById(R.id.txtState)
        val txtRssi: TextView = convertView.findViewById(R.id.txtRssi)
        val device: BluetoothDevice? = scanBluetoothDeviceList?.get(position)

        txtName.text = device?.name
        txtMac.text = device?.address
        txtRssi.text = scanBluetoothDeviceRssiList?.get(position).toString()

        if (device?.bondState != BluetoothDevice.BOND_BONDED) {
            txtState.text = "未匹配"
        } else if (device.bondState == BluetoothDevice.BOND_BONDED) {
            txtState.text = "已匹配"
        }

        return convertView
    }

}