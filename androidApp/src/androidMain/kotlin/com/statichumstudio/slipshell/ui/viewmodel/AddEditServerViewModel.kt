package com.statichumstudio.slipshell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.statichumstudio.slipshell.data.credential.CredentialStore
import com.statichumstudio.slipshell.data.model.AuthMethod
import com.statichumstudio.slipshell.data.model.ServerProfileData
import com.statichumstudio.slipshell.data.repository.ServerProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditServerUiState(
    val id: String = "",
    val name: String = "",
    val hostname: String = "",
    val port: String = "22",
    val username: String = "",
    val authMethod: AuthMethod = AuthMethod.PASSWORD,
    val password: String = "",
    val privateKey: String = "",
    val passphrase: String = "",
    val groupName: String = "",
    val colorTag: String = "#FF6B35",
    val notes: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isTesting: Boolean = false,
    val testResult: String? = null,
    val errors: Map<String, String> = emptyMap(),
    val savedSuccessfully: Boolean = false,
)

class AddEditServerViewModel(
    private val serverId: String?,
    private val repository: ServerProfileRepository,
    private val credentialStore: CredentialStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditServerUiState())
    val uiState: StateFlow<AddEditServerUiState> = _uiState.asStateFlow()

    init {
        if (serverId != null) {
            loadServer(serverId)
        }
    }

    private fun loadServer(id: String) {
        viewModelScope.launch {
            val profile = repository.getById(id) ?: return@launch
            val password = credentialStore.getPassword(id) ?: ""
            val key = credentialStore.getPrivateKey(id) ?: ""
            val passphrase = credentialStore.getPassphrase(id) ?: ""
            _uiState.update {
                it.copy(
                    id = profile.id,
                    name = profile.name,
                    hostname = profile.hostname,
                    port = profile.port.toString(),
                    username = profile.username,
                    authMethod = profile.authMethod,
                    password = password,
                    privateKey = key,
                    passphrase = passphrase,
                    groupName = profile.groupName,
                    colorTag = profile.colorTag,
                    notes = profile.notes,
                    isEditing = true,
                )
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, errors = it.errors - "name") }
    fun onHostnameChange(value: String) = _uiState.update { it.copy(hostname = value, errors = it.errors - "hostname") }
    fun onPortChange(value: String) = _uiState.update { it.copy(port = value, errors = it.errors - "port") }
    fun onUsernameChange(value: String) = _uiState.update { it.copy(username = value, errors = it.errors - "username") }
    fun onAuthMethodChange(value: AuthMethod) = _uiState.update { it.copy(authMethod = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }
    fun onPrivateKeyChange(value: String) = _uiState.update { it.copy(privateKey = value) }
    fun onPassphraseChange(value: String) = _uiState.update { it.copy(passphrase = value) }
    fun onGroupNameChange(value: String) = _uiState.update { it.copy(groupName = value) }
    fun onColorTagChange(value: String) = _uiState.update { it.copy(colorTag = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun save() {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        if (state.name.isBlank()) errors["name"] = "Name is required"
        if (state.hostname.isBlank()) errors["hostname"] = "Hostname is required"
        if (state.username.isBlank()) errors["username"] = "Username is required"
        val port = state.port.toIntOrNull()
        if (port == null || port !in 1..65535) errors["port"] = "Valid port (1-65535)"

        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(errors = errors) }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val id = if (state.isEditing) state.id else java.util.UUID.randomUUID().toString()

            val profile = ServerProfileData(
                id = id,
                name = state.name.trim(),
                hostname = state.hostname.trim(),
                port = port!!,
                username = state.username.trim(),
                authMethod = state.authMethod,
                groupName = state.groupName.trim(),
                colorTag = state.colorTag,
                notes = state.notes.trim(),
                createdAt = if (state.isEditing) 0L else now, // 0 won't be used for update
                updatedAt = now,
            )

            if (state.isEditing) {
                repository.update(profile)
            } else {
                repository.insert(profile.copy(createdAt = now))
            }

            // Save credentials
            when (state.authMethod) {
                AuthMethod.PASSWORD -> {
                    if (state.password.isNotEmpty()) {
                        credentialStore.savePassword(id, state.password)
                    }
                    credentialStore.deletePrivateKey(id)
                    credentialStore.deletePassphrase(id)
                }
                AuthMethod.PRIVATE_KEY -> {
                    if (state.privateKey.isNotEmpty()) {
                        credentialStore.savePrivateKey(id, state.privateKey)
                    }
                    if (state.passphrase.isNotEmpty()) {
                        credentialStore.savePassphrase(id, state.passphrase)
                    }
                    credentialStore.deletePassword(id)
                }
            }

            _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
        }
    }

    fun testConnection() {
        _uiState.update { it.copy(isTesting = true, testResult = null) }
        viewModelScope.launch {
            // Stub: will be wired in Phase 2 to SshConnectionManager
            kotlinx.coroutines.delay(1500)
            _uiState.update {
                it.copy(isTesting = false, testResult = "Test connection will be available after SSH is implemented")
            }
        }
    }
}
