package com.doggystyle.table.di.modules

import android.content.Context
import androidx.room.Room
import com.doggystyle.table.annotations.ApplicationContext
import com.doggystyle.table.components.TableApp
import com.doggystyle.table.datasource.local.database.TableDatabase
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

    constructor(@ApplicationContext context: Context){
        tableDatabase = Room.databaseBuilder(context, TableDatabase::class.java, "timetable-db").build()
    }

    @Singleton
    @Provides
    fun providesTableDatabase() = tableDatabase

    @Singleton
    @Provides
    fun providesTimeTableDao() = tableDatabase.getTimeTableDAO()

}
