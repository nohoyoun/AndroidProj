package com.example.theproj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.theproj.databinding.ActivitySearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class SearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private lateinit var loc_name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var intent = intent
        loc_name = intent.getStringExtra("location_Name").toString()

        Log.d("전달받은 지역 출력", loc_name)
        val ParkDBTable = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()
        var checkId_list = arrayListOf<CheckBox>(binding.chk1, binding.chk2, binding.chk3, binding.chk4, binding.chk5, binding.chk6,
            binding.chk7, binding.chk8, binding.chk9, binding.chk10, binding.chk11, binding.chk12, binding.chk13, binding.chk14,
            binding.chk15, binding.chk16)


        binding.facTxt.setOnClickListener {
            if (binding.facTable.visibility == View.VISIBLE) {
                binding.facTable.visibility = View.GONE
            }else
            {
                binding.facTable.visibility = View.VISIBLE
            }

        }


        binding.btnSearchTxt.setOnClickListener{
            var searchName = binding.edtSearch.text.toString()
            if(searchName.length != 0) {
                CoroutineScope(Dispatchers.Main).launch {
                    var strId = ParkDBTable.parkDBInterface().getNN(searchName)
                    Log.d("검색로그", strId.toString())
                    if(strId == null) {
                        Toast.makeText(applicationContext, "검색된 공원이 없습니다.", Toast.LENGTH_SHORT).show()
                        //검색된 공원이 없습니다 띄워주기
                    }else{
                        SeeList(strId, ParkDBTable)
                    }
                }
            }else {
                Searching(checkId_list, loc_name, ParkDBTable)
            }

            }

        }

    fun Searching(checkId_list : ArrayList<CheckBox>, loc_name : String, ParkDBTable : AppDatabase) {
        var count = 0
        var strId : List<String>
        for (i in checkId_list) {//체크박스로 검색
            if(i.isChecked) {
                CoroutineScope(Dispatchers.Main).launch {
                    strId = ParkDBTable.parkDBInterface().getNamebyLF(loc_name, i.text.toString())
                    Log.d("지역로그", strId.toString())

                    if(strId != null) {
                        SeeList(strId, ParkDBTable)
                    }else{
                        removeList()
                    }
                }
            }
            else if(i.isChecked == false) {
                count++
                Log.d("카운트", count.toString())
            }

            if(count == checkId_list.size) {    //전체검색
                CoroutineScope(Dispatchers.Main).launch {
                    strId = ParkDBTable.parkDBInterface().getNamebyLoc(loc_name)
                    Log.d("지역로그", strId.toString())

                    if(strId != null) {
                        SeeList(strId, ParkDBTable)
                    }else{
                        removeList()
                    }
                }
            }
        }

    }

    fun SeeList(strid : List<String>, ParkDBTable: AppDatabase) {

        var fac : String

        var item = mutableListOf<list_View_Item>()

        item.clear()

        for(i in strid){
            CoroutineScope(Dispatchers.Main).launch {
                fac = ParkDBTable.parkDBInterface().getfacbyName(i)
                Log.d("SeeList fac출력", fac)
                if(fac == null) {
                    fac = " "
                }
                item.add(list_View_Item(i,fac))

                Log.d("item 로그", item.toString())
                val adapter = ContentAdapter(item)
                binding.listPark.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }
    fun removeList() {
        var item = mutableListOf<list_View_Item>()
        val adapter = ContentAdapter(item)
        binding.listPark.adapter = adapter
        item.clear()
    }
}



