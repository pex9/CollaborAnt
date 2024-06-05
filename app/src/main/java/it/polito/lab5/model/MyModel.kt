package it.polito.lab5.model

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class MyModel(val context: Context) {
    init {
            FirebaseApp.initializeApp(context)
        }

        private val db = Firebase.firestore
        private val storage = FirebaseStorage.getInstance()

        private suspend fun uploadImage(userId: String, byteArray: ByteArray, onSuccess: suspend (String) -> Unit, onFailure: (Exception) -> Unit) {
            val storageRef = storage.reference

            try {
                val fileName = "images/${userId}.jpg"
                val imageRef = storageRef.child(fileName)

                // Upload ByteArray to Firebase Storage
                imageRef.putBytes(byteArray).await()
                val downloadUrl = imageRef.downloadUrl.await()
                onSuccess(downloadUrl.toString())
            } catch (e: Exception) {
                onFailure(e)
        }
    }
    private suspend fun uploadImageTeam(TeamId: String, byteArray: ByteArray, onSuccess: suspend (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = storage.reference

        try {
            val fileName = "images/${TeamId}.jpg"
            val imageRef = storageRef.child(fileName)

            // Upload ByteArray to Firebase Storage
            imageRef.putBytes(byteArray).await()
            val downloadUrl = imageRef.downloadUrl.await()
            onSuccess(downloadUrl.toString())
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private suspend fun deleteImage(userId: String) {
        val storageRef = storage.reference

        val fileName = "images/${userId}.jpg"
        val imageRef = storageRef.child(fileName)
        imageRef.delete().await()
    }

    suspend fun createUser(user: User) {
        val documentReference = db.collection("Users").document(user.id)

        documentReference.set(
            hashMapOf(
                "first" to user.first,
                "last" to user.last,
                "nickname" to user.nickname,
                "email" to user.email,
                "telephone" to user.telephone,
                "location" to user.location,
                "description" to user.description,
                "image" to mapOf(
                    "color" to (user.imageProfile as Empty).color.value.toString(),
                    "url" to null
                ),
                "joinedTeams" to user.joinedTeams,
                "kpiValues" to user.kpiValues,
                "categories" to user.categories
            )
        ).await()
    }

    @Suppress("unchecked_cast")
    fun getUser(userId: String) : Flow<User?> = callbackFlow{
        val documentReference = db.collection("Users").document(userId)
        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if(snapshot != null) {
                val image = snapshot.get("image") as Map<String, String?>

                trySend(User(
                    id = userId,
                    first = snapshot.get("first").toString(),
                    last = snapshot.get("last").toString(),
                    nickname = snapshot.get("nickname").toString(),
                    email = snapshot.get("email").toString(),
                    telephone = snapshot.get("telephone").toString(),
                    location = snapshot.get("location").toString(),
                    description = snapshot.get("description").toString(),
                    imageProfile = if(image["color"] != null) Empty(Color(image["color"]?.toULong() ?: 0UL))
                        else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                    joinedTeams = snapshot.get("joinedTeams") as Long,
                    kpiValues = snapshot.get("kpiValues") as Map<String, KPI>,
                    categories = snapshot.get("categories") as List<String>
                ))
            } else {
                if (e != null) { Log.e("Server Error", e.message.toString()) }
                trySend(null)
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    @Suppress("unchecked_cast")
    fun getUsers(): Flow<List<User>> = callbackFlow {
        val documentReference = db.collection("Users")
        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val users = snapshot.documents.mapNotNull { document ->
                    val image = document.get("image") as Map<String, String?>
                    try {
                        User(
                            id = document.id,
                            first = document.getString("first") ?: "",
                            last = document.getString("last") ?: "",
                            nickname = document.getString("nickname") ?: "",
                            email = document.getString("email") ?: "",
                            telephone = document.getString("telephone") ?: "",
                            location = document.getString("location") ?: "",
                            description = document.getString("description") ?: "",
                            imageProfile = if(image["color"] != null) Empty(Color(image["color"]?.toULong() ?: 0UL))
                            else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                            joinedTeams = document.getLong("joinedTeams") ?: 0L,
                            kpiValues = document.get("kpiValues") as? Map<String, KPI> ?: emptyMap(),
                            categories = document.get("categories") as? List<String> ?: emptyList()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(users).isSuccess
            } else {
                trySend(emptyList<User>()).isSuccess
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    suspend fun updateUser(userId: String, user: User, deletePrevious: Boolean) {
        val documentReference = db.collection("Users").document(userId)
        val byteArray = when (user.imageProfile) {
            is Empty -> null
            is Taken -> bitmapToByteArray(user.imageProfile.image)
            is Uploaded -> uriToBitmap(context, user.imageProfile.image)?.let {
                bitmapToByteArray(it)
            }
        }

        if(byteArray != null) {
            uploadImage(
                userId = userId,
                byteArray = byteArray,
                onSuccess = {
                    documentReference.update(
                        hashMapOf(
                            "first" to user.first,
                            "last" to user.last,
                            "nickname" to user.nickname,
                            "email" to user.email,
                            "telephone" to user.telephone,
                            "location" to user.location,
                            "description" to user.description,
                            "image" to mapOf(
                                "color" to null,
                                "url" to it
                            ),
                            "joinedTeams" to user.joinedTeams,
                            "kpiValues" to user.kpiValues,
                            "categories" to user.categories
                        )
                    ).await()
                },
                onFailure = { Log.e("Server Error", it.message.toString()) }
            )
        } else {
            try {
                documentReference.update(
                    hashMapOf(
                        "first" to user.first,
                        "last" to user.last,
                        "nickname" to user.nickname,
                        "email" to user.email,
                        "telephone" to user.telephone,
                        "location" to user.location,
                        "description" to user.description,
                        "image" to mapOf(
                            "color" to (user.imageProfile as Empty).color.value.toString(),
                            "url" to null
                        ),
                        "joinedTeams" to user.joinedTeams,
                        "kpiValues" to user.kpiValues,
                        "categories" to user.categories
                    )
                ).await()

                if(deletePrevious) { deleteImage(userId) }
            } catch (e: Exception) {
                Log.e("Server Error", e.message.toString())
            }
        }
    }

    //team
    suspend fun createTeam(team: Team) {
        val documentReference = db.collection("Teams").document(team.id)

        documentReference.set(
            hashMapOf(
                "id" to team.id,
                "name" to team.name,
                "description" to team.description,
                "image" to mapOf(
                    "color" to (team.image as Empty).color.value.toString(),
                    "url" to null
                ),
                "members" to team.members,
                "chat" to team.chat
            )
        ).await()
    }
//    @Suppress("unchecked_cast")
//    fun getTeam(TeamId: String) : Flow<Team?> = callbackFlow{
//        val documentReference = db.collection("Teams").document(TeamId)
//        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
//            if(snapshot != null) {
//                val image = snapshot.get("image") as Map<String, String?>
//
//                trySend(Team(
//                    id = TeamId,
//                    name = snapshot.get("name").toString(),
//                    description = snapshot.get("description").toString(),
//                    image = if(image["color"] != null) Empty(Color(image["color"]?.toULong() ?: 0UL))
//                    else Uploaded(image["url"]?.toUri() ?: "".toUri()),
//                    members = snapshot.get("members") as List<Pair<String,Role>>,
//                    chat= snapshot.get("chat") as List<Message>
//                ))
//            } else {
//                if (e != null) { Log.e("Server Error", e.message.toString()) }
//                trySend(null)
//            }
//        }
//        awaitClose { snapshotListener.remove() }
//    }
//    fun getTeamsByUserId(userId: String): Flow<List<Team>> = callbackFlow {
//        val documentReference = db.collection("Teams")
//        val query = documentReference.whereArrayContains("members", userId)
//        val snapshotListener = query.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                close(e)
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null && !snapshot.isEmpty) {
//                val teams = snapshot.documents.mapNotNull { document ->
//                    val image = document.get("image") as Map<String, String?>
//                    Team(
//                        id = document.get("id").toString(),
//                        name = document.get("name").toString(),
//                        description = document.get("description").toString(),
//                        image = if(image["color"] != null) Empty(Color(image["color"]?.toULong() ?: 0UL))
//                        else Uploaded(image["url"]?.toUri() ?: "".toUri()),
//                        members = document.get("members") as List<Pair<String,Role>>,
//                        chat= document.get("chat") as List<Message>
//                    )
//                }
//                trySend(teams).isSuccess
//            } else {
//                trySend(emptyList<Team>()).isSuccess
//            }
//        }
//
//        awaitClose { snapshotListener.remove() }
//
//    }
    //update team riceve il team aggiornanto es nuovo membro, ruolo diverso o altro
    suspend fun updateTeam2(teamId: String, team: Team,deletePrevious: Boolean) {
        val documentReference = db.collection("Teams").document(teamId)
        val byteArray = when (team.image) {
            is Empty -> null
            is Taken -> bitmapToByteArray(team.image.image)
            is Uploaded -> uriToBitmap(context, team.image.image)?.let {
                bitmapToByteArray(it)
            }
        }
        if(byteArray != null) {
            uploadImageTeam(
                TeamId = teamId,
                byteArray = byteArray,
                onSuccess = {
                    documentReference.update(
                        hashMapOf(
                            "name" to team.name,
                            "description" to team.description,
                            "image" to mapOf(
                                "color" to null,
                                "url" to it
                            ),
                            "members" to team.members,
                            "chat" to team.chat,
                        )
                    ).await()
                },
                onFailure = { Log.e("Server Error", it.message.toString()) }
            )
        } else {
            try {
                documentReference.update(
                    hashMapOf(
                        "name" to team.name,
                        "description" to team.description,
                        "image" to mapOf(
                            "color" to (team.image as Empty).color.value.toString(),
                            "url" to null
                        ),
                        "members" to team.members,
                        "chat" to team.chat,
                    )
                ).await()
                if (deletePrevious) {
                    deleteImage(teamId)
                }
            } catch (e: Exception) {
                Log.e("Server Error", e.message.toString())
            }
        }

    }
    suspend fun deleteTeam2(teamId: String) {
        val documentReference = db.collection("Teams").document(teamId)
        try {
            documentReference.delete().await()
        } catch (e: Exception) {
            Log.e("Server Error", e.message.toString())
        }
    }


    //  Users
    private val _users = MutableStateFlow(DataBase.users)
    val users: StateFlow<List<User>> = _users

    fun updateU(userId: String, user: User) {
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

                updateU(userId, user.copy(kpiValues = kpiValues))
            }
        }
    }

    // add a new string category to the user
    fun addCategoryToUser(userId: String, c: String) {
        _users.value.find { it.id == userId }?.let { user ->
            val categories = user.categories.toMutableList().apply { add(c) }
            updateU(userId, user.copy(categories = categories))
        }
    }

    fun updateCategory(userId: String, old: String, new: String) {
        if(old != "Recently assigned") {
            _users.value.find { it.id == userId }?.let { user ->
                val categories = user.categories.toMutableList()
                val idx = categories.indexOf(old)
                categories[idx] = new

                updateU(userId, user.copy(categories = categories))
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

                updateU(userId, user.copy(categories = categories))
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
            val members: MutableMap<String, Role> = team.members.toMutableMap()

            if(team.members[memberId] != null) {
                members[memberId] = role
                updateTeam(teamId, team.copy(members = members))
            }
        }
    }

    fun addMember(teamId: String, memberId: String): Boolean {
        _teams.value.find { it.id == teamId }?.let { team ->
            val members: MutableMap<String, Role> = team.members.toMutableMap()

            if(members.none { it.key == memberId }) {
                members[memberId] = Role.JUNIOR_MEMBER
                updateTeam(teamId, team.copy(members = members))

                //  Update Kpi for new member
                _users.value.find { it.id == memberId }?.let { user ->
                    val updatedKpiValues = user.kpiValues.toMutableMap()
                    updatedKpiValues[teamId] = KPI(
                        assignedTasks = 0,
                        completedTasks = 0,
                        score = calculateScore(0, 0)
                    )

                    updateU(user.id, user.copy(
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
            val members: MutableMap<String, Role> = team.members.toMutableMap()
            members.remove(memberId)

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