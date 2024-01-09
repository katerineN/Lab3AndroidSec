package com.example.inventory.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val settingsUiState by viewModel.settingsUiState
    //val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(SettingsDestination.titleRes)) },
                modifier = modifier,
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            SettingItem(
                name = "Default values",
                isChecked = settingsUiState.enterDefaults,
                onCheckedChange = viewModel::onUseDefaultValuesCheckedChanged,
                enabled = false
            ) {
                DefaultInputField(
                    value = settingsUiState.defaultSellerName,
                    onValueChange = viewModel::onDefaultSellerNameChanged,
                    label = stringResource(R.string.seller_name),
                    keyboardType = KeyboardType.Text,
                    enabled = false
                )
                Spacer(Modifier.height(8.0.dp))
                DefaultInputField(
                    value = settingsUiState.defaultSellerEmail,
                    onValueChange = viewModel::onDefaultSellerEmailChanged,
                    label = stringResource(R.string.seller_email),
                    keyboardType = KeyboardType.Email,
                    enabled = false
                )
                Spacer(Modifier.height(8.0.dp))
                DefaultInputField(
                    value = settingsUiState.defaultSellerPhone,
                    onValueChange = viewModel::onDefaultSellerPhoneChanged,
                    label = stringResource(R.string.seller_phone),
                    keyboardType = KeyboardType.Phone,
                    enabled = false
                )
            }

            SettingItem(
                name = "Hide sensitive data",
                isChecked = settingsUiState.hideSensitiveData,
                onCheckedChange = viewModel::onHideSensitiveDataCheckedChanged
            )
            SettingItem(
                name = "Turn on data sharing",
                isChecked = settingsUiState.dataSharing,
                onCheckedChange = viewModel::onSendDataSwitchCheckedChanged
            )
        }
    }
}

@Composable
fun SettingItem(
    name: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true, // Добавлен новый параметр
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(32.0.dp),
        modifier = Modifier.padding(8.0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.0.dp, vertical = 4.0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, true)
                    .clickable(enabled = enabled) {
                        if (enabled) {
                            onCheckedChange(!isChecked)
                        }
                    }
            )
            Spacer(modifier = Modifier.width(16.0.dp))
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
        if (isChecked && content != null) {
            Column(
                content = content,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.0.dp, vertical = 8.0.dp),
            )
        }
    }
}


@Composable
fun DefaultInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    enabled: Boolean
) {
    var isFieldEditable by remember { mutableStateOf(false) }

    var mutableValue by remember { mutableStateOf(value) }

    OutlinedTextField(
        shape = RoundedCornerShape(32.0.dp),
        value = mutableValue,
        onValueChange = {
            mutableValue = it
        },
        label = { Text(label) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        readOnly = !isFieldEditable || !enabled,
        isError = mutableValue.isBlank(),
        trailingIcon = {
            Icon(
                imageVector = if (isFieldEditable)
                    if (mutableValue.isBlank())
                        Icons.Filled.Check
                    else Icons.Filled.Close
                else Icons.Filled.Edit,
                contentDescription = stringResource(R.string.edit),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if(enabled) {
                            if (mutableValue.isBlank()) {
                                onValueChange(mutableValue)
                            } else {
                                mutableValue = value
                            }
                            isFieldEditable = !isFieldEditable
                        }
                    }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    InventoryTheme {
        SettingItem(
            name = "Temp",
            isChecked = true,
            onCheckedChange = {})
    }
}
