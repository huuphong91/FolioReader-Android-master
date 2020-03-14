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
import com.folioreader.FolioReader
import com.folioreader.FolioReader.*
import com.folioreader.model.HighLight
import com.folioreader.model.HighLight.HighLightAction
import com.folioreader.model.locators.ReadLocator
import com.folioreader.model.locators.ReadLocator.Companion.fromJson
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
import xyz.phongtoanhuu.danmei.extension.toast
import xyz.phongtoanhuu.danmei.utils.InterstitialAdUtils
import xyz.phongtoanhuu.danmei.utils.Status
import xyz.phongtoanhuu.danmei.utils.TopSpacingItemDecoration
import xyz.phongtoanhuu.danmei.viewmodel.MainViewModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class MainActivity : BaseActivity(), OnHighlightListener,
    ReadLocatorListener, OnClosedListener {

    private var folioReader: FolioReader? = null
    private lateinit var mainAdapter: MainAdapter
    private val broadcastReceiver = BroadCastReceiver()
    private lateinit var bManager: LocalBroadcastManager
    private val interstitialAdUtils: InterstitialAdUtils by inject()

    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        folioReader = get()
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
            val topSpacingDecorator = TopSpacingItemDecoration(15)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            mainAdapter = MainAdapter { categoryEntity ->
                run {
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
                                                    if (it.externalStorageFile != "") {
                                                        folioReader?.openBook(categoryEntity.externalStorageFile)
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
                        folioReader?.openBook(categoryEntity.externalStorageFile)
                    }
                }
            }

            adapter = mainAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.getCategoriesCount().observe(this, Observer {})
        viewModel.getCategories().observe(this, Observer {
            it.data?.let {
                mainAdapter.submitList(it as ArrayList<CategoryEntity>)
            }
        })
    }

    private val lastReadLocator: ReadLocator?
        get() {
            val jsonString =
                loadAssetTextAsString("Locators/LastReadLocators/last_read_locator_1.json")
            return fromJson(jsonString)
        }

    override fun saveReadLocator(readLocator: ReadLocator) {
        Log.i(LOG_TAG, "-> saveReadLocator -> " + readLocator.toJson())
    }//You can do anything on successful saving highlight list

    private fun loadAssetTextAsString(name: String): String? {
        var `in`: BufferedReader? = null
        try {
            val buf = StringBuilder()
            val `is`: InputStream = assets.open(name)
            `in` = BufferedReader(InputStreamReader(`is`))
            var str: String?
            var isFirst = true
            while (`in`.readLine().also { str = it } != null) {
                if (isFirst) isFirst = false else buf.append('\n')
                buf.append(str)
            }
            return buf.toString()
        } catch (e: IOException) {
            Log.e("HomeActivity", "Error opening asset $name")
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    Log.e("HomeActivity", "Error closing asset $name")
                }
            }
        }
        return null
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

