package xyz.phongtoanhuu.danmei.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadEntity(
    var progress: Int,
    var currentFileSize: Int,
    var totalFileSize: Int
) : Parcelable