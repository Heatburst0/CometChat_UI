package com.example.cometchatUi

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.cometchatUi.Model.Contact
import com.example.cometchatUi.Presentation.NewChatScreen.NewChatScreen
import com.example.cometchatUi.ui.theme.CometChat_UITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewChatActivity : ComponentActivity() {

    private val searchQuery = mutableStateOf("")
    private val contactsState = mutableStateListOf<Contact>()
    private val isLoading = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                101
            )
        } else {
            loadAllContacts()
        }

        setContent {
            CometChat_UITheme {
                NewChatScreen(
                    contacts = filteredContacts.value,
                    isLoading = isLoading.value,
                    searchQuery = searchQuery.value,
                    onSearchQueryChange = { searchQuery.value = it },
                    onBackClick = { finish() }
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadAllContacts()
        }
    }

    private fun loadAllContacts() {
        isLoading.value = true
        lifecycleScope.launch {
            val contacts = withContext(Dispatchers.IO) {
                fetchAllContactsFast()
            }
            contactsState.addAll(contacts)
            isLoading.value = false
        }
    }

    private fun fetchAllContactsFast(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        val resolver = contentResolver
        val providerClient = resolver.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI)

        try {
            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_URI
            )

            val cursor = providerClient?.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val hasPhoneIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                val photoUriIndex = it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)

                while (it.moveToNext()) {
                    val id = it.getString(idIndex)
                    val name = it.getString(nameIndex) ?: continue
                    val hasPhone = it.getInt(hasPhoneIndex) > 0
                    val photoUri = it.getString(photoUriIndex)

                    if (hasPhone) {
                        val phoneCursor = resolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(id),
                            null
                        )
                        phoneCursor?.use { pc ->
                            if (pc.moveToFirst()) {
                                val phoneIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                val phone = pc.getString(phoneIndex)
                                contacts.add(
                                    Contact(
                                        name = name,
                                        profileUrl = photoUri ?: "",
                                        isOnline = true // Replace with real logic if needed
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            providerClient?.close()
        }

        return contacts
    }

    private val filteredContacts = derivedStateOf {
        if (searchQuery.value.isBlank()) {
            contactsState
        } else {
            contactsState.filter {
                it.name.contains(searchQuery.value, ignoreCase = true)
            }
        }
    }

}