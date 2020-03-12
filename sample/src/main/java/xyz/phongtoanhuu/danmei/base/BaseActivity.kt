package xyz.phongtoanhuu.danmei.base

import android.annotation.SuppressLint
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import xyz.phongtoanhuu.danmei.R

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
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
        progressBar.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }


}