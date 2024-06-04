package it.polito.lab5.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalDateTime

sealed class ImageProfile
data class Taken(val image: Bitmap): ImageProfile()
data class Uploaded(val image: Uri): ImageProfile()
data class Empty(val color: Color) : ImageProfile()

enum class TaskState {
    NOT_ASSIGNED,
    PENDING,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED,
    OVERDUE
}

enum class Tag {
    UNDEFINED,
    LOW,
    MEDIUM,
    HIGH,
}

enum class Repeat {
    NEVER,
    DAILY,
    WEEKLY,
    MONTHLY
}

data class Comment(
    val content: String,
    val authorId: String,
    val date: LocalDateTime
)

data class Action(
    val id: String,
    val memberId: String,
    val taskState: TaskState,
    val date: LocalDate,
    val description: String
)

data class Attachment(
    val id: String,
    val name: String,
    val type: String,
    val uri: Uri,
    val size: Float
)

enum class Role{
    TEAM_MANAGER,
    SENIOR_MEMBER,
    JUNIOR_MEMBER,
}

data class Team(
    val id: String,
    val name: String,
    val description: String,
    val image: ImageProfile,
    val members: List<Pair<String, Role>>,
    val chat: List<Message>
)

data class User(
    val id: String,
    val first: String,
    val last: String,
    val nickname: String,
    val email: String,
    val telephone: String,
    val location: String,
    val description: String,
    val imageProfile: ImageProfile,
    val joinedTeams: Long,
    val kpiValues: Map<String, KPI>,//List<Pair<Int, KPI>>, // [(teamId, KPI)]
    val categories: List<String>
)

data class U(
    val id: String,
    val first: String,
    val last: String,
    val nickname: String,
    val email: String,
    val telephone: String,
    val location: String,
    val description: String,
    val imageProfile: String,
    val joinedTeams: Int,
    val kpiValues: Map<String, KPI>,//List<Pair<Int, KPI>>, // [(teamId, KPI)]
    val categories: List<String>
)

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val teamId: String,
    val dueDate: LocalDate?,
    val repeat: Repeat,
    val tag: Tag,
    val teamMembers: List<String>,
    val state: TaskState,
    val comments: List<Comment>,
    val categories: Map<String, String>,    //  [userId -> category]
    val attachments: List<Attachment>,
    val history: List<Action>
)

data class Message(
    val senderId: String,
    val receiverId: String?,  // if null means everybody
    val date: LocalDateTime,
    val content: String,
)

data class KPI (
    val assignedTasks: Int,
    val completedTasks: Int,
    val score: Int
)

//used for login and registration
data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

data class SignInResult(
    val signedInUserId: String?,
    val errorMessage: String?
)

fun calculateScore(assignedTasks: Int, completedTasks: Int): Int {
    val n = 5
    return Math.round(n * completedTasks.toFloat() * (completedTasks.toFloat() / assignedTasks.toFloat()))
}


