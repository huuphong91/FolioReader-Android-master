package xyz.phongtoanhuu.danmei.view

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import xyz.phongtoanhuu.danmei.entity.CategoryEntity

const val CATEGORY_VIEW_STATE_BUNDLE_KEY = "xyz.phongtoanhuu.danmei.view.MainViewState"

@Parcelize
data class MainViewState(
    var categoryFields: CategoryFields = CategoryFields()
) : Parcelable {

    @Parcelize
    data class CategoryFields(
        var categories: List<CategoryEntity> = ArrayList(),
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var layoutManagerState: Parcelable? = null
    ) : Parcelable
}