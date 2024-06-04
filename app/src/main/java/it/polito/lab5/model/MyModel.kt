package it.polito.lab5.model

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class MyModel(context: Context) {
    init {
        FirebaseApp.initializeApp(context)
    }

    private val db = Firebase.firestore

     fun createUser(userId: String, name: String?, email: String? , telephone: String?, image: Uri?) {
        val documentReference = db.collection("Users").document(userId)

        documentReference.set(
            hashMapOf(
                "name" to name,
                "email" to email,
                "telephone" to telephone,
                "image" to image
            )
        )
            .addOnSuccessListener { Log.d("New User", userId) }
            .addOnFailureListener { Log.e("Errore", it.toString()) }
    }

    //  Users
    private val _users = MutableStateFlow(DataBase.users)
    val users: StateFlow<List<User>> = _users

    fun updateUser(userId: String, user: User) {
        val updatedUsers = _users.value.toMutableList()
        val index = updatedUsers.indexOfFirst { it.id == userId }

        if (index != -1) {
            updatedUsers[index] = user
            _users.value = updatedUsers
        }
    }

    fun updateKpi(userId: String, teamId: String, kpiCategory: String, value: Int = 1) {
        _users.value.find { it.id == userId }?.let { user ->
            val kpiValues = user.kpiValues.toMutableMap()
            val kpi = kpiValues[teamId]

            if (kpi != null) {
                if(kpiCategory == "assignedTasks") {
                    val newAssignedTasks = kpi.assignedTasks + value
                    val newScore = calculateScore(newAssignedTasks, kpi.completedTasks)

                    kpiValues[teamId] = kpi.copy(assignedTasks = newAssignedTasks, score = newScore)
                } else {
                    val newCompletedTasks = kpi.completedTasks + value
                    val newScore = calculateScore(kpi.assignedTasks, newCompletedTasks)

                    kpiValues[teamId] = kpi.copy(completedTasks = newCompletedTasks, score = newScore)
                }

                updateUser(userId, user.copy(kpiValues = kpiValues))
            }
        }
    }

    // add a new string category to the user
    fun addCategoryToUser(userId: String, c: String) {
        _users.value.find { it.id == userId }?.let { user ->
            val categories = user.categories.toMutableList().apply { add(c) }
            updateUser(userId, user.copy(categories = categories))
        }
    }

    fun updateCategory(userId: String, old: String, new: String) {
        if(old != "Recently assigned") {
            _users.value.find { it.id == userId }?.let { user ->
                val categories = user.categories.toMutableList()
                val idx = categories.indexOf(old)
                categories[idx] = new

                updateUser(userId, user.copy(categories = categories))
            }
        }
    }

    fun updateCategoryFromTask(taskId: String, userId: String, new: String) {
        _tasks.value.find { it.id == taskId }?.let { task ->
            val categories = task.categories.toMutableMap()
            categories[userId] = new
            updateTask(taskId, task.copy(categories = categories))
        }
    }

    fun deleteCategoryFromUser(userId: String, c: String){
        if(c != "Recently assigned") {
            _users.value.find { it.id == userId }?.let { user ->
                val categories = user.categories.toMutableList()
                categories.remove(c)

                updateUser(userId, user.copy(categories = categories))
            }
        }
    }

    //  Teams
    private val _teams = MutableStateFlow(DataBase.teams)
    val teams: StateFlow<List<Team>> = _teams

    fun addTeam(team: Team): String {
        val updatedTeams = _teams.value.toMutableList()
        val id = updatedTeams.size

        updatedTeams.add(team.copy(id = (id + 1).toString()))
        _teams.value = updatedTeams
        return (id + 1).toString()
    }

    fun updateTeam(teamId: String, team: Team) {
        val updatedTeams = _teams.value.toMutableList()
        val index = updatedTeams.indexOfFirst { it.id == teamId }

        if (index != -1) {
            updatedTeams[index] = team
            _teams.value = updatedTeams
        }
    }

    fun deleteTeam(teamId: String) {
        val updatedTeams = _teams.value.toMutableList()
        val teamTasks = _tasks.value.filter {it.teamId == teamId }.map { it.id }

        teamTasks.forEach { taskId ->
            deleteTask(taskId)
        }

        updatedTeams.removeIf { it.id == teamId }
        _teams.value = updatedTeams
    }

    fun updateRole(teamId: String, memberId: String, role: Role) {
        _teams.value.find { it.id == teamId }?.let { team ->
            val members: MutableList<Pair<String, Role>> = team.members.toMutableList()
            val idx = team.members.indexOfFirst { it.first == memberId }

            if(idx != -1) {
                members[idx] = memberId to role
                updateTeam(teamId, team.copy(members = members))
            }
        }
    }

    fun addMember(teamId: String, memberId: String): Boolean {
        _teams.value.find { it.id == teamId }?.let { team ->
            val members: MutableList<Pair<String, Role>> = team.members.toMutableList()

            if(members.none { it.first == memberId }) {
                members.add(memberId to Role.JUNIOR_MEMBER)
                updateTeam(teamId, team.copy(members = members))

                //  Update Kpi for new member
                _users.value.find { it.id == memberId }?.let { user ->
                    val updatedKpiValues = user.kpiValues.toMutableMap()
                    updatedKpiValues[teamId] = KPI(
                        assignedTasks = 0,
                        completedTasks = 0,
                        score = calculateScore(0, 0)
                    )

                    updateUser(user.id, user.copy(
                        joinedTeams = user.joinedTeams + 1,
                        kpiValues = updatedKpiValues
                    ))
                }

                return true
            }
        }
        return false
    }

    fun removeMember(teamId: String, memberId: String) {
        _teams.value.find { it.id == teamId }?.let { team ->
            val members: MutableList<Pair<String, Role>> = team.members.toMutableList()
            members.removeIf { it.first == memberId }

            updateTeam(teamId, team.copy(members = members))
        }
    }

    fun addMessage(teamId: String, message: Message) {
        _teams.value.find { it.id == teamId }?.let { team ->
            val chat = team.chat.toMutableList()

            chat.add(message)
            updateTeam(teamId, team.copy(chat = chat))
        }
    }

    //  Tasks
    private val _tasks = MutableStateFlow(DataBase.tasks)
    val tasks: StateFlow<List<Task>> = _tasks

    fun addTask(task: Task): String {
        val updatedTasks = _tasks.value.toMutableList()
        val id = updatedTasks.size
        updatedTasks.add(task.copy(id = (id + 1).toString()))

        _tasks.value = updatedTasks
        return (id + 1).toString()
    }

    fun updateTask(taskId: String, task: Task) {
        val updatedTasks = _tasks.value.toMutableList()
        val index = updatedTasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            updatedTasks[index] = task
            _tasks.value = updatedTasks
        }
    }

    fun deleteTask(taskId: String) {
        val updatedTask = _tasks.value.toMutableList()

        //  If state of deleted task is different of Completed, we decrease the assignedTasks Kpi value for all delegated members
        updatedTask.find { it.id == taskId }?.let { task ->
            if(task.state != TaskState.COMPLETED) {
                task.teamMembers.forEach { memberId ->
                    updateKpi(memberId, task.teamId, "assignedTasks", -1)
                }
            }
        }

        updatedTask.removeIf { it.id == taskId }
        _tasks.value = updatedTask
    }

    fun setTaskState(taskId: String, state: TaskState) {
        _tasks.value.find { it.id == taskId }?.let { task ->
            //  Increase completeTasks Kpi value for all delegated members when the state is Completed
            if(state == TaskState.COMPLETED) {
                task.teamMembers.forEach { memberId ->
                    updateKpi(memberId, task.teamId, "completedTasks")
                }
            }

            val history = task.history.toMutableList()
            history.add(
                Action(
                    id = task.history.size.toString(),
                    memberId = DataBase.LOGGED_IN_USER_ID,
                    taskState = state,
                    date = LocalDate.now(),
                    description = if(state == TaskState.COMPLETED) "Task completed"
                    else "Task state changed"
                )
            )

            updateTask(taskId, task.copy(state = state, history = history))
        }
    }

    fun addComment(taskId: String, comment: Comment) {
        _tasks.value.find { it.id == taskId }?.let { task ->
            val comments = task.comments.toMutableList()

            comments.add(comment)
            updateTask(taskId, task.copy(comments = comments))
        }
    }

    fun addAttachment(taskId: String, attachment: Attachment) {
        _tasks.value.find { it.id == taskId }?.let { task ->
            val attachments = task.attachments.toMutableList()
            val id = attachments.size

            attachments.add(attachment.copy(id = (id + 1).toString()))
            updateTask(taskId, task.copy(attachments = attachments))
        }
    }

    fun removeAttachment(taskId: String, attachmentId: String) {
        _tasks.value.find { it.id == taskId }?.let { task ->
            val attachments = task.attachments.toMutableList()

            attachments.removeIf { it.id == attachmentId }
            updateTask(taskId, task.copy(attachments = attachments))
        }
    }
}