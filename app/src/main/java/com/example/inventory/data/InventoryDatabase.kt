package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.MasterKeys
import androidx.security.crypto.MasterKeys.AES256_GCM_SPEC
import com.example.inventory.encryption.AppSettings
import com.commonsware.cwac.saferoom.SQLCipherUtils
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


@Database (entities = [Item::class], version = 5, exportSchema = true)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }

        fun getEncryptedDatabase(context: Context): InventoryDatabase {
            // Получаем пароль из SharedPreferences или используем дефолтное значение "null"
            val encryptionKey = MasterKeys.getOrCreate(AES256_GCM_SPEC)

            // Проверяем текущее состояние базы данных
            val databaseState = SQLCipherUtils.getDatabaseState(context, "item_database")

            // Если база данных не зашифрована, шифруем ее
            if (databaseState == SQLCipherUtils.State.UNENCRYPTED) {
                SQLCipherUtils.encrypt(context, "item_database", encryptionKey.toByteArray())
            }

            val supportFactory = SupportFactory(SQLiteDatabase.getBytes(encryptionKey.toCharArray()))

            // Строим экземпляр базы данных с использованием Room
            return Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                .fallbackToDestructiveMigration()
                .openHelperFactory(supportFactory)
                .build()
        }

    }
}