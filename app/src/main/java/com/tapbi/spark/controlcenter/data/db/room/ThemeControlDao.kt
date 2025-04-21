package com.tapbi.spark.controlcenter.data.db.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeControlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertThemeControl(theme: ThemeControl): Long




    @Query("SELECT * FROM TABLE_THEME_CONTROL ORDER BY id DESC")
    fun getAllThemeControlFlow(): Flow<List<ThemeControl>>



    @Query("SELECT * FROM TABLE_THEME_CONTROL WHERE id = :id")
    fun getItemThemeControlById(id: Long): ThemeControl?

    @Delete
    fun deleteItemThemeControl(itemTheme: ThemeControl)

    @Query("DELETE FROM TABLE_THEME_CONTROL WHERE id = :id")
    fun deleteThemeControlById(id: Long)
}