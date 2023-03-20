package com.example.table.di.modules

import android.app.AlarmManager
import android.app.Service
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import com.example.table.annotations.ApplicationContext
import com.example.table.annotations.DayWeek
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
class WorkerModule constructor(private val worker: CoroutineWorker) {

    @Provides
    @ApplicationContext
    fun provideContext(): Context {
        return worker.applicationContext
    }

    @Provides
    fun provideSharedPref() = worker.applicationContext.getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

    @Provides
    fun provideAlarmManager() = worker.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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
}
