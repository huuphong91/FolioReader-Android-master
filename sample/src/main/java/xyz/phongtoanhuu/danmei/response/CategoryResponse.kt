package xyz.phongtoanhuu.danmei.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CategoryResponse {
    @SerializedName("id")
    @Expose
    val id: Int = 0

    @SerializedName("title")
    @Expose
    val title: String = ""

    @SerializedName("description")
    @Expose
    val description: String = ""

    @SerializedName("url")
    @Expose
    val url: String = ""

    @SerializedName("avatar_url")
    @Expose
    val avatar: String = ""

    @SerializedName("created_at")
    @Expose
    val created_at: String = ""
}