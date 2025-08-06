package com.example.cometchatUi.utils

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.example.cometchatUi.Model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactUtils {

    suspend fun fetchAllContactsFast(context: Context): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val resolver: ContentResolver = context.contentResolver

        // A: Load phone numbers into a map
        val phoneMap = mutableMapOf<String, String>()
        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null, null
        )?.use { phoneCursor ->
            val idIdx = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numIdx = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(idIdx)
                val phoneNumber = phoneCursor.getString(numIdx)
                if (!phoneMap.containsKey(contactId)) {
                    phoneMap[contactId] = phoneNumber
                }
            }
        }

        // B: Load contact names and photo
        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_URI
            ),
            "${ContactsContract.Contacts.HAS_PHONE_NUMBER} > 0",
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"
        )?.use { contactCursor ->
            val idIdx = contactCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIdx = contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val photoIdx = contactCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            while (contactCursor.moveToNext()) {
                val id = contactCursor.getString(idIdx)
                val name = contactCursor.getString(nameIdx) ?: continue
                if (phoneMap.containsKey(id)) {
                    val photoUri = contactCursor.getString(photoIdx)
                    contacts.add(Contact(name = name, profileUrl = photoUri.orEmpty(), isOnline = true))
                }
            }
        }

        return@withContext contacts
    }
}