package it.polito.lab5.model

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

class MyModel(val context: Context) {
    init {
        FirebaseApp.initializeApp(context)
    }

    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()


    private suspend fun uploadImage(
        imageId: String,
        byteArray: ByteArray,
        onSuccess: suspend (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
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
    fun getUser(userId: String): Flow<User?> = callbackFlow {
        val documentReference = db.collection("Users").document(userId)

        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val image = snapshot.get("image") as Map<String, String?>

                trySend(
                    User(
                        id = userId,
                        first = snapshot.get("first").toString(),
                        last = snapshot.get("last").toString(),
                        nickname = snapshot.get("nickname").toString(),
                        email = snapshot.get("email").toString(),
                        telephone = snapshot.get("telephone").toString(),
                        location = snapshot.get("location").toString(),
                        description = snapshot.get("description").toString(),
                        imageProfile = if (image["color"] != null) Empty(
                            Color(
                                image["color"]?.toULong() ?: 0UL
                            )
                        )
                        else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                        joinedTeams = snapshot.get("joinedTeams") as Long,
                        kpiValues = emptyMap(),
                        categories = snapshot.get("categories") as List<String>
                    )
                )
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
                trySend(null)
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    fun getUserKpi(userId: String): Flow<List<Pair<String, KPI>>> = callbackFlow {
        val ref = db.collection("Users").document(userId).collection("kpiValues")

        val snapshotListener = ref.addSnapshotListener { snapshot, err ->
            if (snapshot != null) {
                val kpiValues = snapshot.documents.map { document ->
                    document.id to KPI(
                        assignedTasks = document.get("assignedTasks") as Long,
                        completedTasks = document.get("completedTasks") as Long,
                        score = document.get("score") as Long
                    )
                }
                trySend(kpiValues)
            } else {
                if (err != null) {
                    Log.e("Server Error", err.message.toString())
                }
                trySend(emptyList())
            }
        }
        awaitClose{ snapshotListener.remove() }
    }

    @Suppress("unchecked_cast")
    fun getUsersTeam(members: List<String>): Flow<List<User>> = callbackFlow {
        val usersDocumentReference = db.collection("Users").whereIn(FieldPath.documentId(), members)

        val usersSnapshotListener = usersDocumentReference.addSnapshotListener { usersSnapshot, e ->
            if (usersSnapshot != null) {
                val users = usersSnapshot.documents.map { userDocument ->
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
                        imageProfile = if (image["color"] != null) Empty(
                            Color(
                                image["color"]?.toULong() ?: 0UL
                            )
                        )
                        else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                        joinedTeams = userDocument.getLong("joinedTeams") ?: 0L,
                        kpiValues = emptyMap(),
                        categories = userDocument.get("categories") as? List<String> ?: emptyList()
                    )
                }
                trySend(users)
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
                trySend(emptyList())
            }
        }
        awaitClose { usersSnapshotListener.remove() }
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

        if (byteArray != null) {
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
        } else if (user.imageProfile is Uploaded && user.imageProfile.image.toString()
                .contains("https://firebasestorage.googleapis.com")
        ) {
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

            if (deletePrevious) {
                deleteImage(userId)
            }
        }

        if (user.kpiValues.isNotEmpty()) {
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

    suspend fun updateUserKpi(userId: String, joinedTeams: Long, kpiValues: Pair<String, KPI>, delete: Boolean = false) {
        val documentReference = db.collection("Users").document(userId)

        val kpiValuesReference = documentReference.collection("kpiValues").document(kpiValues.first)

        if(delete) {
            kpiValuesReference.delete().await()
        } else {
            kpiValuesReference.set(
                mapOf(
                    "assignedTasks" to kpiValues.second.assignedTasks,
                    "completedTasks" to kpiValues.second.completedTasks,
                    "score" to kpiValues.second.score
                )
            ).await()
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
                "roles" to team.members.values.toList(),
                "unreadMessage" to team.unreadMessage.values.toList()
            )
        ).await()

        if (byteArray != null) {
            uploadImage(
                imageId = result.id,
                byteArray = byteArray,
                onSuccess = {   //  Update the "image" field of the new document
                    result.update(
                        "image",
                        mapOf(
                            "color" to null,
                            "url" to it
                        )
                    ).await()
                },
                onFailure = { Log.e("Server Error", it.message.toString()) }
            )
        } else {
            result.update(
                "image",
                mapOf(
                    "color" to (team.image as Empty).color.value.toString(),
                    "url" to null
                )
            ).await()
        }

        return result.id
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTeamChat(teamId: String): Flow<List<Message>> = callbackFlow {
        val ref = db.collection("Teams").document(teamId).collection("chat").orderBy("date", Query.Direction.ASCENDING)

        val chatsSnapshotListener = ref.addSnapshotListener { chatSnapshot, err ->
            if (chatSnapshot != null) {
                val chat = chatSnapshot.documents.map { messageDocument ->
                    val timestamp = messageDocument.get("date") as Timestamp
                    val literalReceiver = messageDocument.get("receiverId").toString()

                    Message(
                        id = messageDocument.id,
                        senderId = messageDocument.get("senderId").toString(),
                        receiverId = if(literalReceiver == "null") { null } else { literalReceiver },
                        content = messageDocument.get("content").toString(),
                        date = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC)
                    )
                }
                trySend(chat)
            } else {
                if (err != null) {
                    Log.e("Server Error", err.message.toString())
                }
                trySend(emptyList())
            }
        }
        awaitClose{ chatsSnapshotListener.remove() }
    }

    @Suppress("unchecked_cast")
    fun getTeam(teamId: String): Flow<Team?> = callbackFlow {
        val documentReference = db.collection("Teams").document(teamId)

        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val image = snapshot.get("image") as Map<String, String?>
                val members = (snapshot.get("members") as List<String>)
                    .zip((snapshot.get("roles") as List<String>).map {
                        when (it) {
                            "TEAM_MANAGER" -> Role.TEAM_MANAGER
                            "SENIOR_MEMBER" -> Role.SENIOR_MEMBER
                            else -> Role.JUNIOR_MEMBER
                        }
                    }).toMap()

                val unreadMessage = (snapshot.get("members") as List<String>)
                    .zip((snapshot.get("unreadMessage") as List<Boolean>)).toMap()

                trySend(
                    Team(
                        id = teamId,
                        name = snapshot.get("name").toString(),
                        description = snapshot.get("description").toString(),
                        image = if (image["color"] != null) Empty(
                            Color(
                                image["color"]?.toULong() ?: 0UL
                            )
                        )
                        else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                        members = members,
                        chat = emptyList(),
                        unreadMessage = unreadMessage
                    )
                )
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
                trySend(null)
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    @Suppress("unchecked_cast")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserTeams(userId: String): Flow<List<Team>> = callbackFlow {
        val documentReference = db.collection("Teams")
        val query = documentReference.whereArrayContains("members", userId)

        val snapshotListener = query.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val teams = snapshot.documents.map { document ->
                    val image = document.get("image") as Map<String, String?>
                    val members = (document.get("members") as List<String>)
                        .zip((document.get("roles") as List<String>).map {
                            when (it) {
                                "TEAM_MANAGER" -> Role.TEAM_MANAGER
                                "SENIOR_MEMBER" -> Role.SENIOR_MEMBER
                                else -> Role.JUNIOR_MEMBER
                            }
                        }).toMap()

                    val unreadMessage = (document.get("members") as List<String>)
                        .zip((document.get("unreadMessage") as List<Boolean>)).toMap()

                    Team(
                        id = document.id,
                        name = document.get("name").toString(),
                        description = document.get("description").toString(),
                        image = if (image["color"] != null) Empty(
                            Color(
                                image["color"]?.toULong() ?: 0UL
                            )
                        )
                        else Uploaded(image["url"]?.toUri() ?: "".toUri()),
                        members = members,
                        chat = emptyList(),
                        unreadMessage = unreadMessage
                    )
                }
                trySend(teams)
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
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

        if (byteArray != null) {
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
                            "roles" to team.members.values.toList(),
                            "unreadMessage" to team.unreadMessage.values.toList()
                        )
                    ).await()
                },
                onFailure = { Log.e("Server Error", it.message.toString()) }
            )
        } else if (team.image is Uploaded && team.image.image.toString()
                .contains("https://firebasestorage.googleapis.com")
        ) {
            documentReference.update(
                hashMapOf(
                    "name" to team.name,
                    "description" to team.description,
                    "image" to mapOf(
                        "color" to null,
                        "url" to team.image.image
                    ),
                    "members" to team.members.keys.toList(),
                    "roles" to team.members.values.toList(),
                    "unreadMessage" to team.unreadMessage.values.toList()
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
                    "roles" to team.members.values.toList(),
                    "unreadMessage" to team.unreadMessage.values.toList()
                )
            ).await()

            if (deletePrevious) {
                deleteImage(teamId)
            }
        }
    }

