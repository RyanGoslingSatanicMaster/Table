package com.doggystyle.table.usecases

import com.doggystyle.table.model.pojo.DayTimeTable

interface IGetDayWidget {

    suspend fun getActiveDayWidget(index: Int): DayTimeTable?

}
