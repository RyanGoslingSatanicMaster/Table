package com.example.table.model

import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("GroupId")
    val id: Int,
    @SerializedName("GroupName")
    val name: String
)
