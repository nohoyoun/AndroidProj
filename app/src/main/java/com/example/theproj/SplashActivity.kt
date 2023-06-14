package com.example.theproj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.example.theproj.databinding.ActivitySplashBinding
import kotlin.random.Random

class SplashActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private val SPLASH_TIME_OUT : Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val random = Random
        val num = random.nextInt(2)

        when(num) {
            0->{
                binding.imageView.setImageResource(R.drawable.parkloadingscreen1)
            }
            1->{
                binding.imageView.setImageResource(R.drawable.parkloadingscreen2)
            }
            2->{
                binding.imageView.setImageResource(R.drawable.parkloadingscreen3)
            }
        }

        Handler().postDelayed(
            {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT
        )
    }
}