package com.statichumstudio.slipshell.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerProfileData(
    val id: String,
    val name: String,
    val hostname: String,
    val port: Int = 22,
    val username: String,
    val authMethod: AuthMethod = AuthMethod.PASSWORD,
    val groupName: String = "",
    val colorTag: String = "#FF6B35",
    val notes: String = "",
    val lastConnected: Long? = null,
    val sortOrder: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

@Serializable
enum class AuthMethod {
    PASSWORD,
    PRIVATE_KEY;

    companion object {
        fun fromString(value: String): AuthMethod = when (value.lowercase()) {
            "password" -> PASSWORD
            "privatekey", "private_key" -> PRIVATE_KEY
            else -> PASSWORD
        }
    }

    fun toDbString(): String = when (this) {
        PASSWORD -> "password"
        PRIVATE_KEY -> "privateKey"
    }
}
