package xyz.phongtoanhuu.danmei.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.phongtoanhuu.danmei.entity.CategoryEntity

@Dao
interface ServerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM category")
    fun getCategories(): LiveData<List<CategoryEntity>>
}