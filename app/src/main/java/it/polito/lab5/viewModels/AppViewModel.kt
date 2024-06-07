package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AppViewModel: ViewModel() {

    var showDialog by mutableStateOf(false)
    fun setShowDialogValue(b: Boolean) {
        showDialog = b
    }

    var chatsReadState: MutableList<Pair<String, Boolean>> = mutableStateListOf()
        private set
    fun setChatsReadStateValue(l: List<Pair<String, Boolean>>) {
        chatsReadState.addAll(l)
    }
}