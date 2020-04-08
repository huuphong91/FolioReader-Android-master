package com.loiphong.truyendammyfull.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.loiphong.truyendammyfull.entity.CategoryEntity

@Database(entities = [CategoryEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serverDao(): ServerDao
}