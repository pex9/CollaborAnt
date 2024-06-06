package it.polito.lab5.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyApplication
import it.polito.lab5.model.MyModel

class AppFactory(context: Context, val teamId: String? = null, val userId: String? = null, val taskId: String? = null) : ViewModelProvider.Factory {
    val model: MyModel = (context.applicationContext as? MyApplication)?.model ?:
        throw java.lang.IllegalArgumentException("Bad application class")

    val auth: GoogleAuthentication = (context.applicationContext as? MyApplication)?.auth ?:
    throw java.lang.IllegalArgumentException("Bad application class")

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MyTeamsViewModel::class.java) -> {
                MyTeamsViewModel(teamId, model, auth) as T
            }
            modelClass.isAssignableFrom(TeamViewModel::class.java) -> {
                teamId?.let { TeamViewModel(it, model) } as T
            }
            modelClass.isAssignableFrom(TeamInfoViewModel::class.java) -> {
                teamId?.let { TeamInfoViewModel(it, model, auth) } as T
            }
            modelClass.isAssignableFrom(TeamFormViewModel::class.java) -> {
                TeamFormViewModel(teamId, model, auth)  as T
            }
            modelClass.isAssignableFrom(TaskViewViewModel::class.java) -> {
                taskId?.let { TaskViewViewModel(taskId, model) } as T
            }
            modelClass.isAssignableFrom(TaskFormViewModel::class.java) -> {
                TaskFormViewModel(teamId, taskId, model) as T
            }
            modelClass.isAssignableFrom(TaskHistoryViewModel::class.java) -> {
                taskId?.let { TaskHistoryViewModel(it, model) } as T
            }
            modelClass.isAssignableFrom(MyTasksViewModel::class.java) -> {
                MyTasksViewModel(model) as T
            }
            modelClass.isAssignableFrom(MyChatsViewModel::class.java) -> {
                MyChatsViewModel(model) as T
            }
            modelClass.isAssignableFrom(ChatViewViewModel::class.java) -> {
                teamId?.let { ChatViewViewModel(teamId, userId, model) } as T
            }
            modelClass.isAssignableFrom(IndividualStatsViewModel::class.java) -> {
                teamId?.let {
                    userId?.let {
                        IndividualStatsViewModel(teamId, userId, model)
                    }
                } as T
            }
            modelClass.isAssignableFrom(TeamStatsViewModel::class.java) -> {
                teamId?.let {
                    TeamStatsViewModel(teamId, model)
                } as T
            }
            modelClass.isAssignableFrom(TeamInvitationViewModel::class.java) -> {
                teamId?.let { TeamInvitationViewModel(it, model) } as T
            }

            modelClass.isAssignableFrom(MyProfileViewModel::class.java) -> {
                 MyProfileViewModel(model, auth)  as T
            }
            modelClass.isAssignableFrom(MyProfileFormViewModel::class.java) -> {
                MyProfileFormViewModel(model, auth)  as T
            }
            modelClass.isAssignableFrom(UserProfileViewModel::class.java) -> {
                userId?.let { UserProfileViewModel(it, model) } as T
            }
            else -> { throw IllegalArgumentException("Unknown ViewModel class") }

        }
    }
}