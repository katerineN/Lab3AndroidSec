package com.example.inventory.ui.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.inventory.encryption.AppSettings


data class SettingsItems(
    val enterDefaults: Boolean? = null,
    val hideSensitiveData: Boolean? = null,
    val dataSharing: Boolean? = null,
    val defaultSellerName: String? = null,
    val defaultSellerPhone: String? = null,
    val defaultSellerEmail: String? = null
)

class SettingsViewModel(
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var settingsUiState = mutableStateOf(AppSettings.getSettings().toSettingsUiState())
        private set

    fun onSendDataSwitchCheckedChanged(newValue: Boolean){
        settingsUiState.value = settingsUiState.value.copy(dataSharing = newValue)
        AppSettings.updateSettings(SettingsItems(dataSharing = newValue))
    }

    fun onHideSensitiveDataCheckedChanged(newValue: Boolean){
        settingsUiState.value = settingsUiState.value.copy(hideSensitiveData = newValue)
        AppSettings.updateSettings(SettingsItems(hideSensitiveData = newValue))
    }

    fun onUseDefaultValuesCheckedChanged(newValue: Boolean){
        settingsUiState.value = settingsUiState.value.copy(enterDefaults = newValue)
        AppSettings.updateSettings(SettingsItems(enterDefaults = newValue))
    }

    fun onDefaultSellerNameChanged(newValue: String){
        settingsUiState.value = settingsUiState.value.copy(defaultSellerName = newValue)
        AppSettings.updateSettings(SettingsItems(defaultSellerName = newValue))
    }

    fun onDefaultSellerEmailChanged(newValue: String){
        settingsUiState.value = settingsUiState.value.copy(defaultSellerEmail = newValue)
        AppSettings.updateSettings(SettingsItems(defaultSellerEmail = newValue))
    }

    fun onDefaultSellerPhoneChanged(newValue: String){
        settingsUiState.value = settingsUiState.value.copy(defaultSellerPhone = newValue)
        AppSettings.updateSettings(SettingsItems(defaultSellerPhone = newValue))
    }
}

data class SettingsUiState(
    val enterDefaults: Boolean,
    val hideSensitiveData: Boolean,
    val dataSharing: Boolean,

    val defaultSellerName: String,
    val defaultSellerPhone: String,
    val defaultSellerEmail: String
)

fun SettingsItems.toSettingsUiState(): SettingsUiState {
    return SettingsUiState(
        enterDefaults = enterDefaults ?: false,
        hideSensitiveData = hideSensitiveData ?: false,
        dataSharing = dataSharing ?: false,
        defaultSellerName = defaultSellerName ?: "",
        defaultSellerPhone = defaultSellerName ?: "",
        defaultSellerEmail = defaultSellerName ?: ""
    )
}