    suspend fun deleteTeam(team: Team, members: List<User>) {    //  TODO: removeTasks
        //  Remove Team image if needed
        if (team.image is Uploaded) {
            deleteImage(team.id)
        }

        //  Update kpi values for all team members
        members.filter { team.members.containsKey(it.id) }.forEach { member ->
            member.kpiValues[team.id]?.let { kpi ->
                updateUserKpi(member.id, member.joinedTeams - 1, team.id to kpi, true)
            }
        }

        //  Delete Team document
        db.collection("Teams").document(team.id).delete().await()
    }

    suspend fun addMessageToTeam(team: Team, message: Message) {
        db.collection("Teams").document(team.id).collection("chat").add(
            mapOf(
                "senderId" to message.senderId,
                "content" to message.content,
                "receiverId" to message.receiverId,
                "date" to Timestamp(Date(message.date.toInstant(ZoneOffset.UTC).toEpochMilli()))
            )
        ).await()

        val membersId = when(message.receiverId) {
            null -> team.members.keys.toList().filter { it != message.senderId }
            else -> listOf(message.receiverId)
        }

        updateUnreadMessage(team, membersId, true)
    }

    suspend fun addUserToTeam(team: Team, user: User) {
        val updatedMembers = team.members.toMutableMap()
        updatedMembers[user.id] = Role.JUNIOR_MEMBER

        val uploadedUnreadMessage = team.unreadMessage.toMutableMap()
        uploadedUnreadMessage[user.id] = false

        updateTeam(
            teamId = team.id, team = team.copy(
                members = updatedMembers,
                unreadMessage = uploadedUnreadMessage
            ), false
        )

        //  Update User kpi
        updateUserKpi(user.id, user.joinedTeams + 1, team.id to KPI(
            assignedTasks = 0,
            completedTasks = 0,
            score = calculateScore(0, 0)
        ))
    }

