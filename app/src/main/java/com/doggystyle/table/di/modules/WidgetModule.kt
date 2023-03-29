package com.doggystyle.table.di.modules

import android.content.Context
import com.doggystyle.table.annotations.ApplicationContext
import com.doggystyle.table.annotations.DayWeek
import com.doggystyle.table.components.TableApp
import com.doggystyle.table.datasource.remote.Api
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.pojo.GroupWrapper
import com.doggystyle.table.utils.Constant
import com.doggystyle.table.utils.UnsafeOkHttpClient
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
class WidgetModule constructor(private val context: Context) {

    @Provides
    fun providesGson() = GsonBuilder().create()

    @Provides
    fun providesUnsafeClient() = com.doggystyle.table.utils.UnsafeOkHttpClient.getUnsafeOkHttpClient()

    @Provides
    fun providesRetrofit(gson: Gson, client: OkHttpClient) = Retrofit.Builder().baseUrl(
        Constant.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson)).build()

    @Provides
    fun providesApi(retrofit: Retrofit) = retrofit.create<Api>()

    @Provides
    @ApplicationContext
    fun providesApplicationContext() = context

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
