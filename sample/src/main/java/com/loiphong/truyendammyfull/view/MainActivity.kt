package com.loiphong.truyendammyfull.view

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
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
import com.loiphong.truyendammyfull.R
import com.loiphong.truyendammyfull.adapter.MainAdapter
import com.loiphong.truyendammyfull.base.BaseActivity
import com.loiphong.truyendammyfull.entity.CategoryEntity
import com.loiphong.truyendammyfull.entity.DownloadEntity
import com.loiphong.truyendammyfull.extension.startActivity
import com.loiphong.truyendammyfull.extension.toast
import com.loiphong.truyendammyfull.utils.InterstitialAdUtils
import com.loiphong.truyendammyfull.utils.Status
import com.loiphong.truyendammyfull.utils.TopSpacingItemDecoration
import com.loiphong.truyendammyfull.viewmodel.MainViewModel

class MainActivity : BaseActivity(), OnHighlightListener,
    ReadLocatorListener, OnClosedListener {

    private var folioReader: FolioReader? = null
    private lateinit var mainAdapter: MainAdapter
    private val broadcastReceiver = BroadCastReceiver()
    private lateinit var bManager: LocalBroadcastManager
    private val interstitialAdUtils: InterstitialAdUtils by inject()
    private var categoryEntity: CategoryEntity? = null
    private var kExit = 0

    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        kExit = 1
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
                            this.putExtra("CategoryEntity", categoryEntity)
                        }
                    }

                }
            }
            adapter = mainAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.getCategoriesCount().observe(this, Observer { })
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

    override fun onBackPressed() {
        if (kExit == 1) {
            kExit = 2
            exit()
            return
        }
        onDestroy()
        System.runFinalizersOnExit(true)
        System.exit(0)
    }

    private fun exit() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Đam Mỹ Tình")
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage(" Đam mỹ tình cám ơn bạn đã quan tâm và đọc truyện từ ứng dụng của chúng tôi. Mong các bạn rate 5 sao để đội ngũ biên soạn chúng tôi có động lực tìm kiếm thêm truyện mới. Chúc bạn đọc truyện vui vẻ!")
        builder.setCancelable(true)
        builder.setPositiveButton("Đánh giá App") { dialog, _ ->
            run {
                dialog.cancel()
                try {
                    val uriRate = Uri.parse("market://details?id=$packageName")
                    val intentRate = Intent(Intent.ACTION_VIEW, uriRate)
                    startActivity(intentRate)
                } catch (e: ActivityNotFoundException) {
                    toast("Ứng dụng Google Play không được cài đặt trên máy")
                }
                onDestroy()
                System.runFinalizersOnExit(true)
                System.exit(0)
            }
        }
        builder.setNegativeButton("Thoát") { dialog, _ ->
            run {
                onDestroy()
                System.runFinalizersOnExit(true)
                System.exit(0)
            }
        }
        builder.create().show()
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

