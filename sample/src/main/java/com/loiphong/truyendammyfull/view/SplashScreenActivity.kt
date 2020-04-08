package com.loiphong.truyendammyfull.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.loiphong.truyendammyfull.R
import com.loiphong.truyendammyfull.extension.startActivity

class SplashScreenActivity : AppCompatActivity() {

    var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        timer = object : CountDownTimer((2 * 1000).toLong(), 1000) {
            override fun onTick(l: Long) {

            }

            override fun onFinish() {
                onResourcesLoaded()
            }
        }

    }

    private fun onResourcesLoaded() {
        startActivity<MainActivity>()
        finish()
    }


    override fun onStart() {
        super.onStart()
        timer?.start()
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }
}
