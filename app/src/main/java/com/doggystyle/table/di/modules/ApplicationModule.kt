package com.doggystyle.table.di.modules

import android.app.Application
import android.content.Context
import com.doggystyle.table.annotations.DayNightMode
import com.doggystyle.table.annotations.DayWeek
import com.doggystyle.table.components.TableApp
import com.doggystyle.table.datasource.remote.Api
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.pojo.GroupWrapper
import com.doggystyle.table.utils.Constant
import com.doggystyle.table.utils.ConverterUtils
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
class ApplicationModule {

    private val app: TableApp

    private lateinit var api: Api

    constructor(app: TableApp) {
        this.app = app
    }

    @Provides
    fun provideSharedPref() =
        app.getSharedPreferences(Constant.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

    @Provides
    fun providesGson() = GsonBuilder()
        .registerTypeAdapter(GroupWrapper::class.java, GroupWrapper.GroupWrapperDeserializer())
        .registerTypeAdapter(Group::class.java, Group.GroupDeserializer())
        .create()

    @Provides
    fun providesUnsafeClient() = com.doggystyle.table.utils.UnsafeOkHttpClient.getUnsafeOkHttpClient()

    @Provides
    fun providesRetrofit(gson: Gson, client: OkHttpClient) = Retrofit.Builder().baseUrl(
        Constant.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson))
        .build()

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
    @DayNightMode
    fun provideDayNightMode(date: Date): Boolean {
        val cal = Calendar.getInstance()
        cal.set(Calendar.AM_PM, Calendar.PM)
        cal.set(Calendar.HOUR_OF_DAY, 18)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return date.after(cal.time)
    }

    @Provides
    fun providesContext(): Context {
        return app
    }

    @Provides
    fun providesApplication(): Application {
        return app
    }

}
