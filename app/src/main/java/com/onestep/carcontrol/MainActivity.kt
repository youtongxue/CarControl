package com.onestep.carcontrol

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.style.MaterialStyle
import com.onestep.carcontrol.adapter.MenuItemAdapter
import com.onestep.carcontrol.adapter.ScanBluetoothDeviceListAdapter
import com.onestep.carcontrol.databinding.ActivityMainBinding
import com.onestep.carcontrol.myutils.MyUtils
import androidx.activity.result.ActivityResultLauncher as ActivityResultLauncher


/**
 *@author OneStep
 * @description 蓝牙配置、连接、建立socket通讯...
 * @date 2022/05/22
 * */

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val logTAG = "CarControl"
    private lateinit var binding: ActivityMainBinding
    private lateinit var mContext: Context
    //设置蓝牙
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var bluetoothBroadcastReceiver: BluetoothBroadcastReceiver
    //打开设置
    private lateinit var bluetoothLauncher: ActivityResultLauncher<Intent>
    private lateinit var gpsLauncher: ActivityResultLauncher<Intent>
    private val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    private val enableGPSIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

    private var menu:Boolean = false
    private lateinit var menuLayout: ConstraintLayout.LayoutParams
    private lateinit var statueLayout: ConstraintLayout.LayoutParams
    private var menuItemList: MutableList<Drawable> = ArrayList() //EmojiMini 图片


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mContext = this
        binding.btnMenu.setOnClickListener(this)
        binding.scanDeviceBtn.setOnClickListener(this)
        binding.closeBtn.setOnClickListener(this)


        enableBluetooth()
        enableGPS()
        //初始化DialogX弹窗
        initDialogX()
        //布局margin
        menuLayout = binding.emojiLinear.layoutParams as ConstraintLayout.LayoutParams
        statueLayout = binding.infoRel.layoutParams as ConstraintLayout.LayoutParams

        initMenuItem()

        //注册蓝牙扫描接收广播
        bluetoothBroadcastReceiver = BluetoothBroadcastReceiver()
        val filter1 = IntentFilter()
        filter1.addAction(BluetoothDevice.ACTION_FOUND)
        filter1.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        filter1.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        mContext.registerReceiver(bluetoothBroadcastReceiver, filter1)

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
     * 判断设备是否支持蓝牙功能,蓝牙是否打开
     * */
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBluetoothAbility(){

        if (MyUtils.checkBluetoothAbility(mContext)) {
            //支持蓝牙功能
            //获取BluetoothAdapter实列
            bluetoothManager = mContext.getSystemService(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager.adapter

            if (!MyUtils.bluetoothOpen(mContext)) {
                //未开启蓝牙功能，请求打开蓝牙
                bluetoothLauncher.launch(enableBtIntent)
            }

            if (!MyUtils.locationOpen(mContext)) {
                //未开定位功能，请求打开定位
                gpsLauncher.launch(enableGPSIntent)
            }

        } else {
            MessageDialog
                .show("错误", "此设备不具备蓝牙功能", "退出")
                .setOkButton { _, _ ->
                    finish()
                    return@setOkButton false
                }
                .isCancelable = false

        }
        Log.d(logTAG, "设备是否支持蓝牙功能： > > > ${MyUtils.checkBluetoothAbility(mContext)} 是否开启蓝牙： ${MyUtils.bluetoothOpen(mContext)} 是否开启定位： ${MyUtils.locationOpen(mContext)}")
    }

    /**
     * 开启蓝牙
     * */
    private fun enableBluetooth() {
        bluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            Log.e(logTAG, "开启蓝牙返回码为： ${it.resultCode}")
            when (it.resultCode) {
                -1 -> {
                    //显示 2 秒
                    PopTip.show("蓝牙开启成功").showLong()
                }
                0 -> {
                    PopTip.show("蓝牙打开失败", "重试").showLong()
                        .setButton { _, _ -> //点击“重试”按钮回调
                            bluetoothLauncher.launch(enableBtIntent)
                            return@setButton false
                        }
                }
            }
        }
    }

    /**
     * 开启定位
     * */
    private fun enableGPS() {
        gpsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.e(logTAG, "开启定位返回码为： ${it.resultCode} data: ${it.data}")
            //resultCode无论是开启还是未开启 都是 0

            if (MyUtils.locationOpen(mContext)) {
                PopTip.show("定位开启成功").showLong()
            } else {
                PopTip.show("定位打开失败", "重试").showLong()
                        .setButton { _, _ -> //点击“重试”按钮回调
                            gpsLauncher.launch(enableGPSIntent)
                            return@setButton false
                        }
            }

//            when (it.resultCode) {
//                -1 -> {
//                    //显示 2 秒
//                    PopTip.show("定位开启成功").showLong()
//                }
//                0 -> {
//                    PopTip.show("定位打开失败", "重试").showLong()
//                        .setButton { _, _ -> //点击“重试”按钮回调
//                            gpsLauncher.launch(enableGPSIntent)
//                            return@setButton false
//                        }
//                }
//            }
        }
    }


    /**询已经配对的设备
     *@description 查询以配对设备
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
     * @author OneStep
     * @description 扫描到外部蓝牙设备的BroadCastReceiver
     * @doc https://developer.android.google.cn/guide/topics/connectivity/bluetooth/find-bluetooth-devices
     * @date 2022/05/22
     * */
    class BluetoothBroadcastReceiver : BroadcastReceiver() {
        private var mFlag: Int = 0
        private val logTAG = "broadCast"
        private lateinit var mCurDevice: BluetoothDevice
        //蓝牙搜索对话框相关
        private val scanDialog: AlertDialog? = null
        private val scanBluetoothDeviceListAdapter: ScanBluetoothDeviceListAdapter? = null
        private val scanBluetoothDeviceList: MutableList<BluetoothDevice> = mutableListOf()//存放扫描到的蓝牙设备对象
        //在Kotlin中listOf()为不可变列表，mutableListOf()为可变列表
        private val scanBluetoothDeviceRssiList: MutableList<Short> = mutableListOf()//扫描到的蓝牙设备Rssi值

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
            val action: String? = intent.action
            Log.e(logTAG, "Action received is $action")
            //蓝牙搜索
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //实例化扫描到的设备，如果为空则结束
                    val scanDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return

                    //获取到的扫描到的设备类型
                    val btType = scanDevice.type
                    //这儿设定的是，如果蓝牙设备为 低功耗设备，或者未知类型的蓝牙设备则return
                    if (btType == BluetoothDevice.DEVICE_TYPE_LE || btType == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {
                        return
                    }
                    Log.e(logTAG, "scanBluetoothDevice name=" + scanDevice.name + " address=" + scanDevice.address
                    )
                    //向数列表添加蓝牙设备对象
                    scanBluetoothDeviceList.add(scanDevice)
                    val rssi = intent.extras!!.getShort(BluetoothDevice.EXTRA_RSSI)
                    scanBluetoothDeviceRssiList.add(rssi)
                    scanBluetoothDeviceListAdapter?.notifyDataSetChanged()
                }

                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (mCurDevice != null && btDevice!!.address == mCurDevice.address) {
                        val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                        if (state == BluetoothDevice.BOND_NONE) {
                            //showTip("已取消与设备" + btDevice!!.name + "的配对")
                            Log.e(logTAG, "已取消与设备" + btDevice!!.name + "的配对")
                            mFlag = -1
                        } else if (state == BluetoothDevice.BOND_BONDED) {
                            //showTip("与设备" + btDevice!!.name + "配对成功")
                            Log.e(logTAG, "与设备" + btDevice!!.name + "配对成功")
                            mFlag = 1
                        }
                    }
                }

                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                    when (blueState) {
                        BluetoothAdapter.STATE_TURNING_ON -> Log.i(logTAG, "onReceive---------STATE_TURNING_ON")
                        BluetoothAdapter.STATE_ON -> {
                            Log.e(logTAG, "onReceive---------STATE_ON")
                            //showTip("蓝牙当前状态：ON")

                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> Log.i(logTAG, "onReceive---------STATE_TURNING_OFF")
                        BluetoothAdapter.STATE_OFF -> {
                            Log.e(logTAG, "onReceive---------STATE_OFF")
                            //showTip("蓝牙当前状态：OFF")
                        }
                    }
                }
            }
        }
    }


    /**
     * 扫描蓝牙设备
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    fun scanBluetoothDevice() {
        //判断设备是否支持蓝牙功能，是否开启蓝牙
        checkBluetoothAbility()
        bluetoothAdapter?.startDiscovery()

    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onClick(view: View?) {
        when (view?.id) {
            binding.scanDeviceBtn.id -> {

                //检测是否有定位权限
                // 判断一个或多个权限是否全部授予了
                Log.e(logTAG, "是否拥有定位权限： FINE: ${XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION)} COARSE: ${XXPermissions.isGranted(this, Permission.ACCESS_COARSE_LOCATION)}")

                if (!XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION) || !XXPermissions.isGranted(this, Permission.ACCESS_COARSE_LOCATION)) {
                    val permissionList: List<String> = listOf(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION)
                    XXPermissions.with(this)
                        //申请单个权限
                        .permission(permissionList)
                        .request(object : OnPermissionCallback {

                            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                                if (all) {
                                    Toast.makeText(this@MainActivity, "获取GSP、网络定位权限成功", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                                if (never) {
                                    Toast.makeText(this@MainActivity, "被永久拒绝授权，请手动授予定位权限", Toast.LENGTH_LONG).show()
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                                } else {
                                    Toast.makeText(this@MainActivity, "获取定位权限失败", Toast.LENGTH_LONG).show()
                                }
                            }
                        })

                } else {
                    //拥有权限就开始开启扫描设备
                    scanBluetoothDevice()
                }
            }

            binding.btnMenu.id,  binding.closeBtn.id-> {
                //判断点击menu
                if (!menu) {
                    //

                    menuLayout.marginEnd = 0
                    binding.emojiLinear.layoutParams = menuLayout //使layout更新

                    statueLayout.topMargin = -(MyUtils.dp2px(this, 40.0).toInt())
                    binding.infoRel.layoutParams = statueLayout

                    menu = true

                } else {

                    menuLayout.marginEnd = -(MyUtils.dp2px(this, 340.5).toInt())
                    binding.emojiLinear.layoutParams = menuLayout //使layout更新

                    statueLayout.topMargin = 0
                    binding.infoRel.layoutParams = statueLayout

                    menu = false
                }
            }
        }
    }


    /**
     * 初始化menu item
     * */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun initMenuItem() {
        var temp: Int = 0
        //设置 RecyclerView的 布局方式（方向）
        val layManager = LinearLayoutManager(this)//实例化 LayoutManager
        layManager.orientation = LinearLayoutManager.VERTICAL
        binding.emojiminiRcy.layoutManager = layManager

        //设置adapter
        menuItemList.add(getDrawable(R.drawable.ic_blue)!!)
        menuItemList.add(getDrawable(R.drawable.ic_battery)!!)
        menuItemList.add(getDrawable(R.drawable.ic_hd)!!)
        menuItemList.add(getDrawable(R.drawable.ic_control)!!)
        menuItemList.add(getDrawable(R.drawable.ic_more)!!)

        val adapter = MenuItemAdapter(this, menuItemList)
        binding.emojiminiRcy.adapter = adapter

        (binding.emojiminiRcy.itemAnimator as DefaultItemAnimator).removeDuration = 2

        //底部 emoji 预览 RecyclerView Item点击监听
        adapter.setOnMyItemClickListener(object : MenuItemAdapter.OnMyItemClickListener {
            override fun myClick(pos: Int) {
                MyUtils.utilToast(this@MainActivity, "点击 ： $pos")
                binding.emojiViewpager.currentItem = pos//ViewPager滑动到指定页面， 在 ViewPager 的滑动监听回调方法 OnPageChangeCallback -> 做RecyclerView Item修改背景操作
                binding.emojiminiRcy.smoothScrollToPosition(pos + 1)//RecyclerView滑动到选择项
                temp = pos//设置当前选中页，在refreshBg方法中 更新单个Item -> notifyItemChange() 需要，若 全部更新 -> notifyDataSetChanged() 则不需要设置

            }

        })

        //ViewPager 滑动监听回调
        binding.emojiViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.e(" >>    >>", "被调用")
                MyUtils.utilToast(this@MainActivity, "滑动到 $position 页")
                adapter.refreshBg(position, temp)//向RecyclerView Adapter传入当前ViewPager选中页位置
                temp = position
                binding.emojiminiRcy.smoothScrollToPosition(position + 1)
            }

            //删除了另两个方法 override fun onPageScrollStateChanged , override fun onPageScrolled
        })

    }

    /**
     * 初始化 menuFragment
     * */
    //初始化，表情选择的 fragment 页面
    private fun initFragment() {
        val list: MutableList<Fragment> = ArrayList()
        list.add(EmojiOneFragment())
        list.add(EmojiTwoFragment())
        //设置预加载的Fragment页面数量，可防止流式布局StaggeredGrid数组越界错误。
        //binding.emojiViewpager.setOffscreenPageLimit(list.size - 1) //设置适配器
        val adapter: FragmentStateAdapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return list[position]
            }

            override fun getItemCount(): Int {
                return list.size
            }
        }
        binding.emojiViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.emojiViewpager.adapter = adapter //把适配器添加给ViewPager2
    }


    /**
     * 关闭广播服务
     * */
    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        //unregisterReceiver(receiver)
        mContext.unregisterReceiver(bluetoothBroadcastReceiver)
    }

}