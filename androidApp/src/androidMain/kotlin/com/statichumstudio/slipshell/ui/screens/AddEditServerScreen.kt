package com.statichumstudio.slipshell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.statichumstudio.slipshell.data.model.AuthMethod
import com.statichumstudio.slipshell.ui.viewmodel.AddEditServerViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val colorOptions = listOf(
    "#FF6B35", "#4CAF50", "#2196F3", "#9C27B0",
    "#F44336", "#FF9800", "#00BCD4", "#E91E63",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditServerScreen(
    serverId: String?,
    onBack: () -> Unit,
    viewModel: AddEditServerViewModel = koinViewModel { parametersOf(serverId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Server" else "Add Server") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.save() },
                        enabled = !state.isSaving,
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Filled.Save, contentDescription = "Save")
                        }
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Server Name") },
                isError = "name" in state.errors,
                supportingText = state.errors["name"]?.let { { Text(it) } },
                singleLine = true,
            )

            // Hostname
            OutlinedTextField(
                value = state.hostname,
                onValueChange = viewModel::onHostnameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Hostname / IP") },
                isError = "hostname" in state.errors,
                supportingText = state.errors["hostname"]?.let { { Text(it) } },
                singleLine = true,
            )

            // Port + Username row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = state.port,
                    onValueChange = viewModel::onPortChange,
                    modifier = Modifier.width(100.dp),
                    label = { Text("Port") },
                    isError = "port" in state.errors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Username") },
                    isError = "username" in state.errors,
                    supportingText = state.errors["username"]?.let { { Text(it) } },
                    singleLine = true,
                )
            }

            // Auth method dropdown
            var authExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = authExpanded,
                onExpandedChange = { authExpanded = it },
            ) {
                OutlinedTextField(
                    value = when (state.authMethod) {
                        AuthMethod.PASSWORD -> "Password"
                        AuthMethod.PRIVATE_KEY -> "Private Key"
                    },
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                    label = { Text("Auth Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(authExpanded) },
                )
                ExposedDropdownMenu(
                    expanded = authExpanded,
                    onDismissRequest = { authExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Password") },
                        onClick = {
                            viewModel.onAuthMethodChange(AuthMethod.PASSWORD)
                            authExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Private Key") },
                        onClick = {
                            viewModel.onAuthMethodChange(AuthMethod.PRIVATE_KEY)
                            authExpanded = false
                        },
                    )
                }
            }

            // Conditional auth fields
            when (state.authMethod) {
                AuthMethod.PASSWORD -> {
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                    )
                }
                AuthMethod.PRIVATE_KEY -> {
                    OutlinedTextField(
                        value = state.privateKey,
                        onValueChange = viewModel::onPrivateKeyChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Private Key (paste or import)") },
                        minLines = 3,
                        maxLines = 6,
                    )
                    OutlinedTextField(
                        value = state.passphrase,
                        onValueChange = viewModel::onPassphraseChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Passphrase (optional)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                    )
                }
            }

            // Group
            OutlinedTextField(
                value = state.groupName,
                onValueChange = viewModel::onGroupNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Group (optional)") },
                singleLine = true,
            )

            // Color tag picker
            Text(
                text = "Color Tag",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                colorOptions.forEach { hex ->
                    val color = try { Color(hex.toColorInt()) } catch (_: Exception) { Color.Gray }
                    val isSelected = state.colorTag == hex
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                else Modifier
                            )
                            .clickable { viewModel.onColorTagChange(hex) }
                    )
                }
            }

            // Notes
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes (optional)") },
                minLines = 2,
                maxLines = 4,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Test Connection
            OutlinedButton(
                onClick = { viewModel.testConnection() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isTesting && state.hostname.isNotBlank(),
            ) {
                if (state.isTesting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Testing...")
                } else {
                    Icon(Icons.Filled.NetworkCheck, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Connection")
                }
            }

            state.testResult?.let { result ->
                Text(
                    text = result,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save button
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
            ) {
                Text(if (state.isEditing) "Update Server" else "Save Server")
            }
        }
    }
}
