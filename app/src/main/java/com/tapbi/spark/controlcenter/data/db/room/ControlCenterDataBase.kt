package com.tapbi.spark.controlcenter.data.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tapbi.spark.controlcenter.common.Constant.DB_VERSION
import com.tapbi.spark.controlcenter.data.db.Converters
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.data.model.ItemPeople
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel

@Database(
    entities = [FocusIOS::class, ItemApp::class, ItemPeople::class, ItemTurnOn::class, NotyModel::class, ThemeControl::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ControlCenterDataBase : RoomDatabase() {
    abstract fun focusDao(): FocusDao
    abstract fun themeControlDao(): ThemeControlDao

    companion object {
        @JvmField
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
    }
}
