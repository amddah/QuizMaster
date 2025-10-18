package com.example.quizmaster.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Custom deserializer for UserRole enum to handle lowercase values from API
 */
class UserRoleDeserializer : JsonDeserializer<UserRole> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UserRole? {
        val roleString = json?.asString ?: return null
        return UserRole.fromString(roleString)
    }
}

