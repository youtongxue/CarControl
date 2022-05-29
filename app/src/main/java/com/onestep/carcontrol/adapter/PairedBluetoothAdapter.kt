package com.onestep.carcontrol.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onestep.carcontrol.databinding.PairedbluetoothItemBinding

class PairedBluetoothAdapter(val bluetoothDevice: MutableList<BluetoothDevice>): RecyclerView.Adapter<PairedBluetoothAdapter.MyViewHolder>() {
    private var pairedBluetoothDeviceList: MutableList<BluetoothDevice> = bluetoothDevice//用来存放扫描到的蓝牙设备
    private var mContext: Context? = null

    inner class MyViewHolder(private val binding: PairedbluetoothItemBinding) : RecyclerView.ViewHolder(binding.root){

        val deviceImg: ImageView = binding.pairedType
        val deviceName: TextView = binding.pairedNameTxt
        val deviceRssi: ImageView = binding.pairedInfo
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PairedbluetoothItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.e("pairedAdapter", "传入列表长度为： ${pairedBluetoothDeviceList.size}")
        val device = pairedBluetoothDeviceList[position]

        holder.deviceName.text = device.name

        //设置Item OnClick回调
        var clickPosition = holder.layoutPosition //当前 点击Item 位置
        if (listener != null) {
            holder.itemView.setOnClickListener { listener!!.myClick(clickPosition) }
        }
    }

    override fun getItemCount(): Int = pairedBluetoothDeviceList!!.size


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