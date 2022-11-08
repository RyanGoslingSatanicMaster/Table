package com.example.table.datasource.remote

import com.example.table.model.pojo.GroupWrapper
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("/bitrix/components/atom/atom.education.schedule-real/get.php")
    suspend fun getGroups(@Query("query") query: String, @Query("type_schedule") typeSchedule: Int): Response<GroupWrapper>

    @GET("/")
    suspend fun getTimeTable(@Query("val") value: String, @Query("type_schedule") typeSchedule: Int): Response<ResponseBody>
}