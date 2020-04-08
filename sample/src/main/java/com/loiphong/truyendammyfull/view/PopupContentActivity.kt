package com.loiphong.truyendammyfull.view

import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.model.HighLight
import com.folioreader.model.locators.ReadLocator
import com.folioreader.util.AppUtil
import com.folioreader.util.OnHighlightListener
import com.folioreader.util.ReadLocatorListener
import kotlinx.android.synthetic.main.activity_popup_content.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.loiphong.truyendammyfull.R
import com.loiphong.truyendammyfull.base.BaseActivity
import com.loiphong.truyendammyfull.entity.CategoryEntity
import com.loiphong.truyendammyfull.extension.toast
import com.loiphong.truyendammyfull.utils.InterstitialAdUtils
import com.loiphong.truyendammyfull.utils.Status
import com.loiphong.truyendammyfull.viewmodel.MainViewModel

class PopupContentActivity : BaseActivity(), OnHighlightListener,
    ReadLocatorListener, FolioReader.OnClosedListener {

    private val viewModel: MainViewModel by viewModel()
    private var categoryEntity: CategoryEntity? = null
    private val interstitialAdUtils: InterstitialAdUtils by inject()
    private var folioReader: FolioReader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_content)

        folioReader = FolioReader.get().setOnHighlightListener(this)
            .setReadLocatorListener(this)
            .setOnClosedListener(this)

//        val dm = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(dm)
//
//        val width = dm.widthPixels
//        val height = dm.heightPixels
//
//        window.setLayout((width * 0.8).toInt(), (height * 0.7).toInt())
//
//        val params = window.attributes
//        params.gravity = Gravity.CENTER
//        params.x = 0
//        params.y = -20
//
//        window.attributes = params

        imageView2.setOnClickListener {
            finish()
        }

        categoryEntity = intent.getParcelableExtra("CategoryEntity")

        tvContent.movementMethod = ScrollingMovementMethod()
        tvContent.text = (Html.fromHtml(categoryEntity?.description))

        btnRead.setOnClickListener {
            interstitialAdUtils.showInterstitialAd(object :
                InterstitialAdUtils.AdCloseListener {
                override fun onAdClosed() {
                    btnRead.isEnabled = false
                    val readLocator = lastReadLocator
                    var config =
                        AppUtil.getSavedConfig(
                            applicationContext
                        )
                    if (config == null) {
                        config = Config()
                    }
                    config.allowedDirection =
                        Config.AllowedDirection.VERTICAL_AND_HORIZONTAL;
                    if (categoryEntity!!.externalStorageFile != "") {
                        folioReader
                            ?.setReadLocator(readLocator)
                            ?.setConfig(config, true)
                            ?.openBook(categoryEntity!!.externalStorageFile)
                    }

                }
            })
        }

        button2.setOnClickListener {
            categoryEntity!!.isReaded = 1
            viewModel.updateCategoryEntity(categoryEntity!!)
            if (categoryEntity?.externalStorageFile == "") {
                viewModel.downloadEpub(categoryEntity!!)
                    .observe(this@PopupContentActivity, Observer { result ->
                        when (result.status) {
                            Status.LOADING -> showProgressBar(visibility = true)
                            Status.SUCCESS -> {
                                result.data?.let {
                                    categoryEntity = it
                                }
                                showProgressBar(false)
                                button2.background =
                                    ContextCompat.getDrawable(this, R.color.app_gray)
                                button2.isEnabled = false
                                btnRead.background =
                                    ContextCompat.getDrawable(this, R.drawable.custom_button)
                                btnRead.isEnabled = true
                            }
                            Status.ERROR -> {
                                showProgressBar(false)
                                toast("Nếu bị lỗi, vui lòng kiểm tra lại kết nối internet")
                            }
                        }
                    })
            }
        }
    }

    private val lastReadLocator: ReadLocator?
        get() {
            val jsonString = categoryEntity?.lastReadLocator
            return ReadLocator.fromJson(jsonString)
        }

    override fun onHighlight(highlight: HighLight?, type: HighLight.HighLightAction?) {
        toast("Đã đánh dấu")
    }

    override fun saveReadLocator(readLocator: ReadLocator?) {
        categoryEntity?.lastReadLocator = readLocator?.toJson().toString()
        categoryEntity?.let {
            viewModel.updateCategoryEntity(it)
        }
        Log.i(LOG_TAG, "-> saveReadLocator -> " + readLocator?.toJson())
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        folioReader = null
    }

    override fun onFolioReaderClosed() {
        Log.v(LOG_TAG, "-> onFolioReaderClosed")
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
    }
}
