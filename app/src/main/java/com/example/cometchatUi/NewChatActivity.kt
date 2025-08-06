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
import com.example.cometchatUi.utils.ContactUtils
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
            val contacts = ContactUtils.fetchAllContactsFast(this@NewChatActivity)
            contactsState.addAll(contacts)
            isLoading.value = false
        }
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