package com.example.myapplication.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.adapter.EmojiViewPagerAdapter
import com.example.myapplication.databinding.EmojiOneBinding
import com.example.myapplication.myinterface.EmojiInterFace


class EmojiOneFragment : Fragment() {
    private lateinit var _binding: EmojiOneBinding
    private val binding get() = _binding!!
    private lateinit var emojiInterface: EmojiInterFace//实列化一个接口


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = EmojiOneBinding.inflate(inflater,container, false)
        initEmoji()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.button10.setOnClickListener {
            Log.e(" > ", "点击")
            emojiInterface.onEmojiClick("123456")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        emojiInterface = activity as EmojiInterFace //实列化监听接口
    }

    fun initEmoji() {
        val emojiList = ArrayList<String>()
        emojiList.add("\uD83D\uDE01")
        emojiList.add("\uD83D\uDE02")
        emojiList.add("\uD83D\uDE03")
        emojiList.add("\uD83D\uDE04")
        emojiList.add("\uD83D\uDE0A")
        emojiList.add("️\uD83D\uDE0C")
        emojiList.add("\uD83D\uDE14")
        emojiList.add("\uD83D\uDE1A")
        emojiList.add("\uD83D\uDE20")
        emojiList.add("\uD83D\uDE1E")
        emojiList.add("\uD83D\uDE1D")
        emojiList.add("\uD83D\uDE01")
        emojiList.add("\uD83D\uDE02")
        emojiList.add("\uD83D\uDE03")
        emojiList.add("\uD83D\uDE04")
        emojiList.add("\uD83D\uDE0A")
        emojiList.add("️\uD83D\uDE0C")
        emojiList.add("\uD83D\uDE14")
        emojiList.add("\uD83D\uDE1A")
        emojiList.add("\uD83D\uDE20")
        emojiList.add("\uD83D\uDE1E")
        emojiList.add("\uD83D\uDE1D")
        emojiList.add("\uD83D\uDE01")
        emojiList.add("\uD83D\uDE02")
        emojiList.add("\uD83D\uDE03")
        emojiList.add("\uD83D\uDE04")
        emojiList.add("\uD83D\uDE0A")
        emojiList.add("️\uD83D\uDE0C")
        emojiList.add("\uD83D\uDE14")
        emojiList.add("\uD83D\uDE1A")
        emojiList.add("\uD83D\uDE20")
        emojiList.add("\uD83D\uDE1E")
        emojiList.add("\uD83D\uDE1D")
        emojiList.add("\uD83D\uDE01")
        emojiList.add("\uD83D\uDE02")
        emojiList.add("\uD83D\uDE03")
        emojiList.add("\uD83D\uDE04")
        emojiList.add("\uD83D\uDE0A")
        emojiList.add("️\uD83D\uDE0C")
        emojiList.add("\uD83D\uDE14")
        emojiList.add("\uD83D\uDE1A")
        emojiList.add("\uD83D\uDE20")
        emojiList.add("\uD83D\uDE1E")
        emojiList.add("\uD83D\uDE1D")
        emojiList.add("\uD83D\uDE01")
        emojiList.add("\uD83D\uDE02")
        emojiList.add("\uD83D\uDE03")
        emojiList.add("\uD83D\uDE04")
        emojiList.add("\uD83D\uDE0A")
        emojiList.add("️\uD83D\uDE0C")
        emojiList.add("\uD83D\uDE14")
        emojiList.add("\uD83D\uDE1A")
        emojiList.add("\uD83D\uDE20")
        emojiList.add("\uD83D\uDE1E")
        emojiList.add("\uD83D\uDE1D")
        emojiList.add("\uD83D\uDE01")
        emojiList.add("\uD83D\uDE02")
        emojiList.add("\uD83D\uDE03")
        emojiList.add("\uD83D\uDE04")
        emojiList.add("\uD83D\uDE0A")
        emojiList.add("️\uD83D\uDE0C")
        emojiList.add("\uD83D\uDE14")
        emojiList.add("\uD83D\uDE1A")
        emojiList.add("\uD83D\uDE20")
        emojiList.add("\uD83D\uDE1E")
        emojiList.add("\uD83D\uDE1D")
        emojiList.add("\uD83D\uDE01")
        emojiList.add("\uD83D\uDE02")
        emojiList.add("\uD83D\uDE03")
        emojiList.add("\uD83D\uDE04")
        emojiList.add("\uD83D\uDE0A")
        emojiList.add("️\uD83D\uDE0C")
        emojiList.add("\uD83D\uDE14")
        emojiList.add("\uD83D\uDE1A")
        emojiList.add("\uD83D\uDE20")
        emojiList.add("\uD83D\uDE1E")
        emojiList.add("\uD83D\uDE1D")
        emojiList.add("\uD83D\uDE01")
        emojiList.add("\uD83D\uDE02")
        emojiList.add("\uD83D\uDE03")
        emojiList.add("\uD83D\uDE04")
        emojiList.add("\uD83D\uDE0A")
        emojiList.add("️\uD83D\uDE0C")
        emojiList.add("\uD83D\uDE14")
        emojiList.add("\uD83D\uDE1A")
        emojiList.add("\uD83D\uDE20")
        emojiList.add("\uD83D\uDE1E")
        emojiList.add("\uD83D\uDE1D")

        val layoutManager = GridLayoutManager(context, 7)
        //val layoutManager = LinearLayoutManager(context)
        binding.emojiRevOne.layoutManager = layoutManager
        val adapter = EmojiViewPagerAdapter(emojiList)
        binding.emojiRevOne.adapter = adapter

        //emoji icon  点击监听
        adapter.setOnMyItemClickListener(object : EmojiViewPagerAdapter.OnMyItemClickListener{
            override fun myClick(pos: String) {
                //context?.let { Utils.utilToast(it, pos) }
                emojiInterface.onEmojiClick(pos)
            }

        })




    }


}