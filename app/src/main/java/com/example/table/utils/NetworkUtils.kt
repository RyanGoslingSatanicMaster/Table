package com.example.table.utils

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


object NetworkUtils {

    fun buildUnsafeOkHttpClient(): OkHttpClient{
        val builder = OkHttpClient.Builder().addInterceptor(getHttpLoggingInterceptor())
        return builder.build()
    }

    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }
}