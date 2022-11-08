package com.example.table.di.modules

import androidx.room.Room
import com.example.table.TableApp
import com.example.table.datasource.local.database.TableDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Volatile
    private var tableDatabase: TableDatabase

    constructor(app: TableApp){
        tableDatabase = Room.databaseBuilder(app, TableDatabase::class.java, "timetable-db").build()
    }

    @Singleton
    @Provides
    fun providesTableDatabase() = tableDatabase

    @Singleton
    @Provides
    fun providesTimeTableDao() = tableDatabase.getTimeTableDAO()

}