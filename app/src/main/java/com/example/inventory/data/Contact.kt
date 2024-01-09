package com.example.inventory.data

import com.example.inventory.R


/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// взято из https://github.com/googlearchive/android-DirectShare/tree/master

/**
 * Provides the list of dummy contacts. This sample implements this as constants, but real-life apps
 * should use a database and such.
 */
class Contact
/**
 * Instantiates a new [Contact].
 *
 * @param name The name of the contact.
 */(
    /**
     * The name of this contact.
     */
    val name: String
) {
    /**
     * Gets the name of this contact.
     *
     * @return The name of this contact.
     */

    val icon: Int
        /**
         * Gets the icon of this contact.
         *
         * @return The icon.
         */
        get() = R.mipmap.logo_avatar
    companion object {
        /**
         * The list of dummy contacts.
         */
        val CONTACTS = arrayOf(
            Contact("Tereasa"),
            Contact("Chang"),
            Contact("Kory"),
            Contact("Clare"),
            Contact("Landon"),
            Contact("Kyle"),
            Contact("Deana"),
            Contact("Daria"),
            Contact("Melisa"),
            Contact("Sammie")
        )

        /**
         * The contact ID.
         */
        const val ID = "contact_id"

        /**
         * Representative invalid contact ID.
         */
        const val INVALID_ID = -1

        /**
         * Finds a [Contact] specified by a contact ID.
         *
         * @param id The contact ID. This needs to be a valid ID.
         * @return A [Contact]
         */
        fun byId(id: Int): Contact {
            return CONTACTS[id]
        }
    }
}