    suspend fun updateUserRole(userId: String, newRole: Role, team: Team) {
        val updatedMembers = team.members.toMutableMap()
        updatedMembers[userId] = newRole

        updateTeam(
            teamId = team.id, team = team.copy(
                members = updatedMembers
            ), false
        )
    }

    suspend fun updateUnreadMessage(team: Team, membersId: List<String>, value: Boolean) {
        val uploadedUnreadMessage = team.unreadMessage.toMutableMap()
        membersId.forEach { uploadedUnreadMessage[it] = value }

        updateTeam(
            teamId = team.id, team = team.copy(
                unreadMessage = uploadedUnreadMessage
            ), false
        )
    }

    suspend fun removeUserFromTeam(user: User, team: Team, chosenMember: String? = null) {
        //  Update User kpi
        user.kpiValues[team.id]?.let { kpi ->
            updateUserKpi(user.id, user.joinedTeams - 1, team.id to kpi, true)
        }

        //  Update Team
        val updatedMembers = team.members.toMutableMap()
        updatedMembers.remove(user.id)

        val uploadedUnreadMessage = team.unreadMessage.toMutableMap()
        uploadedUnreadMessage.remove(user.id)

        if (chosenMember != null) {
            updatedMembers[chosenMember] = Role.TEAM_MANAGER
        }

        updateTeam(
            teamId = team.id, team = team.copy(
                members = updatedMembers,
                unreadMessage = uploadedUnreadMessage
            ), false
        )
    }

