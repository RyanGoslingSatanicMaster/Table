package com.example.table.di.modules

import android.content.Context
import com.example.table.annotations.ApplicationContext
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
class WidgetModule constructor(private val context: Context) {

    @Provides
    fun providesGson() = GsonBuilder().create()

    @Provides
    fun providesUnsafeClient() = UnsafeOkHttpClient.getUnsafeOkHttpClient()

    @Provides
    fun providesRetrofit(gson: Gson, client: OkHttpClient) = Retrofit.Builder().baseUrl(
        Constant.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson)).build()

    @Provides
    fun providesApi(retrofit: Retrofit) = retrofit.create<Api>()

    @Provides
    @ApplicationContext
    fun providesApplicationContext() = context

    @Provides
    fun providesDayWeek(): List<String> = listOf("Воскресение", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    @Provides
    fun providesCurrentDay() = Date()
}