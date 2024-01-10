package com.example.inventory.encryption

import android.net.Uri
import com.example.inventory.data.Item
interface SecuredFileRepository {
    suspend fun saveItemToFile(item: Item, targetFile: Uri)
    suspend fun getItemFromFile(targetFile: Uri): Item
}