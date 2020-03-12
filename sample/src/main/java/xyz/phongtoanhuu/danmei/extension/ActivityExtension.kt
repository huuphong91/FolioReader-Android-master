package xyz.phongtoanhuu.danmei.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import xyz.phongtoanhuu.danmei.R

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    block(intent)
    startActivity(intent)
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.toast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

//fun Activity.displaySuccessDialog(message: String?) {
//    MaterialDialog(this)
//        .show {
//            title(R.string.text_success)
//            message(text = message)
//            positiveButton(R.string.text_ok)
//        }
//}
//
//fun Activity.displayErrorDialog(errorMessage: String?) {
//    MaterialDialog(this)
//        .show {
//            title(R.string.text_error)
//            message(text = errorMessage)
//            positiveButton(R.string.text_ok)
//        }
//}
//
//fun Activity.displayInfoDialog(message: String?) {
//    MaterialDialog(this)
//        .show {
//            title(R.string.text_info)
//            message(text = message)
//            positiveButton(R.string.text_ok)
//        }
//}
//
//fun Activity.areYouSureDialog(message: String, callback: AreYouSureCallback) {
//    MaterialDialog(this)
//        .show {
//            title(R.string.are_you_sure)
//            message(text = message)
//            negativeButton(R.string.text_cancel) {
//                callback.cancel()
//            }
//            positiveButton(R.string.text_yes) {
//                callback.proceed()
//            }
//        }
//}

inline fun <reified T> Activity.readJsonConvertToListFromRaw(fileName: Int): List<T> {
    val inputStream = resources.openRawResource(fileName)

    val data = StringBuffer()

    inputStream.bufferedReader(Charsets.UTF_8).useLines { lines ->
        lines.fold("") { some, text ->
            data.append("$some$text")
            ""
        }
    }
    return data.toString().toListByGson()
}

interface AreYouSureCallback {

    fun proceed()

    fun cancel()
}