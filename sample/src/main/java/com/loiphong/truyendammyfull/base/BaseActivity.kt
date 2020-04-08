package com.loiphong.truyendammyfull.base

import android.annotation.SuppressLint
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.loiphong.truyendammyfull.R

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var progressBar: ProgressBar
    val TAG: String = "AppDebug"

    @SuppressLint("InflateParams")
    override fun setContentView(layoutResID: Int) {
        val constraintLayout =
            layoutInflater.inflate(R.layout.activity_base, null) as ConstraintLayout
        val frameLayout = constraintLayout.findViewById<FrameLayout>(R.id.frameLayoutBaseActivity)
        progressBar = constraintLayout.findViewById(R.id.progressBarBaseActivity)
        layoutInflater.inflate(layoutResID, frameLayout, true)
        super.setContentView(constraintLayout)
    }

    protected fun showProgressBar(visibility: Boolean) {
        if (visibility) {
            progressBar.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

    }

//    override fun isStoragePermissionGranted(): Boolean {
//        if (
//            ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED  ) {
//
//
//            ActivityCompat.requestPermissions(this,
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ),
//                PERMISSIONS_REQUEST_READ_STORAGE
//            )
//
//            return false
//        } else {
//            // Permission has already been granted
//            return true
//        }
//    }
}