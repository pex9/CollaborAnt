package it.polito.lab5.model

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldPath
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


    private suspend fun uploadImage(imageId: String, byteArray: ByteArray, onSuccess: suspend (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = storage.reference

        try {
            val fileName = "images/${imageId}.jpg"
            val imageRef = storageRef.child(fileName)

            // Upload ByteArray to Firebase Storage
            imageRef.putBytes(byteArray).await()
            val downloadUrl = imageRef.downloadUrl.await()
            onSuccess(downloadUrl.toString())
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private suspend fun deleteImage(imageId: String) {
        val storageRef = storage.reference

        val fileName = "images/${imageId}.jpg"
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
                "categories" to user.categories
            )
        ).await()
    }

    @Suppress("unchecked_cast")
    fun getUser(userId: String) : Flow<User?> = callbackFlow{
        val documentReference = db.collection("Users").document(userId)

        val kpiValues: MutableMap<String, KPI> = emptyMap<String, KPI>().toMutableMap()
        val result = documentReference.collection("kpiValues").get().await()

        for(d in result.documents) {
            kpiValues[d.id] = KPI(
                assignedTasks = d.get("assignedTasks") as Long,
                completedTasks = d.get("completedTasks") as Long,
                score = d.get("score") as Long
            )
        }


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
                    kpiValues = kpiValues,
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
    fun getUsersTeam(members: List<String>): Flow<List<User>> = callbackFlow {
        val kpiList = members.map { memberId ->
            val result = db.collection("Users").document(memberId).collection("kpiValues").get().await()

            result.documents.associate {
                it.id to KPI(
                    assignedTasks = it.get("assignedTasks") as Long,
                    completedTasks = it.get("completedTasks") as Long,
                    score = it.get("score") as Long
                )
            }
        }

        val usersDocumentReference = db.collection("Users").whereIn(FieldPath.documentId(), members)

        val usersSnapshotListener = usersDocumentReference.addSnapshotListener { usersSnapshot, e ->
            if (usersSnapshot != null) {
                val users = usersSnapshot.documents.mapIndexed { idx, userDocument ->
                    val image = userDocument.get("image") as Map<String, String?>
                    User(
                        id = userDocument.id,
                        first = userDocument.getString("first") ?: "",
                        last = userDocument.getString("last") ?: "",
                        nickname = userDocument.getString("nickname") ?: "",
                        email = userDocument.getString("email") ?: "",
                        telephone = userDocument.getString("telephone") ?: "",
                        location = userDocument.getString("location") ?: "",
                        description = userDocument.getString("description") ?: "",
                        imageProfile = if (image["color"] != null) Empty(Color(image["color"]?.toULong() ?: 0UL))
                            else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                        joinedTeams = userDocument.getLong("joinedTeams") ?: 0L,
                        kpiValues = kpiList[idx],
                        categories = userDocument.get("categories") as? List<String> ?: emptyList()
                    )
                }
                trySend(users)
            } else {
                if (e != null) { Log.e("Server Error", e.message.toString()) }
                trySend(emptyList())
            }
        }
        awaitClose{ usersSnapshotListener.remove() }
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
                imageId = userId,
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
                            "categories" to user.categories
                        )
                    ).await()
                },
                onFailure = { Log.e("Server Error", it.message.toString()) }
            )
        } else if(user.imageProfile is Uploaded && user.imageProfile.image.toString().contains("https://firebasestorage.googleapis.com")) {
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
                        "url" to user.imageProfile.image
                    ),
                    "joinedTeams" to user.joinedTeams,
                    "categories" to user.categories
                )
            ).await()
        } else {
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
                        "categories" to user.categories
                    )
                ).await()

                if(deletePrevious) { deleteImage(userId) }
        }

        if(user.kpiValues.isNotEmpty()) {
            user.kpiValues.forEach { (teamId, kpi) ->
                documentReference.collection("kpiValues").document(teamId)
                    .set(
                        mapOf(
                            "assignedTasks" to kpi.assignedTasks,
                            "completedTasks" to kpi.completedTasks,
                            "score" to kpi.score
                        )
                    ).await()
            }
        }
    }

    suspend fun updateUserKpi(userId: String, joinedTeams: Long, kpiValues: Map<String, KPI>) {
        val documentReference = db.collection("Users").document(userId)

        if(kpiValues.isNotEmpty()) {
            kpiValues.forEach { (teamId, kpi) ->
                documentReference.collection("kpiValues").document(teamId)
                    .set(
                        mapOf(
                            "assignedTasks" to kpi.assignedTasks,
                            "completedTasks" to kpi.completedTasks,
                            "score" to kpi.score
                        )
                    ).await()
            }
        }

        documentReference.update("joinedTeams", joinedTeams).await()
    }

    //  Team
    suspend fun createTeam(team: Team): String {
        val documentReference = db.collection("Teams")
        val byteArray = when (team.image) {
            is Empty -> null
            is Taken -> bitmapToByteArray(team.image.image)
            is Uploaded -> uriToBitmap(context, team.image.image)?.let {
                bitmapToByteArray(it)
            }
        }

        //  Create the new team document
        val result = documentReference.add(
            hashMapOf(
                "name" to team.name,
                "description" to team.description,
                "members" to team.members.keys.toList(),
                "roles" to team.members.values.toList()
            )
        ).await()

        if(byteArray != null) {
            uploadImage(
                imageId = result.id,
                byteArray = byteArray,
                onSuccess = {   //  Update the "image" field of the new document
                    result.update("image",
                        mapOf(
                            "color" to null,
                            "url" to it
                        )
                    ).await()
                },
                onFailure = { Log.e("Server Error", it.message.toString()) }
            )
        } else {
            result.update("image",
                mapOf(
                    "color" to (team.image as Empty).color.value.toString(),
                    "url" to null
                )
            ).await()
        }

        return result.id
    }

    @Suppress("unchecked_cast")
    fun getTeam(teamId: String) : Flow<Team?> = callbackFlow{
        val documentReference = db.collection("Teams").document(teamId)

        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if(snapshot != null) {
                val image = snapshot.get("image") as Map<String, String?>
                val members = (snapshot.get("members") as List<String>)
                    .zip((snapshot.get("roles") as List<String>).map {
                        when(it) {
                            "TEAM_MANAGER" -> Role.TEAM_MANAGER
                            "SENIOR_MEMBER" -> Role.SENIOR_MEMBER
                            else -> Role.JUNIOR_MEMBER
                        }
                    }).toMap()

                trySend(Team(
                    id = teamId,
                    name = snapshot.get("name").toString(),
                    description = snapshot.get("description").toString(),
                    image = if(image["color"] != null) Empty(Color(image["color"]?.toULong() ?: 0UL))
                        else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                    members = members,
                    chat = emptyList() //   TODO: manage with sub-collection
                ))
            } else {
                if (e != null) { Log.e("Server Error", e.message.toString()) }
                trySend(null)
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    @Suppress("unchecked_cast")
    fun getUserTeams(userId: String): Flow<List<Team>> = callbackFlow {
        val documentReference = db.collection("Teams")
        val query = documentReference.whereArrayContains("members", userId)

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val teams = snapshot.documents.map { document ->
                    val image = document.get("image") as Map<String, String?>
                    val members = (document.get("members") as List<String>)
                        .zip((document.get("roles") as List<String>).map {
                            when(it) {
                                "TEAM_MANAGER" -> Role.TEAM_MANAGER
                                "SENIOR_MEMBER" -> Role.SENIOR_MEMBER
                                else -> Role.JUNIOR_MEMBER
                            }
                        }).toMap()

                    Team(
                        id = document.id,
                        name = document.get("name").toString(),
                        description = document.get("description").toString(),
                        image = if(image["color"] != null) Empty(Color(image["color"]?.toULong() ?: 0UL))
                            else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                        members = members,
                        chat = emptyList() //   TODO: manage with sub-collection
                    )
                }
                trySend(teams)
            } else {
                if (e != null) { Log.e("Server Error", e.message.toString()) }
                trySend(emptyList())
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    suspend fun updateTeam(teamId: String, team: Team, deletePrevious: Boolean) {
        val documentReference = db.collection("Teams").document(teamId)
        val byteArray = when (team.image) {
            is Empty -> null
            is Taken -> bitmapToByteArray(team.image.image)
            is Uploaded -> uriToBitmap(context, team.image.image)?.let {
                bitmapToByteArray(it)
            }
        }

        if(byteArray != null) {
            uploadImage(
                imageId = teamId,
                byteArray = byteArray,
                onSuccess = { url ->
                    documentReference.update(
                        hashMapOf(
                            "name" to team.name,
                            "description" to team.description,
                            "image" to mapOf(
                                "color" to null,
                                "url" to url
                            ),
                            "members" to team.members.keys.toList(),
                            "roles" to team.members.values.toList()
                        )
                    ).await()
                },
                onFailure = { Log.e("Server Error", it.message.toString()) }
            )
        } else if(team.image is Uploaded && team.image.image.toString().contains("https://firebasestorage.googleapis.com")) {
            documentReference.update(
                hashMapOf(
                    "name" to team.name,
                    "description" to team.description,
                    "image" to mapOf(
                        "color" to null,
                        "url" to team.image.image
                    ),
                    "members" to team.members.keys.toList(),
                    "roles" to team.members.values.toList()
                )
            ).await()
        } else {
            documentReference.update(
                hashMapOf(
                    "name" to team.name,
                    "description" to team.description,
                    "image" to mapOf(
                        "color" to (team.image as Empty).color.value.toString(),
                        "url" to null
                    ),
                    "members" to team.members.keys.toList(),
                    "roles" to team.members.values.toList()
                )
            ).await()

            if (deletePrevious) { deleteImage(teamId) }
        }
    }

    suspend fun deleteTeam(team: Team, members: List<User>) {    //  TODO: removeTasks
        //  Remove Team image if needed
        if(team.image is Uploaded) {
            deleteImage(team.id)
        }

        //  Update kpi values for all team members
        members.filter { team.members.containsKey(it.id) }.forEach { member ->
            val updatedKpiValues = member.kpiValues.toMutableMap()
            updatedKpiValues.remove(team.id)

            updateUserKpi(member.id, member.joinedTeams - 1, updatedKpiValues)
        }

        //  Delete Team document
        db.collection("Teams").document(team.id).delete().await()
    }

    suspend fun addUserToTeam(team: Team, user: User) {
        val updatedMembers = team.members.toMutableMap()
        updatedMembers[user.id] = Role.JUNIOR_MEMBER

        updateTeam(teamId = team.id, team = team.copy(
            members = updatedMembers
        ), false)

        //  User kpi
        val updatedKpiValues = user.kpiValues.toMutableMap()
        updatedKpiValues[team.id] = KPI(
            assignedTasks = 0,
            completedTasks = 0,
            score = calculateScore(0, 0)
        )

        updateUserKpi(user.id, user.joinedTeams + 1, updatedKpiValues)
    }

    suspend fun updateUserRole(userId: String, newRole: Role, team: Team) {
        val updatedMembers = team.members.toMutableMap()
        updatedMembers[userId] = newRole

        updateTeam(teamId = team.id, team = team.copy(
            members = updatedMembers
        ), false)
    }

    suspend fun removeUserFromTeam(user: User, team: Team, chosenMember: String? = null) {
        val updatedMembers = team.members.toMutableMap()
        updatedMembers.remove(user.id)

        if(chosenMember != null) {
            updatedMembers[chosenMember] = Role.TEAM_MANAGER
        }

        updateTeam(teamId = team.id, team = team.copy(
            members = updatedMembers
        ), false)

        //  Update User kpi
        val updatedKpiValues = user.kpiValues.toMutableMap()
        updatedKpiValues.remove(team.id)

        updateUserKpi(user.id, user.joinedTeams - 1, updatedKpiValues)
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

    fun updateT(teamId: String, team: Team) {
        val updatedTeams = _teams.value.toMutableList()
        val index = updatedTeams.indexOfFirst { it.id == teamId }

        if (index != -1) {
            updatedTeams[index] = team
            _teams.value = updatedTeams
        }
    }

    fun removeMember(teamId: String, memberId: String) {
        _teams.value.find { it.id == teamId }?.let { team ->
            val members: MutableMap<String, Role> = team.members.toMutableMap()
            members.remove(memberId)

            updateT(teamId, team.copy(members = members))
        }
    }

    fun addMessage(teamId: String, message: Message) {
        _teams.value.find { it.id == teamId }?.let { team ->
            val chat = team.chat.toMutableList()

            chat.add(message)
            updateT(teamId, team.copy(chat = chat))
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