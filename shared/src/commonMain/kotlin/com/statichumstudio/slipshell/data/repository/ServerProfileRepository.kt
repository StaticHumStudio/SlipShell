package com.statichumstudio.slipshell.data.repository

import com.statichumstudio.slipshell.data.model.ServerProfileData
import kotlinx.coroutines.flow.Flow

interface ServerProfileRepository {
    fun observeAll(): Flow<List<ServerProfileData>>
    fun observeById(id: String): Flow<ServerProfileData?>
    suspend fun getAll(): List<ServerProfileData>
    suspend fun getById(id: String): ServerProfileData?
    suspend fun getByGroup(group: String): List<ServerProfileData>
    suspend fun getGroups(): List<String>
    suspend fun insert(profile: ServerProfileData)
    suspend fun update(profile: ServerProfileData)
    suspend fun delete(id: String)
    suspend fun updateLastConnected(id: String, timestamp: Long)
    fun search(query: String): Flow<List<ServerProfileData>>
}
