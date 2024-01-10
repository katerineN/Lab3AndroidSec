package com.example.inventory.encryption

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.inventory.ui.settings.SettingsItems

object AppSettings {
    lateinit var encryptedPrefs: SharedPreferences
    lateinit var mainKey: MasterKey

    lateinit var dbKey: MasterKey
    lateinit var dbPrefs: SharedPreferences

    fun init(app: Application) {
        mainKey = MasterKey.Builder(app)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptedPrefs = EncryptedSharedPreferences.create(
            app,
            "encrypted_settings",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        dbKey = MasterKey.Builder(app, "database_key")
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        dbPrefs = EncryptedSharedPreferences.create(
            app,
            "database_prefs",
            dbKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )


    }

    fun getSettings() : SettingsItems {
        return SettingsItems(
            enterDefaults = encryptedPrefs.getBoolean("enter_defaults", false),
            hideSensitiveData = encryptedPrefs.getBoolean("hide_sensetive_data", false),
            dataSharing = encryptedPrefs.getBoolean("data_sharing", false),
            defaultSellerName = encryptedPrefs.getString("deafult_seller_name", "Ivan Ivanov"),
            defaultSellerPhone = encryptedPrefs.getString("default_seller_phone", "+79085553535"),
            defaultSellerEmail = encryptedPrefs.getString("default_seller_email", "temp@temp.com")
        )
    }

     fun updateSettings(updatedSettings: SettingsItems) {
        with(encryptedPrefs.edit()){
            updatedSettings.enterDefaults?.let{
                this.putBoolean("enter_defaults", it)
            }
            updatedSettings.hideSensitiveData?.let{
                this.putBoolean("hide_sensetive_data", it)
            }
            updatedSettings.dataSharing?.let{
                this.putBoolean("data_sharing", it)
            }
            updatedSettings.defaultSellerName?.let{
                this.putString("default_seller_name", it)
            }
            updatedSettings.defaultSellerPhone?.let{
                this.putString("default_seller_phone", it)
            }
            updatedSettings.defaultSellerEmail?.let{
                this.putString("default_seller_email", it)
            }
            commit()
        }
    }
}
