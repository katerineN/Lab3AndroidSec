/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.item

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Item
import com.example.inventory.data.ItemType
import com.example.inventory.data.ItemsRepository
import com.example.inventory.encryption.AppSettings
import com.example.inventory.encryption.SecuredFileRepository
import com.example.inventory.ui.settings.SettingsItems
import java.text.NumberFormat

/**
 * ViewModel to validate and insert items in the Room database.
 */
class ItemEntryViewModel(private val itemsRepository: ItemsRepository,
                         private val fileRepository: SecuredFileRepository)  : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState(AppSettings.getSettings().toItemDetails()))
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }
    suspend fun loadItemFromFile(targetFile: Uri) {
        val item = fileRepository.getItemFromFile(targetFile)
        itemsRepository.insertItem(item)
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            (isNameValid && name.isNotBlank()) && (isPriceValid && price.isNotBlank()) && (isQuantityValid && quantity.isNotBlank())
                    && isPhoneValid && isSellerEmailValid && isSellerNameValid
        }
    }
}

/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    val sellerName: String = "",
    val sellerEmail: String = "",
    val sellerPhone: String = "",
    @Transient var type: ItemType = ItemType.MANUAL
){
    val isSellerEmailValid: Boolean
        get() = if (sellerEmail.isNotBlank()) {
            Regex("""^[a-z0-9!#${'$'}%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#${'$'}%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?${'$'}""").matches(sellerEmail)
        } else {
            true // Возвращает true, если поле пустое
        }

    val isPriceValid: Boolean
        get() = Regex("\\d*\\.?\\d+").matches(price)

    val isQuantityValid: Boolean
        get() = Regex("\\d+").matches(quantity)

    val isNameValid: Boolean
        get() = name.length > 2

    val isSellerNameValid: Boolean
        get() = if (sellerName.isNotBlank()) {
            sellerName.length > 2
        } else {
            true
        }

    val isPhoneValid: Boolean
        get() = if (sellerPhone.isNotBlank()){
            Regex("\\+\\d{11}").matches(sellerPhone)
        } else {
            true
        }

}

/**
 * Extension function to convert [ItemDetails] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0,
    sellerName = sellerName,
    sellerEmail = sellerEmail,
    sellerPhone =  sellerPhone,
    type = type
)

fun SettingsItems.toItemDetails() = if (enterDefaults == true) ItemDetails(
    sellerName = defaultSellerName ?: "",
    sellerPhone = defaultSellerPhone ?: "",
    sellerEmail = defaultSellerEmail ?: ""
) else ItemDetails()


fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString(),
    sellerName = sellerName,
    sellerEmail = sellerEmail,
    sellerPhone =  sellerPhone
)
