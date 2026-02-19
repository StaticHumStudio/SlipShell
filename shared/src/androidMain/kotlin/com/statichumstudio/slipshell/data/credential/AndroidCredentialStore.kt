package com.statichumstudio.slipshell.data.credential

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidCredentialStore(context: Context) : CredentialStore {

    private val prefs: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "slipshell_credentials",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override suspend fun savePassword(serverId: String, password: String) =
        withContext(Dispatchers.IO) {
            prefs.edit().putString("password_$serverId", password).apply()
        }

    override suspend fun getPassword(serverId: String): String? =
        withContext(Dispatchers.IO) {
            prefs.getString("password_$serverId", null)
        }

    override suspend fun deletePassword(serverId: String) =
        withContext(Dispatchers.IO) {
            prefs.edit().remove("password_$serverId").apply()
        }

    override suspend fun savePrivateKey(serverId: String, key: String) =
        withContext(Dispatchers.IO) {
            prefs.edit().putString("key_$serverId", key).apply()
        }

    override suspend fun getPrivateKey(serverId: String): String? =
        withContext(Dispatchers.IO) {
            prefs.getString("key_$serverId", null)
        }

    override suspend fun deletePrivateKey(serverId: String) =
        withContext(Dispatchers.IO) {
            prefs.edit().remove("key_$serverId").apply()
        }

    override suspend fun savePassphrase(serverId: String, passphrase: String) =
        withContext(Dispatchers.IO) {
            prefs.edit().putString("passphrase_$serverId", passphrase).apply()
        }

    override suspend fun getPassphrase(serverId: String): String? =
        withContext(Dispatchers.IO) {
            prefs.getString("passphrase_$serverId", null)
        }

    override suspend fun deletePassphrase(serverId: String) =
        withContext(Dispatchers.IO) {
            prefs.edit().remove("passphrase_$serverId").apply()
        }

    override suspend fun deleteAll(serverId: String) = withContext(Dispatchers.IO) {
        prefs.edit()
            .remove("password_$serverId")
            .remove("key_$serverId")
            .remove("passphrase_$serverId")
            .apply()
    }
}
