package com.example.table.usecases

import com.example.table.model.pojo.DayTimeTable

interface IGetDayWidget {

    suspend fun getActiveDayWidget(index: Int): DayTimeTable?

}