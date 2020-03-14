package xyz.phongtoanhuu.danmei.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.phongtoanhuu.danmei.entity.CategoryEntity

@Dao
interface ServerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM category")
    fun getCategories(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE id = :id")
    fun getCategoryEntity(id: Int): LiveData<CategoryEntity>

    @Update
    fun updateCategoryEntity(categoryEntity: CategoryEntity)
}