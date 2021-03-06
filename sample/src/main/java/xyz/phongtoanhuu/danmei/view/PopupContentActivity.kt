package xyz.phongtoanhuu.danmei.view

import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
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
import xyz.phongtoanhuu.danmei.R
import xyz.phongtoanhuu.danmei.base.BaseActivity
import xyz.phongtoanhuu.danmei.entity.CategoryEntity
import xyz.phongtoanhuu.danmei.extension.toast
import xyz.phongtoanhuu.danmei.utils.InterstitialAdUtils
import xyz.phongtoanhuu.danmei.utils.Status
import xyz.phongtoanhuu.danmei.viewmodel.MainViewModel

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

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.8).toInt(), (height * 0.7).toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = -20

        window.attributes = params

        categoryEntity = intent.getParcelableExtra("CategoryEntity")

        tvContent.movementMethod = ScrollingMovementMethod()
        tvContent.text = (Html.fromHtml(categoryEntity?.description))

        btnRead.setOnClickListener {
            showProgressBar(true)
            categoryEntity!!.isReaded = 1
            viewModel.updateCategoryEntity(categoryEntity!!)
            if (categoryEntity?.externalStorageFile == "") {
                viewModel.downloadEpub(categoryEntity!!)
                    .observe(this@PopupContentActivity, Observer { result ->
                        when (result.status) {
                            Status.LOADING -> showProgressBar(visibility = true)
                            Status.SUCCESS -> {
                                showProgressBar(false)
                                interstitialAdUtils.showInterstitialAd(object :
                                    InterstitialAdUtils.AdCloseListener {
                                    override fun onAdClosed() {
                                        result.data?.let {
                                            this@PopupContentActivity.categoryEntity = it
                                            val readLocator = lastReadLocator
                                            var config =
                                                AppUtil.getSavedConfig(
                                                    applicationContext
                                                )
                                            if (config == null) {
                                                config = Config()
                                            }
                                            config!!.allowedDirection =
                                                Config.AllowedDirection.VERTICAL_AND_HORIZONTAL;
                                            if (it.externalStorageFile != "") {
                                                folioReader
                                                    ?.setReadLocator(readLocator)?.setConfig(config, true)
                                                    ?.openBook(categoryEntity!!.externalStorageFile)
                                            }
                                        }
                                    }
                                })
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
