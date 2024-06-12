package it.polito.lab5.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.model.Attachment
import it.polito.lab5.model.Comment
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Option
import it.polito.lab5.model.Task
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.User
import kotlinx.coroutines.async
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
class TaskViewViewModel(val taskId: String, val model: MyModel,val auth: GoogleAuthentication): ViewModel() {
    val loggedInUserId = auth.getSignedInUserId()

    fun getTask(taskId: String) = model.getTask(taskId)

    fun getTaskComments(taskId: String) = model.getTaskComments(taskId)

    fun getAttachments(taskId: String) = model.getAttachments(taskId)

    fun getTeam(teamId: String) = model.getTeam(teamId)

    fun getUserKpi(userId: String) = model.getUserKpi(userId)

    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)

    suspend fun updateTaskState(task: Task, delegatedMembers: List<User>, loggedInUserId: String, state: TaskState) =
        model.updateTaskState(task, delegatedMembers, loggedInUserId, state)

    suspend fun addCommentToTask(taskId: String, comment: Comment) = model.addCommentToTask(taskId, comment)

    suspend fun addAttachmentToTask(taskId: String, attachment: Attachment) = model.addAttachmentToTask(taskId, attachment)

    suspend fun downloadFileFromFirebase(taskId: String, attachment: Attachment, onComplete: (File) -> Unit, onFailure: (Exception) -> Unit) =
        model.downloadFileFromFirebase(taskId, attachment, onComplete, onFailure)

    suspend fun deleteAttachmentFromTask(taskId: String, attachment: Attachment) = model.deleteAttachmentFromTask(taskId, attachment)

    suspend fun deleteTask(task: Task, delegatedMembers: List<User>, option: Option): Boolean {
        try {
            viewModelScope.async {
                model.deleteTask(task, delegatedMembers, option)
            }.await()
            return true
        } catch (e: Exception) {
            Log.e("Server Error", e.message.toString())
            return false
        }
    }

    var comment by mutableStateOf("")
        private set
    fun setCommentValue(c: String) {
        comment = c
    }

    var optionsOpened by mutableStateOf(false)
        private set
    fun setOptionsOpenedValue(b: Boolean) {
        optionsOpened = b
    }

    var stateSelOpened by mutableStateOf(false)
        private set
    fun setStateSelOpenedValue(b: Boolean) {
        stateSelOpened = b
    }

    var showBottomSheet by mutableStateOf(false)
        private set
    fun setShowBottomSheetValue(b: Boolean) {
        showBottomSheet = b
    }

    var showDeleteDialog by mutableStateOf(false)
        private set
    fun setShowDeleteDialogValue(b: Boolean) {
        showDeleteDialog = b
    }

    var showRepeatDeleteDialog by mutableStateOf(false)
        private set
    fun setShowRepeatDeleteDialogValue(b: Boolean) {
        showRepeatDeleteDialog = b
    }

    var showLoading by mutableStateOf(false)
        private set
    fun setShowLoadingValue(b: Boolean) {
        showLoading = b
    }

    var showDownloadLoading by mutableStateOf("")
        private set
    fun setShowDownloadLoadingValue(s: String) {
        showDownloadLoading = s
    }

    var optionSelected by mutableStateOf(Option.CURRENT)
        private set
    fun setOptionSelectedValue(o: Option) {
        optionSelected = o
    }

    var showDeleteLoading by mutableStateOf(false)
        private set
    fun setShowDeleteLoadingValue(b: Boolean) {
        showDeleteLoading = b
    }
}