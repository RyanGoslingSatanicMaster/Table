package com.example.table.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

@Entity(indices = [
    Index(value = ["group_name"], unique = true)
])
data class Group(
    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,
    @ColumnInfo(name = "group_name")
    val groupName: String,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean
){
    class GroupDeserializer: JsonDeserializer<Group>{
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Group {
            json?.asJsonObject.let { group ->
                return Group(group?.get("GroupId")!!.asLong, group.get("GroupName").asString, false)
            }
            throw IllegalArgumentException("Json is not valid")
        }
    }
}
