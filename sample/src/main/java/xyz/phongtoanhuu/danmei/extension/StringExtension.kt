package xyz.phongtoanhuu.danmei.extension

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

inline fun <reified T> String.toListByGson(): List<T> = if (isNotEmpty()) {
    Gson().fromJson<List<T>>(this, TypeToken.getParameterized(ArrayList::class.java, T::class.java).type)
} else {
    listOf()
}