    // TASKS

//    suspend fun createTask(task: Task, userId: String): String {
//        val documentReference = db.collection("Tasks")
//
//
//        //  Create the new task document
//        val result = documentReference.add(
//            hashMapOf(
//                "title" to task.title,
//                "description" to task.description,
//                "teamId" to task.teamId,
//                "dueDate" to task.dueDate?.toString(),
//                "repeat" to task.repeat,
//                "tag" to task.tag,
//                "teamMembers" to task.teamMembers,
//                "state" to task.state
//            )
//        ).await()
//        //comments -> empty map  attachments -> empty map
//        //history
//        documentReference.document(result.id).collection("history").add(
//            hashMapOf(
//                "memberId" to userId,
//                "taskState" to TaskState.NOT_ASSIGNED,
//                "date" to Timestamp.now(),
//                "description" to "Task created"
//            )
//        )
//        if (task.teamMembers.size > 0) {
//            documentReference.document(result.id).collection("history").add(
//                hashMapOf(
//                    "memberId" to userId,
//                    "taskState" to task.state,
//                    "date" to Timestamp.now(),
//                    "description" to "Task created"
//                )
//            )
//        }
//        //categories
//        for (memberId in task.teamMembers) {
//            documentReference.document(result.id).collection("categories").document(memberId)
//                .set("Recently Assigned")
//        }
//        return result.id
//    }

//    suspend fun getComments(taskId: String): MutableMap<String, Comment> {
//        val comments = mutableMapOf<String, Comment>()
//        val documentReference = db.collection("Tasks").document(taskId).collection("comments")
//
//        val snapshot = documentReference.get().await()
//        for (document in snapshot.documents) {
//            val comment = Comment(
//                content = document.get("content").toString(),
//                authorId = document.get("authorId").toString(),
//                date = document.get("date").toString() as LocalDateTime
//            )
//            if (comment != null) {
//                comments[document.id] = comment
//            }
//        }
//        return comments
//    }

//    suspend fun getAttachments(taskId: String): MutableMap<String, Attachment> {
//        val attachments = mutableMapOf<String, Attachment>()
//        val documentReference = db.collection("Tasks").document(taskId).collection("attachments")
//
//        val snapshot = documentReference.get().await()
//        for (document in snapshot.documents) {
//            val attachment = Attachment(
//                id = document.id,
//                name = document.get("name").toString(),
//                type = document.get("type").toString(),
//                uri = document.get("url").toString().toUri(),
//                size = document.get("size") as Float
//            )
//            if (attachment != null) {
//                attachments[document.id] = attachment
//            }
//        }
//        return attachments
//    }

//    suspend fun getHistory(taskId: String): Map<String, Action> {
//        val history = mutableMapOf<String, Action>()
//        val documentReference = db.collection("Tasks").document(taskId).collection("history")
//
//        val snapshot = documentReference.get().await()
//        for (document in snapshot.documents) {
//            val action = Action(
//                id = document.id,
//                memberId = document.get("memberId").toString(),
//                taskState = document.get("taskState") as TaskState,
//                date = document.get("date").toString() as LocalDate,
//                description = document.get("description").toString()
//            )
//            if (action != null) {
//                history[action.id] = action
//            }
//        }
//        return history
//    }

//    suspend fun getTask(taskId: String): Task? {
//        val documentReference = db.collection("Tasks").document(taskId)
//
//        val documentSnapshot = documentReference.get().await()
//
//        if (!documentSnapshot.exists()) {
//            return null
//        }
//
//        val task = documentSnapshot.toObject(Task::class.java)
//
//        if (task != null) {
//            task.comments = getComments(taskId)
//            task.attachments = getAttachments(taskId)
//            task.history = getHistory(taskId)
//        }
//
//        return task
//    }

//    suspend fun deleteTask2(taskId: String): Boolean {
//        val documentReference = db.collection("Tasks").document(taskId)
//
//        return try {
//            documentReference.delete().await()
//            true
//        } catch (e: Exception) {
//            println("Error deleting task: $e")
//            false
//        }
//    }

//    suspend fun updateTask(task: Task, userId: String): Boolean {
//        val documentReference = db.collection("Tasks").document(task.id)
//
//        // Update the basic details of the task
//        val taskData = hashMapOf(
//            "title" to task.title,
//            "description" to task.description,
//            "teamId" to task.teamId,
//            "dueDate" to task.dueDate?.toString(),
//            "repeat" to task.repeat,
//            "tag" to task.tag,
//            "teamMembers" to task.teamMembers,
//            "state" to task.state
//        )
//
//        return try {
//            documentReference.set(taskData).await()
//
//            // Update comments
//            val commentsCollection = documentReference.collection("comments")
//            // Delete existing comments
//            commentsCollection.get().await().forEach { it.reference.delete().await() }
//            // Add new comments
//            for (comment in task.comments.values) {
//                commentsCollection.add(
//                    hashMapOf(
//                        "content" to comment.content,
//                        "authorId" to comment.authorId,
//                        "date" to comment.date
//                    )
//                ).await()
//            }
//
//            // Update attachments
//            val attachmentsCollection = documentReference.collection("attachments")
//            // Delete existing attachments
//            attachmentsCollection.get().await().forEach { it.reference.delete().await() }
//            // Add new attachments
//            for (attachment in task.attachments.values) {
//                attachmentsCollection.add(
//                    hashMapOf(
//                        "name" to attachment.name,
//                        "type" to attachment.type,
//                        "url" to attachment.uri.toString(),
//                        "size" to attachment.size
//                    )
//                ).await()
//            }
//
//            // Update history
//            val historyCollection = documentReference.collection("history")
//            // Delete existing history
//            historyCollection.get().await().forEach { it.reference.delete().await() }
//            // Add new history actions
//            for (action in task.history.values) {
//                historyCollection.add(
//                    hashMapOf(
//                        "memberId" to action.memberId,
//                        "taskState" to action.taskState,
//                        "date" to action.date,
//                        "description" to action.description
//                    )
//                ).await()
//            }
//
//            // Update categories
//            val categoriesCollection = documentReference.collection("categories")
//            // Delete existing categories
//            categoriesCollection.get().await().forEach { it.reference.delete().await() }
//            // Add new categories
//            for ((key, value) in task.categories) {
//                categoriesCollection.document(key).set(
//                    hashMapOf(
//                        "category" to value
//                    )
//                ).await()
//            }
//
//            true
//        } catch (e: Exception) {
//            println("Error updating task: ${e.message}")
//            false
//        }
//    }


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
                if (kpiCategory == "assignedTasks") {
                    val newAssignedTasks = kpi.assignedTasks + value
                    val newScore = calculateScore(newAssignedTasks, kpi.completedTasks)

                    kpiValues[teamId] = kpi.copy(assignedTasks = newAssignedTasks, score = newScore)
                } else {
                    val newCompletedTasks = kpi.completedTasks + value
                    val newScore = calculateScore(kpi.assignedTasks, newCompletedTasks)

                    kpiValues[teamId] =
                        kpi.copy(completedTasks = newCompletedTasks, score = newScore)
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
        if (old != "Recently assigned") {
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

    fun deleteCategoryFromUser(userId: String, c: String) {
        if (c != "Recently assigned") {
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
            if (task.state != TaskState.COMPLETED) {
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
            if (state == TaskState.COMPLETED) {
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
                    description = if (state == TaskState.COMPLETED) "Task completed"
                    else "Task state changed"
                )
            )

            updateTask(taskId, task.copy(state = state, history = history))
        }
    }

    fun addComment(taskId: String, comment: Comment) {
        _tasks.value.find { it.id == taskId }?.let { task ->
            val comments = task.comments.toMutableList()
            val id = comments.size

            comments.add(comment.copy(id = (id + 1).toString()))
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