package com.example.table.model.pojo

import com.example.table.model.db.Group
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class GroupWrapper(
    val list: List<Group>
){
    class GroupWrapperDeserializer: JsonDeserializer<GroupWrapper>{

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): GroupWrapper {
            val resp = json?.asJsonObject
            val list = mutableListOf<Group>()
            if (!resp?.isJsonNull!!){
                val jsonList = resp.getAsJsonArray("data")
                jsonList.forEach {
                    list.add(context!!.deserialize(it, Group::class.java))
                }
            }
            return GroupWrapper(list.toList())
        }
    }
}
