package com.example.table.utils

import android.content.Context
import okhttp3.OkHttpClient


object NetworkUtils {

    fun buildUnsafeOkHttpClient(): OkHttpClient{
        val builder = OkHttpClient.Builder()
        return builder.build()
    }

}
