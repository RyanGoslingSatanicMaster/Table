package com.example.table.datasource.remote

import com.example.table.model.Group
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("/bitrix/components/atom/atom.education.schedule-real/get.php")
    suspend fun getGroups(@Query("query") query: String, @Query("type_schedule") typeSchedule: String): Response<Group>
}