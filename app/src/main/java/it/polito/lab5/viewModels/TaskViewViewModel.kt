package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.Attachment
import it.polito.lab5.model.Comment
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.TaskState

class TaskViewViewModel(val taskId: String, val model: MyModel,val auth: GoogleAuthentication): ViewModel() {
    val teams = model.teams
    val users = model.users
    val tasks = model.tasks

    val loggedInUserId = auth.getSignedInUserId()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTask(taskId: String) = model.getTask(taskId)
    fun getTeam(teamId: String) = model.getTeam(teamId)
    fun getUser(userId: String) = model.getUser(userId)
    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)

    fun deleteTask(taskId: String) = model.deleteTask1(taskId)

    fun setTaskState(taskId: String, state: TaskState) = model.setTaskState(taskId, state)

    fun addComment(taskId: String, comment: Comment) = model.addComment(taskId, comment)

    fun addAttachment(taskId: String, attachment: Attachment) = model.addAttachment(taskId, attachment)

    fun removeAttachment(taskId: String, attachmentId: String) = model.removeAttachment(taskId, attachmentId)

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
}