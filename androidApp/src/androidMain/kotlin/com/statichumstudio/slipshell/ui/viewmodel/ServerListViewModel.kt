package com.statichumstudio.slipshell.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.statichumstudio.slipshell.data.credential.CredentialStore
import com.statichumstudio.slipshell.data.model.ServerProfileData
import com.statichumstudio.slipshell.data.repository.ServerProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

data class ServerListUiState(
    val servers: Map<String, List<ServerProfileData>> = emptyMap(),
    val searchQuery: String = "",
    val collapsedGroups: Set<String> = emptySet(),
    val deletedServer: ServerProfileData? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class ServerListViewModel(
    private val repository: ServerProfileRepository,
    private val credentialStore: CredentialStore,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _collapsedGroups = MutableStateFlow<Set<String>>(emptySet())
    private val _deletedServer = MutableStateFlow<ServerProfileData?>(null)

    val uiState: StateFlow<ServerListUiState> = combine(
        _searchQuery.flatMapLatest { query ->
            if (query.isBlank()) repository.observeAll()
            else repository.search(query)
        },
        _searchQuery,
        _collapsedGroups,
        _deletedServer,
    ) { servers, query, collapsed, deleted ->
        val grouped = servers.groupBy { it.groupName.ifBlank { "Ungrouped" } }
            .toSortedMap()
        ServerListUiState(
            servers = grouped,
            searchQuery = query,
            collapsedGroups = collapsed,
            deletedServer = deleted,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ServerListUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleGroupCollapse(group: String) {
        _collapsedGroups.value = _collapsedGroups.value.let {
            if (group in it) it - group else it + group
        }
    }

    fun deleteServer(server: ServerProfileData) {
        viewModelScope.launch {
            _deletedServer.value = server
            repository.delete(server.id)
            credentialStore.deleteAll(server.id)
        }
    }

    fun undoDelete() {
        val server = _deletedServer.value ?: return
        viewModelScope.launch {
            repository.insert(server)
            _deletedServer.value = null
        }
    }

    fun confirmDelete() {
        _deletedServer.value = null
    }

    fun duplicateServer(server: ServerProfileData) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val newId = java.util.UUID.randomUUID().toString()
            repository.insert(
                server.copy(
                    id = newId,
                    name = "${server.name} (Copy)",
                    createdAt = now,
                    updatedAt = now,
                    lastConnected = null,
                )
            )
        }
    }
}
