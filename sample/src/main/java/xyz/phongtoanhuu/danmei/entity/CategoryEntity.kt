package xyz.phongtoanhuu.danmei.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "url")
    var url: String,
    @ColumnInfo(name = "avatar")
    var avatar: String,
    @ColumnInfo(name = "last_read_locator")
    var lastReadLocator:String = "",
    @ColumnInfo(name = "external_storage_file")
    var externalStorageFile:String = ""
) : Parcelable