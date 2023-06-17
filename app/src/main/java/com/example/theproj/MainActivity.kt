package com.example.theproj

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.room.*
import com.example.theproj.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream




class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val ParkDBTable = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()

        val assetManager : AssetManager = resources.assets
        val inputStream : InputStream = assetManager.open("ParkDB.txt")

        inputStream.bufferedReader().forEachLine {
            var token = it.split("|")
            var input = ParkDB(
                token[0].toInt(),
                token[1],
                token[2].toFloat(),
                token[3].toFloat(),
                token[4],
                token[5]
            )
            Log.d("file_test", token.toString())
            CoroutineScope(Dispatchers.Main).launch {
                ParkDBTable.parkDBInterface().deleteAll()
                ParkDBTable.parkDBInterface().insert(input)
            }
        }


        binding.btnMap.setOnClickListener {
            var intent = Intent(applicationContext, CheckActivity::class.java)
            startActivity(intent)
        }


        binding.btnInmy.setOnClickListener {
            var intent = Intent(applicationContext, MapsActivity::class.java)
            startActivity(intent)

        }

        binding.btnSearch.setOnClickListener {
            var intent = Intent(applicationContext, LocActivity::class.java)
            intent.putExtra("id", true)
            startActivity(intent)
        }



    }



}


