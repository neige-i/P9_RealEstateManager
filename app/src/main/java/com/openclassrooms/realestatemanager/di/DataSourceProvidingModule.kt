package com.openclassrooms.realestatemanager.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.data.PointOfInterest
import com.openclassrooms.realestatemanager.data.room.AgentEntity
import com.openclassrooms.realestatemanager.data.room.AppDatabase
import com.openclassrooms.realestatemanager.data.room.PoiEntity
import com.openclassrooms.realestatemanager.data.room.RoomDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceProvidingModule {

    @Singleton
    @Provides
    fun provideAddDatabase(
        @ApplicationContext applicationContext: Context,
        poiDaoProvider: Provider<RoomDao>,
    ): AppDatabase = Room
        .databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database.db"
        )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {

                    val roomDao = poiDaoProvider.get()

                    PointOfInterest.values().forEach {
                        roomDao.insertPoi(PoiEntity(poiValue = it))
                    }

                    roomDao.insertAgent(AgentEntity(username = "Agent K"))
                    roomDao.insertAgent(AgentEntity(username = "Agent J"))
                    roomDao.insertAgent(AgentEntity(username = "Agent Z"))
                    roomDao.insertAgent(AgentEntity(username = "Agent L"))
                }
            }
        })
        .build()

    @Singleton
    @Provides
    fun provideRoomDao(appDatabase: AppDatabase): RoomDao = appDatabase.roomDao()
}