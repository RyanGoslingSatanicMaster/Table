package com.example.table.di.modules

import android.app.Application
import android.content.Context
import com.example.table.annotations.DayWeek
import com.example.table.components.TableApp
import com.example.table.datasource.remote.Api
import com.example.table.model.db.Group
import com.example.table.model.pojo.GroupWrapper
import com.example.table.utils.Constant
import com.example.table.utils.UnsafeOkHttpClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.*

@Module
class ApplicationModule  {

    private val app: TableApp

    private lateinit var api: Api

    constructor(app: TableApp){
        this.app = app
    }

    @Provides
    fun providesGson() = GsonBuilder()
        .registerTypeAdapter(GroupWrapper::class.java, GroupWrapper.GroupWrapperDeserializer())
        .registerTypeAdapter(Group::class.java, Group.GroupDeserializer())
        .create()

    @Provides
    fun providesUnsafeClient() = UnsafeOkHttpClient.getUnsafeOkHttpClient()

    @Provides
    fun providesRetrofit(gson: Gson, client: OkHttpClient) = Retrofit.Builder().baseUrl(
        Constant.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson)).build()

    @Provides
    fun providesApi(retrofit: Retrofit) = retrofit.create<Api>()

    @Provides
    @DayWeek
    fun providesDayWeek(): List<Pair<String, String>> = listOf(
        "Воскресение" to "вск",
        "Понедельник" to "пн",
        "Вторник" to "вт",
        "Среда" to "ср",
        "Четверг" to "чт",
        "Пятница" to "пт",
        "Суббота" to "сб"
    )

    @Provides
    fun providesCurrentDay() = Date()

    @Provides
    fun providesContext(): Context{
        return app
    }

    @Provides
    fun providesApplication(): Application{
        return app
    }

}