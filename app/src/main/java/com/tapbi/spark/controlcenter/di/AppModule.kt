package com.tapbi.spark.controlcenter.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room.databaseBuilder
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.db.room.FocusDao
import com.tapbi.spark.controlcenter.data.db.room.ControlCenterDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideSharedPreference(context: Application?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
    @Provides
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }
    @Provides
    @Singleton
    fun appDatabase(context: Application): ControlCenterDataBase {
        return databaseBuilder(context.applicationContext, ControlCenterDataBase::class.java, Constant.DB_NAME)
            .allowMainThreadQueries()
            .addMigrations(ControlCenterDataBase.MIGRATION_1_2)
            .build()
    }
    @Provides
    @Singleton
    fun provideControlCenterDao(dataBase: ControlCenterDataBase): FocusDao {
        return dataBase.focusDao()
    }
}
