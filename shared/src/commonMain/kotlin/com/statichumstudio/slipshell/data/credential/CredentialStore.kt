package com.statichumstudio.slipshell.data.credential

interface CredentialStore {
    suspend fun savePassword(serverId: String, password: String)
    suspend fun getPassword(serverId: String): String?
    suspend fun deletePassword(serverId: String)
    suspend fun savePrivateKey(serverId: String, key: String)
    suspend fun getPrivateKey(serverId: String): String?
    suspend fun deletePrivateKey(serverId: String)
    suspend fun savePassphrase(serverId: String, passphrase: String)
    suspend fun getPassphrase(serverId: String): String?
    suspend fun deletePassphrase(serverId: String)
    suspend fun deleteAll(serverId: String)
}
