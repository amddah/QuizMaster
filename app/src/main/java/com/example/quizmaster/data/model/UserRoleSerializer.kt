package com.example.quizmaster.data.model

import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

/**
 * Custom serializer for UserRole enum to send lowercase values in API requests
 */
class UserRoleSerializer : JsonSerializer<UserRole> {
    override fun serialize(src: UserRole?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val roleString = src?.name?.lowercase() ?: ""
        return JsonPrimitive(roleString)
    }
}
