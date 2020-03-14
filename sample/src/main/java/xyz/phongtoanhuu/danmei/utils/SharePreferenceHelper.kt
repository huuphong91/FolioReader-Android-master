package xyz.phongtoanhuu.danmei.utils

import android.content.Context
import xyz.phongtoanhuu.danmei.base.BaseApplication

class SharePreferenceHelper(private val application: BaseApplication) {

    var latestCountCategories: Int
        get() = application.getSharedPreferences(APP_SHARE_PREFERENCE, Context.MODE_PRIVATE)
            .getInt("CountCategories", -1)
        set(latestCountCategories) = application.getSharedPreferences(
                APP_SHARE_PREFERENCE,
                Context.MODE_PRIVATE
            ).edit()
            .putInt("CountCategories", latestCountCategories)
            .apply()
}