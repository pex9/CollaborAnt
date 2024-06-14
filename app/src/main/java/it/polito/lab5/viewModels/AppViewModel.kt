package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class AppViewModel: ViewModel() {
    var themeUserSetting by mutableStateOf(false)

    var showDialog by mutableStateOf(false)
    fun setShowDialogValue(b: Boolean) {
        showDialog = b
    }
}