package xyz.phongtoanhuu.danmei.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.phongtoanhuu.danmei.entity.CategoryEntity

@Database(entities = [CategoryEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serverDao(): ServerDao
}