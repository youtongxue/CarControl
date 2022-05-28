package com.onestep.carcontrol

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.style.MaterialStyle
import com.onestep.carcontrol.adapter.MenuItemAdapter
import com.onestep.carcontrol.databinding.ActivityMainBinding
import com.onestep.carcontrol.entity.ScanBluetoothDevice
import com.onestep.carcontrol.fragment.MenuOneFragment
import com.onestep.carcontrol.fragment.MenuSecondFragment
import com.onestep.carcontrol.myutils.MyUtils
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread


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
    private var scanBluetoothDeviceList: MutableList<BluetoothDevice> = mutableListOf()//存放扫描到的蓝牙设备对象
    private var mFlag = 0 //0:表示测试中，1：表示成功，-1：表示失败
    private lateinit var mPairDevice: BluetoothDevice//需要配对的设备

    //打开设置
    private lateinit var bluetoothLauncher: ActivityResultLauncher<Intent>
    private lateinit var gpsLauncher: ActivityResultLauncher<Intent>
    private val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    private val enableGPSIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

    private var menu:Boolean = false
    private lateinit var menuLayout: ConstraintLayout.LayoutParams
    private lateinit var statueLayout: ConstraintLayout.LayoutParams
    private var menuItemList: MutableList<Drawable> = ArrayList() //EmojiMini 图片
    private var temp: Int = 0
    private var menuFragmentOne: Fragment? = null
    private var menuFragmentTwo: Fragment? =null
    private lateinit var transaction: FragmentTransaction
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentOne: MenuOneFragment



    //handler
    public val mBaseHandler: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {

            }
        }
    }

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
        binding.sendBtn.setOnClickListener(this)


        enableBluetooth()
        enableGPS()
        //初始化DialogX弹窗
        initDialogX()
        //布局margin
        menuLayout = binding.emojiLinear.layoutParams as ConstraintLayout.LayoutParams
        statueLayout = binding.infoRel.layoutParams as ConstraintLayout.LayoutParams

        //fragment
        fragmentManager = supportFragmentManager


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
    inner class BluetoothBroadcastReceiver : BroadcastReceiver() {
        private val logTAG = "broadCast"
        //蓝牙搜索相关
        private var device: ScanBluetoothDevice? = null //自定义蓝牙设备信息实体类
        private val myScanBlueList: MutableList<ScanBluetoothDevice> = mutableListOf()//存放扫描到的蓝牙设备对象
        private val newDeviceList: MutableList<ScanBluetoothDevice> = mutableListOf()//暂存新的设备


        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.e(logTAG, "Action received is $action")
            //蓝牙搜索
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //实例化扫描到的设备，如果为空则结束
                    val scanDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return

                    //扫描到的设备信息
                    val type = scanDevice.type
                    val name = if (scanDevice.name == null) { return } else { scanDevice.name }
                    val address = scanDevice.address
                    val rssi = intent.extras!!.getShort(BluetoothDevice.EXTRA_RSSI)

                    //这儿设定的是，如果蓝牙设备为 低功耗设备，或者未知类型的蓝牙设备则return
                    if (type == BluetoothDevice.DEVICE_TYPE_LE || type == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {
                        return
                    }
                    device = ScanBluetoothDevice(name, address, rssi, type)
                    Log.e(logTAG, "扫描到设备： name=$name address=$address rssi=$rssi type=$type")

                    //过滤掉重复设备
                    if (myScanBlueList.size == 0) {
                        //向数列表添加蓝牙设备对象
                        myScanBlueList.add(device!!)
                        fragmentOne = fragmentManager.findFragmentById(R.id.menu_layout) as MenuOneFragment
                        fragmentOne.initScanDeviceRec(myScanBlueList, context)//初始化设备RecyclerView布局
                    }else {
                        var newDevice: ScanBluetoothDevice? = null
                        myScanBlueList.forEach {
                            if (it.address != address) {
                                newDevice = device!!
                            } else {
                                return
                            }
                        }
                        myScanBlueList.add(newDevice!!)
                        scanBluetoothDeviceList.add(scanDevice)
                        fragmentOne.refreshUI()//刷新RecyclerView
                    }
                }

                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (mPairDevice != null && btDevice!!.address == mPairDevice.address) {
                        val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                        if (state == BluetoothDevice.BOND_NONE) {

                            Log.e(logTAG, "已取消与设备" + btDevice!!.name + "的配对")
                            mFlag = -1
                        } else if (state == BluetoothDevice.BOND_BONDED) {

                            Log.e(logTAG, "与设备" + btDevice!!.name + "配对成功")
                            mFlag = 1
                        }
                    }
                }

                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                    when (blueState) {
                        BluetoothAdapter.STATE_TURNING_ON -> Log.i(logTAG, "蓝牙状态---------STATE_TURNING_ON")
                        BluetoothAdapter.STATE_ON -> {
                            Log.e(logTAG, "蓝牙状态改变---------ON")
                            //showTip("蓝牙当前状态：ON")

                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> Log.i(logTAG, "蓝牙状态---------STATE_TURNING_OFF")
                        BluetoothAdapter.STATE_OFF -> {
                            Log.e(logTAG, "蓝牙状态改变---------OFF")
                            //showTip("蓝牙当前状态：OFF")
                        }
                    }
                }
            }
        }

        fun refreshUI(scanBluetoothDevice: MutableList<ScanBluetoothDevice>) {
            val msg = Message.obtain()
            msg.what = 1
            msg.obj = scanBluetoothDevice
            mBaseHandler.sendMessage(msg)
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
            //扫描蓝牙设备
            binding.scanDeviceBtn.id -> {

                //检测是否有定位权限
                // 判断一个或多个权限是否全部授予了
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

            //弹出扫描蓝牙设备菜单
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

            //发送数据
            binding.sendBtn.id -> {

            }}
        }

    /**
     * 初始化menu item
     * */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun initMenuItem() {
        //设置默认fragment
        changeFragment(0)

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

        //对扫描到的设备设置点击事件
        adapter.setOnMyItemClickListener(object : MenuItemAdapter.OnMyItemClickListener {
            override fun myClick(pos: Int) {
                MyUtils.utilToast(this@MainActivity, "点击 ： $pos")
                changeFragment(pos)
                binding.emojiminiRcy.smoothScrollToPosition(pos + 1)//RecyclerView滑动到选择项
                adapter.refreshBg(pos, temp)
                temp = pos//设置当前选中页，在refreshBg方法中 更新单个Item -> notifyItemChange() 需要，若 全部更新 -> notifyDataSetChanged() 则不需要设置

                //配对点击的蓝牙设备
                pairDevice(pos)
            }

        })

    }

    /**
     * 初始化 menuFragment
     * */
    private fun changeFragment(menuItem: Int) {
        Log.e(logTAG, "传入 menu Item > > > $menuItem")
        val layout = binding.menuLayout.id
        transaction = fragmentManager.beginTransaction()
        hideAllFragment()
        when (menuItem) {
            0 -> {
                binding.menuTitleText.text = "连接设备"

                if (menuFragmentOne == null) {
                    Log.e(logTAG, "show fragment 1")
                    menuFragmentOne = MenuOneFragment()
                    transaction.add(binding.menuLayout.id, menuFragmentOne!!, "Frag1")
                } else {
                    transaction.show(menuFragmentOne!!)
                }
            }
            1 -> {
                binding.menuTitleText.text = "电池信息"

                if (menuFragmentTwo == null) {
                    menuFragmentTwo = MenuSecondFragment()
                    transaction.add(layout, menuFragmentTwo!!, "Frag1")
                } else {
                    transaction.show(menuFragmentTwo!!)
                }
            }
            else -> {
                binding.menuTitleText.text = "连接设备"
                Log.e(logTAG, "第一次进入")
                menuFragmentOne = MenuOneFragment()
                transaction.show(menuFragmentOne!!)
            }
        }
        transaction.commit()
    }

    //隐藏所有fragment
    private fun hideAllFragment() {
        if (menuFragmentOne != null) {
            transaction.hide(menuFragmentOne!!)
        }
        if (menuFragmentTwo != null) {
            transaction.hide(menuFragmentTwo!!)
        }
    }


    /**
     * 配对蓝牙设备
     * */
    @SuppressLint("MissingPermission")
    fun pairDevice(pos: Int) {
        mPairDevice = scanBluetoothDeviceList[pos]

        thread {
            //取消搜索
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter?.cancelDiscovery()
            }

            //当前蓝牙设备未配对，则先进行配对
            if (mPairDevice.bondState == BluetoothDevice.BOND_NONE) {
                Log.d(logTAG,"开启线程尝试与 > > > ${mPairDevice.name} 设备配对")
                val b: Boolean = MyUtils.createBond(mPairDevice)
                if (!b) {
                   Log.e(logTAG, "与 > > > ${mPairDevice.name} 设备配对连接失败")
                    return@thread
                }
                Log.d(logTAG,"正在与 > > > ${mPairDevice.name} 进行配对...")
                //循环等待配对
                mFlag = 0
                while (mFlag == 0) {
                    SystemClock.sleep(250)
                }
                if (mFlag == -1) {
                    Log.e(logTAG, "配对连接失败")
                    return@thread
                }
            }

            //如果传入设备是已经配对的，则直接建立socket连接
            if (mPairDevice.bondState == BluetoothDevice.BOND_BONDED) {
                Log.e(logTAG, "尝试与 > > > ${mPairDevice.name} 建立socket连接")
                try {

                    Log.e(logTAG, "成功与 > > > ${mPairDevice.name} 建立socket连接")
                } catch (e: IOException) {
                    Log.e(logTAG, "与 > > > ${mPairDevice.name} 建立socket连接 失败")
                    e.printStackTrace()
                }
            }
        }
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