package com.statichumstudio.slipshell.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.statichumstudio.slipshell.data.model.AuthMethod
import com.statichumstudio.slipshell.data.model.ServerProfileData
import com.statichumstudio.slipshell.db.ServerProfile
import com.statichumstudio.slipshell.db.SlipShellDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightServerProfileRepository(
    private val db: SlipShellDb
) : ServerProfileRepository {

    private val queries = db.slipShellDbQueries

    override fun observeAll(): Flow<List<ServerProfileData>> =
        queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toData() } }

    override fun observeById(id: String): Flow<ServerProfileData?> =
        queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toData() }

    override suspend fun getAll(): List<ServerProfileData> = withContext(Dispatchers.IO) {
        queries.selectAll().executeAsList().map { it.toData() }
    }

    override suspend fun getById(id: String): ServerProfileData? = withContext(Dispatchers.IO) {
        queries.selectById(id).executeAsOneOrNull()?.toData()
    }

    override suspend fun getByGroup(group: String): List<ServerProfileData> =
        withContext(Dispatchers.IO) {
            queries.selectByGroup(group).executeAsList().map { it.toData() }
        }

    override suspend fun getGroups(): List<String> = withContext(Dispatchers.IO) {
        queries.selectGroups().executeAsList()
    }

    override suspend fun insert(profile: ServerProfileData) = withContext(Dispatchers.IO) {
        queries.insert(
            id = profile.id,
            name = profile.name,
            hostname = profile.hostname,
            port = profile.port.toLong(),
            username = profile.username,
            authMethod = profile.authMethod.toDbString(),
            groupName = profile.groupName,
            colorTag = profile.colorTag,
            notes = profile.notes,
            lastConnected = profile.lastConnected,
            sortOrder = profile.sortOrder.toLong(),
            createdAt = profile.createdAt,
            updatedAt = profile.updatedAt,
        )
    }

    override suspend fun update(profile: ServerProfileData) = withContext(Dispatchers.IO) {
        queries.update(
            name = profile.name,
            hostname = profile.hostname,
            port = profile.port.toLong(),
            username = profile.username,
            authMethod = profile.authMethod.toDbString(),
            groupName = profile.groupName,
            colorTag = profile.colorTag,
            notes = profile.notes,
            sortOrder = profile.sortOrder.toLong(),
            updatedAt = profile.updatedAt,
            id = profile.id,
        )
    }

    override suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        queries.delete(id)
    }

    override suspend fun updateLastConnected(id: String, timestamp: Long) =
        withContext(Dispatchers.IO) {
            queries.updateLastConnected(
                lastConnected = timestamp,
                updatedAt = timestamp,
                id = id,
            )
        }

    override fun search(query: String): Flow<List<ServerProfileData>> =
        queries.search(query, query, query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toData() } }

    private fun ServerProfile.toData(): ServerProfileData = ServerProfileData(
        id = id,
        name = name,
        hostname = hostname,
        port = port.toInt(),
        username = username,
        authMethod = AuthMethod.fromString(authMethod),
        groupName = groupName,
        colorTag = colorTag,
        notes = notes,
        lastConnected = lastConnected,
        sortOrder = sortOrder.toInt(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
