package com.onestep.carcontrol.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.onestep.carcontrol.R


class MenuItemAdapter(var context: Context, val data: List<Drawable>) : RecyclerView.Adapter<MenuItemAdapter.MyViewHolders?>() {
    private var pos = 0


    inner class MyViewHolders(view: View): RecyclerView.ViewHolder(view){
        var emojiImg: ImageView = view.findViewById(R.id.emojimini_img)
        val linear: LinearLayout = view.findViewById(R.id.emojimini_linear)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolders {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MyViewHolders(v)
    }


    override fun getItemCount(): Int {
        return data.size
    }


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



    override fun onBindViewHolder(holder: MyViewHolders, position: Int) {
        //渲染Item数据
        holder.emojiImg.setImageDrawable(data[position])

        //更改Item背景
        if (position == pos) {
            holder.linear.background = getDrawable(context, R.drawable.item_shape_radio_c)
        } else {
            holder.linear.background = null
        }

        //设置Item OnClick回调
        var clickPosition = holder.layoutPosition //当前 点击Item 位置
        if (listener != null) {
            holder.itemView.setOnClickListener { listener!!.myClick(clickPosition) }
        }

    }

    fun refreshBg(viewPagerPosition: Int, lastPosition: Int) {
        Log.e("Adapter", "点击回调传入 position > > > $viewPagerPosition . $lastPosition")

        this.pos = viewPagerPosition
        //notifyDataSetChanged()//更新全部数据

        //更新某个Item数据，有默认动画
        notifyItemChanged(viewPagerPosition)//更新当前选中Item数据 -> 添加背景
        notifyItemChanged(lastPosition)//更新上次选中Item数据 -> 删除背景
    }


}