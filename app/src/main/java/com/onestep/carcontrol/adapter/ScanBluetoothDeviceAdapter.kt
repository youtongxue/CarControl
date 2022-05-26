package com.onestep.carcontrol.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onestep.carcontrol.databinding.BluetoothdeviceItemBinding
import com.onestep.carcontrol.entity.ScanBluetoothDevice

class ScanBluetoothDeviceAdapter(val bluetoothDevice: List<ScanBluetoothDevice>): RecyclerView.Adapter<ScanBluetoothDeviceAdapter.MyViewHolder>() {
    private var scanBluetoothDeviceList: List<ScanBluetoothDevice> = bluetoothDevice//用来存放扫描到的蓝牙设备
    private var mContext: Context? = null

    inner class MyViewHolder(private val binding: BluetoothdeviceItemBinding) : RecyclerView.ViewHolder(binding.root){
        val deviceName: TextView = binding.deviceName
        val deviceImg: ImageView = binding.deviceImg
        val deviceRssi: TextView = binding.deviceRssi
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = BluetoothdeviceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val device = scanBluetoothDeviceList[position]

        holder.deviceName.text = device.name
        holder.deviceRssi.text = device.rssi.toString()

        //设置Item OnClick回调
        var clickPosition = holder.layoutPosition //当前 点击Item 位置
        if (listener != null) {
            holder.itemView.setOnClickListener { listener!!.myClick(clickPosition) }
        }


    }

    override fun getItemCount(): Int = scanBluetoothDeviceList!!.size


    /**
     * 设置item的监听事件的接口
     */
    interface OnMyItemClickListener {
        fun myClick(pos: Int)
    }
    //需要外部访问，所以需要设置set方法，方便调用
    private var listener: OnMyItemClickListener? = null
    fun setOnMyItemClickListener(listener: OnMyItemClickListener?) {
        this.listener = listener
    }





}