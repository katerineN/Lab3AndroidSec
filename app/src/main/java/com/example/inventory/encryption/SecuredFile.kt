package com.example.inventory.encryption

import android.content.Context
import android.net.Uri
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys.AES256_GCM_SPEC
import androidx.security.crypto.MasterKeys.getOrCreate
import com.example.inventory.data.Item
import com.example.inventory.data.ItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File

class SecuredFile(private val applicationContext: Context) : SecuredFileRepository {
        private val masterKeys = getOrCreate(AES256_GCM_SPEC)

        @OptIn(ExperimentalSerializationApi::class)
        override suspend fun saveItemToFile(item: Item, targetFile: Uri) {
            val fileName = item.name
            val cachedFile = File(applicationContext.cacheDir, fileName)
            val encryptedCachedFile = EncryptedFile.Builder(
                cachedFile,
                applicationContext,
                masterKeys,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val encryptedCacheOutput = encryptedCachedFile.openFileOutput()
            Json.encodeToStream(item, encryptedCacheOutput)

            withContext(Dispatchers.IO) {
                encryptedCacheOutput.close()
            }

            val encryptedCacheInput = cachedFile.inputStream()
            val targetOutput = applicationContext.contentResolver.openOutputStream(targetFile)

            withContext(Dispatchers.IO) {
                targetOutput?.close()
                encryptedCacheInput.close()
                cachedFile.delete()
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        override suspend fun getItemFromFile(targetFile: Uri) : Item {
            val cachedFile = File(applicationContext.cacheDir, targetFile.lastPathSegment!!.split("/").last())
            val targetInput = applicationContext.contentResolver.openInputStream(targetFile)

            val cacheOutput = cachedFile.outputStream()

            targetInput!!.copyTo(cacheOutput)

            withContext(Dispatchers.IO) {
                targetInput.close()
                cacheOutput.close()
            }

            val encryptedCachedFile = EncryptedFile.Builder(
                cachedFile,
                applicationContext,
                masterKeys,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val encryptedCacheInput = encryptedCachedFile.openFileInput()
            val item = Json.decodeFromStream<Item>(encryptedCacheInput)

            withContext(Dispatchers.IO) {
                encryptedCacheInput.close()
                cachedFile.delete()
            }

            return item.copy(id = 0, type = ItemType.FILE)
        }
}