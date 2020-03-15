package xyz.phongtoanhuu.danmei.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.FolioReader.*
import com.folioreader.model.HighLight
import com.folioreader.model.HighLight.HighLightAction
import com.folioreader.model.locators.ReadLocator
import com.folioreader.model.locators.ReadLocator.Companion.fromJson
import com.folioreader.util.AppUtil
import com.folioreader.util.OnHighlightListener
import com.folioreader.util.ReadLocatorListener
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.phongtoanhuu.danmei.R
import xyz.phongtoanhuu.danmei.adapter.MainAdapter
import xyz.phongtoanhuu.danmei.base.BaseActivity
import xyz.phongtoanhuu.danmei.entity.CategoryEntity
import xyz.phongtoanhuu.danmei.entity.DownloadEntity
import xyz.phongtoanhuu.danmei.extension.startActivity
import xyz.phongtoanhuu.danmei.extension.toast
import xyz.phongtoanhuu.danmei.utils.InterstitialAdUtils
import xyz.phongtoanhuu.danmei.utils.Status
import xyz.phongtoanhuu.danmei.utils.TopSpacingItemDecoration
import xyz.phongtoanhuu.danmei.viewmodel.MainViewModel

class MainActivity : BaseActivity(), OnHighlightListener,
    ReadLocatorListener, OnClosedListener {

    private var folioReader: FolioReader? = null
    private lateinit var mainAdapter: MainAdapter
    private val broadcastReceiver = BroadCastReceiver()
    private lateinit var bManager: LocalBroadcastManager
    private val interstitialAdUtils: InterstitialAdUtils by inject()
    private var categoryEntity: CategoryEntity? = null

    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        folioReader = get().setOnHighlightListener(this)
            .setReadLocatorListener(this)
            .setOnClosedListener(this)
        initRecyclerView()
        subscribeObservers()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver()
    }

    override fun onStop() {
        super.onStop()
        bManager.unregisterReceiver(broadcastReceiver)
    }

    private fun registerReceiver() {
        bManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction("MESSAGE_PROGRESS")
        bManager.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun initRecyclerView() {
        rvMain.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)
            mainAdapter = MainAdapter { categoryEntity ->
                run {
                    if (categoryEntity.isReaded != 0) {
                        if (categoryEntity.externalStorageFile == "") {
                            viewModel.downloadEpub(categoryEntity)
                                .observe(this@MainActivity, Observer { result ->
                                    when (result.status) {
                                        Status.LOADING -> showProgressBar(visibility = true)
                                        Status.SUCCESS -> {
                                            showProgressBar(false)
                                            interstitialAdUtils.showInterstitialAd(object :
                                                InterstitialAdUtils.AdCloseListener {
                                                override fun onAdClosed() {
                                                    result.data?.let {
                                                        this@MainActivity.categoryEntity = it
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
                                                            folioReader?.setReadLocator(readLocator)
                                                                ?.setConfig(config, true)
                                                                ?.openBook(categoryEntity.externalStorageFile)
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
                        } else {
                            this@MainActivity.categoryEntity = categoryEntity
                            val readLocator = lastReadLocator
                            var config = AppUtil.getSavedConfig(applicationContext)
                            if (config == null) {
                                config = Config()
                            }
                            config!!.allowedDirection =
                                Config.AllowedDirection.VERTICAL_AND_HORIZONTAL;
                            folioReader?.setReadLocator(readLocator)
                                ?.setConfig(config, true)
                                ?.openBook(categoryEntity.externalStorageFile)
                        }
                    } else {
                        startActivity<PopupContentActivity> {
                            this.putExtra("CategoryEntity",categoryEntity)
                        }
                    }

                }
            }

            adapter = mainAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.getCategoriesCount().observe(this, Observer {  })
        viewModel.categoryList.observe(this, Observer {
            it.data?.let {
                mainAdapter.submitList(it as ArrayList<CategoryEntity>)
            }
        })
    }

    private val lastReadLocator: ReadLocator?
        get() {
            val jsonString = categoryEntity?.lastReadLocator
            return fromJson(jsonString)
        }

    override fun saveReadLocator(readLocator: ReadLocator) {
        categoryEntity?.lastReadLocator = readLocator.toJson().toString()
        categoryEntity?.let {
            viewModel.updateCategoryEntity(it)
        }
        Log.i(LOG_TAG, "-> saveReadLocator -> " + readLocator.toJson())
    }

    override fun onDestroy() {
        super.onDestroy()
        clear()
    }

    override fun onHighlight(highlight: HighLight, type: HighLightAction) {
        Toast.makeText(
            this,
//            "highlight id = " + highlight.uuid + " type = " + type,
            "Đã đánh dấu",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onFolioReaderClosed() {
        Log.v(LOG_TAG, "-> onFolioReaderClosed")
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
    }

    inner class BroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action.equals("MESSAGE_PROGRESS")) {
                val download: DownloadEntity = intent.getParcelableExtra("download")!!
                progressBarBaseActivity.progress = download.progress
                if (download.progress == 100) {
                    // Do something when download complete
                } else {
                    // Do show current % current file size
                }
            }
        }
    }
}

