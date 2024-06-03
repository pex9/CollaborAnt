package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.Attachment
import it.polito.lab5.model.Comment
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.TaskState

class TaskViewViewModel(val taskId: Int, val model: MyModel): ViewModel() {
    val teams = model.teams
    val users = model.users
    val tasks = model.tasks

    fun deleteTask(taskId: Int) = model.deleteTask(taskId)

    fun setTaskState(taskId: Int, state: TaskState) = model.setTaskState(taskId, state)

    fun addComment(taskId: Int, comment: Comment) = model.addComment(taskId, comment)

    fun addAttachment(taskId: Int, attachment: Attachment) = model.addAttachment(taskId, attachment)

    fun removeAttachment(taskId: Int, attachmentId: Int) = model.removeAttachment(taskId, attachmentId)

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