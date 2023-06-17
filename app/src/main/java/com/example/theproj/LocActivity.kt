package com.example.theproj

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
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

        val geocoder : Geocoder = Geocoder(this)

        var loc_list = arrayListOf<Button>(binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9, binding.btn10, binding.btn11, binding.btn12, binding.btn13, binding.btn14, binding.btn15,
            binding.btn16, binding.btn17, binding.btn18, binding.btn19, binding.btn20, binding.btn21, binding.btn22,
            binding.btn23, binding.btn24, binding.btn25)

        var intent = intent

        var get_Id = intent.getBooleanExtra("id", false)

        if(get_Id == true) {
            for (i in loc_list) {
                i.setOnClickListener {
                    var loc_Name = i.text.toString()
                    Log.d("전달할 지역 로그출력", loc_Name)
                    var intent = Intent(applicationContext, SearchActivity::class.java)
                    intent.putExtra("location_Name", loc_Name)
                    startActivity(intent)
                }
            }
        }else {
            for (i in loc_list) {
                i.setOnClickListener {
                    var loc_Name = i.text.toString()

                    val address = geocoder.getFromLocationName("서울특별시" + loc_Name, 1)
                    val addr = address!![0]

                    val intent = Intent()
                    intent.putExtra("latitude", addr.latitude)
                    intent.putExtra("longitude", addr.longitude)
                    setResult(Activity.RESULT_OK, intent)
                    finish()

                }
            }
        }



    }
}