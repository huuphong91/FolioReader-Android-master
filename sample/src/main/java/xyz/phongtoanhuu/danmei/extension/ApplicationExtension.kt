package xyz.phongtoanhuu.danmei.extension

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log

fun Application.isConnectedToTheInternet(): Boolean{
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    try{
        Log.e("AppDebug", "isConnectedToTheInternet: ${cm.activeNetworkInfo.isConnected}")
        return cm.activeNetworkInfo.isConnected
    }catch (e: Exception){
        Log.e("AppDebug", "isConnectedToTheInternet: ${e.message}")
    }
    return false
}