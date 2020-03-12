package xyz.phongtoanhuu.danmei.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.folioreader.FolioReader
import com.folioreader.FolioReader.OnClosedListener
import com.folioreader.model.HighLight
import com.folioreader.model.HighLight.HighLightAction
import com.folioreader.model.locators.ReadLocator
import com.folioreader.model.locators.ReadLocator.Companion.fromJson
import com.folioreader.util.OnHighlightListener
import com.folioreader.util.ReadLocatorListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.phongtoanhuu.danmei.R
import xyz.phongtoanhuu.danmei.viewmodel.MainViewModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity(), OnHighlightListener,
    ReadLocatorListener, OnClosedListener {
    private var folioReader: FolioReader? = null

    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        subscribeObservers()
        viewModel.setStateEvent(MainStateEvent.GetCategories())
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            if (dataState != null) {
            }
        })

        viewModel.viewState.observe(this, Observer { viewState ->
            Log.d("CourseFragment", "CourseFragment, ViewState: ${viewState}")
            if (viewState != null) {
            }
        })
    }

    private val lastReadLocator: ReadLocator?
        private get() {
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
        FolioReader.clear()
    }

    override fun onHighlight(highlight: HighLight, type: HighLightAction) {
        Toast.makeText(
            this,
            "highlight id = " + highlight.uuid + " type = " + type,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onFolioReaderClosed() {
        Log.v(LOG_TAG, "-> onFolioReaderClosed")
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
    }
}