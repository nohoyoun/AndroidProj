package com.example.theproj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.theproj.databinding.ActivityLocBinding

class LocActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityLocBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var loc_list = arrayListOf<Button>(binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9, binding.btn10, binding.btn11, binding.btn12, binding.btn13, binding.btn14, binding.btn15,
            binding.btn16, binding.btn17, binding.btn18, binding.btn19, binding.btn20, binding.btn21, binding.btn22,
            binding.btn23, binding.btn24, binding.btn25)

        for (i in loc_list) {
            i.setOnClickListener {
                var loc_Name = i.text.toString()
                Log.d("전달할 지역 로그출력", loc_Name)
                var intent = Intent(applicationContext, SearchActivity::class.java)
                intent.putExtra("location_Name", loc_Name)
                startActivity(intent)
            }
        }
    }
}