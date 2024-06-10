package it.polito.lab5.model

import android.content.Context
import android.net.Uri
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
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
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

    private suspend fun uploadAttachment(
        taskId: String,
        attachment: Attachment,
        onSuccess: suspend (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = storage.reference

        try {
            val (name, extension) = attachment.name.split(".")
            val fileName = "attachments/${taskId}/${name}(${attachment.id}).${extension}"
            val attachmentRef = storageRef.child(fileName)

            // Upload ByteArray to Firebase Storage
            uriToByteArray(this.context, attachment.uri)?.let {
                attachmentRef.putBytes(it).await()
                val downloadUrl = attachmentRef.downloadUrl.await()
                onSuccess(downloadUrl.toString())
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun downloadFileFromFirebase(taskId: String, attachment: Attachment, onComplete: (File) -> Unit, onFailure: (Exception) -> Unit) {
        // Create a storage reference from our app
        val storageRef: StorageReference = storage.reference
        val (name, extension) = attachment.name.split(".")
        val fileName = "attachments/${taskId}/${name}(${attachment.id}).${extension}"
        val localFile = File(context.cacheDir, "${name}(${attachment.id}).${extension}")

        // Create a reference to the file you want to download
        val fileRef: StorageReference = storageRef.child(fileName)

        try {
            // Download file to local file
            fileRef.getFile(localFile).await()
            onComplete(localFile)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private suspend fun deleteAttachment(taskId: String, attachment: Attachment) {
        val storageRef = storage.reference

        val fileName = "attachments/${taskId}/${attachment.id}.${attachment.type.split("/").last()}"
        val attachmentRef = storageRef.child(fileName)
        attachmentRef.delete().await()
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

    suspend fun addCategoryToUser(user: User, newCategory: String) {
        val documentReference = db.collection("Users").document(user.id)

        val updatedCategories = user.categories.toMutableList()
        if(!updatedCategories.contains(newCategory)) {
            updatedCategories.add(newCategory)

            documentReference.update("categories", updatedCategories).await()
        }
    }

    suspend fun updateCategoryToUser(user: User, oldCategory: String, newCategory: String) {
        val documentReference = db.collection("Users").document(user.id)

        val updatedCategories = user.categories.toMutableList()
        val idx = updatedCategories.indexOfFirst { it == oldCategory }
        if(idx != -1) {
            updatedCategories[idx] = newCategory
            documentReference.update("categories", updatedCategories).await()
        }
    }

    suspend fun removeCategoryFromUser(user: User, category: String) {
        val documentReference = db.collection("Users").document(user.id)

        val updatedCategories = user.categories.toMutableList()
        val idx = updatedCategories.indexOfFirst { it == category }
        if(idx != -1) {
            updatedCategories.removeAt(idx)
            documentReference.update("categories", updatedCategories).await()
        }
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
        val ref = db.collection("Teams").document(teamId)
            .collection("chat").orderBy("date", Query.Direction.ASCENDING)

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
            if (snapshot != null && snapshot.exists()) {
                var image: Map<String, String?> = emptyMap()

                if(snapshot.get("image") is Map<*, *>) {
                    image = snapshot.get("image") as Map<String, String?>
                }

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

    suspend fun deleteTeam(team: Team, members: List<User>) {
        val storageRef = storage.reference

        //  Remove Team image if needed
        if (team.image is Uploaded) {
            deleteImage(team.id)
        }

        //  Update kpi values for all team members
        members.forEach { member ->
            member.kpiValues[team.id]?.let { kpi ->
                updateUserKpi(member.id, member.joinedTeams - 1, team.id to kpi, true)
            }
        }

        val teamReference = db.collection("Teams").document(team.id)

        //  Delete Team chat
        val r = teamReference.collection("chat").get().await()
        r.documents.forEach { teamReference.collection("chat").document(it.id).delete().await() }

        db.collection("Tasks").whereEqualTo("teamId", team.id).get().await().documents.forEach { documentSnapshot ->
            val commentsReference = documentSnapshot.reference.collection("comments")
            val attachmentsReference = documentSnapshot.reference.collection("attachments")
            val historyReference = documentSnapshot.reference.collection("history")

            //  Delete Task comments
            commentsReference.get().await().documents.forEach { commentsReference.document(it.id).delete().await() }

            //  Delete Task attachments
            storageRef.child("attachments/${documentSnapshot.id}").listAll().await().items.forEach { it.delete().await() }
            attachmentsReference.get().await().documents.forEach { attachmentsReference.document(it.id).delete().await() }

            //  Delete Task history
            historyReference.get().await().documents.forEach { historyReference.document(it.id).delete().await() }

            //  Delete Task
            documentSnapshot.reference.delete().await()
        }

        //  Delete Team document
        teamReference.delete().await()
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

    // Tasks
    suspend fun createTask(task: Task): String {
        val documentReference = db.collection("Tasks")

        //  Create the new task document
        val result = documentReference.add(
            hashMapOf(
                "title" to task.title,
                "description" to task.description,
                "parentId" to task.parentId,
                "teamId" to task.teamId,
                "dueDate" to localDateToTimestamp(task.dueDate, ZoneId.systemDefault()),
                "repeat" to task.repeat,
                "tag" to task.tag,
                "teamMembers" to task.teamMembers,
                "state" to task.state,
                "categories" to task.categories.values.toList(),
                "endDateRepeat" to localDateToTimestamp(task.endDateRepeat, ZoneId.systemDefault())
            )
        ).await()

        //history
        val historyReference = documentReference.document(result.id).collection("history")

        task.history.forEach { action ->
            historyReference.add(
                hashMapOf(
                    "memberId" to action.memberId,
                    "taskState" to action.taskState,
                    "date" to Timestamp(Date(action.date.toInstant(ZoneOffset.UTC).toEpochMilli())),
                    "description" to action.description
                )
            ).await()
        }

        return result.id
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTaskComments(taskId: String): Flow<List<Comment>> = callbackFlow {
        val documentReference = db.collection("Tasks")
            .document(taskId).collection("comments").orderBy("date", Query.Direction.ASCENDING)

        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val comments = snapshot.documents.map { document ->
                    val timestamp = document.get("date") as Timestamp

                    Comment(
                        id = document.id,
                        content = document.getString("content") ?: "",
                        authorId = document.getString("authorId") ?: "",
                        date = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC)
                    )
                }
                trySend(comments)
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
                trySend(emptyList())
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    fun getAttachments(taskId: String): Flow<List<Attachment>> = callbackFlow {
        val documentReference = db.collection("Tasks").document(taskId)
            .collection("attachments").orderBy("name", Query.Direction.ASCENDING)

        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val attachments = snapshot.documents.map { document ->
                    val attachmentUrl = document.getString("url")

                    Attachment(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        type = document.getString("type") ?: "",
                        uri = attachmentUrl?.toUri() ?: Uri.EMPTY,
                        size = document.getDouble("size") ?: 0.0
                    )
                }

                trySend(attachments)
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
                trySend(emptyList())
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHistory(taskId: String): Flow<List<Action>> = callbackFlow {
        val documentReference = db.collection("Tasks")
            .document(taskId).collection("history").orderBy("date", Query.Direction.ASCENDING)

        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                val history = snapshot.documents.map { document ->
                    val timestamp = document.get("date") as Timestamp
                    val state = getTaskState(document.getString("taskState"))

                    Action(
                        id = document.id,
                        memberId = document.getString("memberId") ?: "",
                        taskState = state,
                        date = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC),
                        description = document.getString("description") ?: ""
                    )
                }
                trySend(history)
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
                trySend(emptyList())
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("unchecked_cast")     //  TODO: add Overdue check
    fun getTask(taskId: String): Flow<Task?> = callbackFlow {
        val documentReference = db.collection("Tasks").document(taskId)

        val snapshotListener = documentReference.addSnapshotListener { snapshot, e ->
            if (snapshot != null && snapshot.exists()) {
                val dueDateTimestamp = snapshot.get("dueDate")?.let { it as Timestamp }
                val endDateRepeatTimestamp = snapshot.get("endDateRepeat")?.let { it as Timestamp }
                val literalParentId = snapshot.get("parentId").toString()
                val delegatedMembers = snapshot.get("teamMembers") as List<String>
                val categories = delegatedMembers.zip(snapshot.get("categories") as List<String>).toMap()

                trySend(
                    Task(
                        id = taskId,
                        title = snapshot.getString("title") ?: "",
                        description = snapshot.getString("description") ?: "",
                        teamId = snapshot.getString("teamId") ?: "",
                        dueDate = dueDateTimestamp?.let { LocalDateTime.ofInstant(it.toInstant(), ZoneOffset.UTC).toLocalDate() },
                        repeat = getRepeat(snapshot.getString("repeat")),
                        parentId = if(literalParentId == "null") { null } else literalParentId,
                        endDateRepeat = endDateRepeatTimestamp?.let { LocalDateTime.ofInstant(it.toInstant(), ZoneOffset.UTC).toLocalDate() },
                        tag = getTag(snapshot.getString("tag")),
                        teamMembers = snapshot.get("teamMembers") as List<String>,
                        state = getTaskState(snapshot.getString("state")),
                        comments = emptyList(),
                        categories = categories,
                        attachments = emptyList(),
                        history = emptyList(),
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("unchecked_cast")     //  TODO: add Overdue check
    fun getTasksTeam(teamId: String): Flow<List<Task>> = callbackFlow {
        val tasksDocumentReference = db.collection("Tasks").whereEqualTo("teamId", teamId)

        val tasksSnapshotListener = tasksDocumentReference.addSnapshotListener { tasksSnapshot, e ->
            if (tasksSnapshot != null) {
                val tasks = tasksSnapshot.documents.map { taskDocument ->
                    val dueDateTimestamp = taskDocument.get("dueDate")?.let { it as Timestamp }
                    val endDateRepeatTimestamp = taskDocument.get("endDateRepeat")?.let { it as Timestamp }
                    val literalParentId = taskDocument.get("parentId").toString()
                    val delegatedMembers = taskDocument.get("teamMembers") as List<String>
                    val categories = delegatedMembers.zip(taskDocument.get("categories") as List<String>).toMap()

                    Task(
                        id = taskDocument.id,
                        title = taskDocument.getString("title") ?: "",
                        description = taskDocument.getString("description") ?: "",
                        teamId = taskDocument.getString("teamId") ?: "",
                        dueDate = dueDateTimestamp?.let { LocalDateTime.ofInstant(it.toInstant(), ZoneOffset.UTC).toLocalDate() },
                        repeat = getRepeat(taskDocument.getString("repeat")),
                        parentId = if(literalParentId == "null") { null } else literalParentId,
                        endDateRepeat = endDateRepeatTimestamp?.let { LocalDateTime.ofInstant(it.toInstant(), ZoneOffset.UTC).toLocalDate() },
                        tag = getTag(taskDocument.getString("tag")),
                        teamMembers = taskDocument.get("teamMembers") as List<String>,
                        state = getTaskState(taskDocument.getString("state")),
                        comments = emptyList(),
                        categories = categories,
                        attachments = emptyList(),
                        history = emptyList(),
                    )
                }
                trySend(tasks)
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
                trySend(emptyList())
            }
        }
        awaitClose { tasksSnapshotListener.remove() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("unchecked_cast")     //  TODO: add Overdue check
    fun getUserTasks(userId: String): Flow<List<Task>> = callbackFlow {
        val tasksDocumentReference = db.collection("Tasks")
            .whereArrayContains("teamMembers", userId).whereNotEqualTo("state", "COMPLETED")

        val tasksSnapshotListener = tasksDocumentReference.addSnapshotListener { tasksSnapshot, e ->
            if (tasksSnapshot != null) {
                val tasks = tasksSnapshot.documents.map { taskDocument ->
                    val dueDateTimestamp = taskDocument.get("dueDate")?.let { it as Timestamp }
                    val endDateRepeatTimestamp = taskDocument.get("endDateRepeat")?.let { it as Timestamp }
                    val literalParentId = taskDocument.get("parentId").toString()
                    val delegatedMembers = taskDocument.get("teamMembers") as List<String>
                    val categories = delegatedMembers.zip(taskDocument.get("categories") as List<String>).toMap()

                    Task(
                        id = taskDocument.id,
                        title = taskDocument.getString("title") ?: "",
                        description = taskDocument.getString("description") ?: "",
                        teamId = taskDocument.getString("teamId") ?: "",
                        dueDate = dueDateTimestamp?.let { LocalDateTime.ofInstant(it.toInstant(), ZoneOffset.UTC).toLocalDate() },
                        repeat = getRepeat(taskDocument.getString("repeat")),
                        parentId = if(literalParentId == "null") { null } else literalParentId,
                        endDateRepeat = endDateRepeatTimestamp?.let { LocalDateTime.ofInstant(it.toInstant(), ZoneOffset.UTC).toLocalDate() },
                        tag = getTag(taskDocument.getString("tag")),
                        teamMembers = taskDocument.get("teamMembers") as List<String>,
                        state = getTaskState(taskDocument.getString("state")),
                        comments = emptyList(),
                        categories = categories,
                        attachments = emptyList(),
                        history = emptyList(),
                    )
                }
                trySend(tasks)
            } else {
                if (e != null) {
                    Log.e("Server Error", e.message.toString())
                }
                trySend(emptyList())
            }
        }
        awaitClose { tasksSnapshotListener.remove() }
    }

    suspend fun updateTask(task: Task) {
        db.collection("Tasks").document(task.id).update(
            hashMapOf(
                "title" to task.title,
                "description" to task.description,
                "parentId" to task.parentId,
                "teamId" to task.teamId,
                "dueDate" to localDateToTimestamp(task.dueDate, ZoneId.systemDefault()),
                "repeat" to task.repeat,
                "tag" to task.tag,
                "teamMembers" to task.teamMembers,
                "state" to task.state,
                "categories" to task.categories.values.toList(),
                "endDateRepeat" to localDateToTimestamp(task.endDateRepeat, ZoneId.systemDefault())
            )
        ).await()
    }

    suspend fun deleteTask(task: Task, delegateMembers: List<User>) {   //  TODO: manage case of recurrent task
        val storageRef = storage.reference
        val taskReference = db.collection("Tasks").document(task.id)
        val commentsReference = taskReference.collection("comments")
        val attachmentsReference = taskReference.collection("attachments")
        val historyReference = taskReference.collection("history")

        //  Delete Task comments
        commentsReference.get().await().documents.forEach { commentsReference.document(it.id).delete().await() }

        //  Delete Task attachments
        storageRef.child("attachments/${task.id}").listAll().await().items.forEach { it.delete().await() }
        attachmentsReference.get().await().documents.forEach { attachmentsReference.document(it.id).delete().await() }

        //  Delete Task history
        historyReference.get().await().documents.forEach { historyReference.document(it.id).delete().await() }

        //  Update kpi for delegated members
        if (task.state != TaskState.COMPLETED) {
            delegateMembers.forEach { member ->
                member.kpiValues[task.teamId]?.let { kpi ->
                    val updatedKpi = kpi.copy(
                        assignedTasks = kpi.assignedTasks - 1,
                        score = calculateScore(kpi.assignedTasks - 1, kpi.completedTasks)
                    )

                    updateUserKpi(member.id, member.joinedTeams, task.teamId to updatedKpi)
                }
            }
        }

        //  Delete Task
        taskReference.delete().await()
    }

    suspend fun deleteAttachmentFromTask(taskId: String, attachment: Attachment) {
        val attachmentsReference = db.collection("Tasks").document(taskId).collection("attachments")

        //  Delete attachment from storage
        deleteAttachment(taskId, attachment)

        //  Delete attachment document
        attachmentsReference.document(attachment.id).delete().await()
    }

    suspend fun addActionToTaskHistory(taskId: String, action: Action) {
        val historyReference = db.collection("Tasks").document(taskId).collection("history")

        historyReference.add(
            hashMapOf(
                "memberId" to action.memberId,
                "taskState" to action.taskState,
                "date" to Timestamp(Date(action.date.toInstant(ZoneOffset.UTC).toEpochMilli())),
                "description" to action.description
            )
        ).await()
    }

    suspend fun addAttachmentToTask(taskId: String, attachment: Attachment) {
        val attachmentsReference = db.collection("Tasks").document(taskId).collection("attachments")

        val result = attachmentsReference.add(
            hashMapOf(
                "name" to attachment.name,
                "type" to attachment.type,
                "uri" to attachment.uri,
                "size" to attachment.size
            )
        ).await()

        uploadAttachment(
            taskId = taskId,
            attachment = attachment.copy(id = result.id),
            onSuccess = { result.update("uri", it).await() },
            onFailure = { Log.e("Server Error", it.message.toString()) }
        )
    }

    suspend fun updateTaskState(task: Task, delegatedMembers: List<User>, loggedInUserId: String, state: TaskState) {
        //  Update delegated members kpi
        if (state == TaskState.COMPLETED) {
            delegatedMembers.forEach { member ->
                val kpi = member.kpiValues[task.teamId]
                val updatedKpi = kpi?.copy(
                    completedTasks = kpi.completedTasks + 1,
                    score = calculateScore(kpi.assignedTasks, kpi.completedTasks + 1)
                )

                updatedKpi?.let {updateUserKpi(member.id, member.joinedTeams, task.teamId to it) }
            }
        }

        //  Add new action to Task history
        addActionToTaskHistory(task.id,
            Action(
                id = "",
                memberId = loggedInUserId,
                taskState = state,
                date = LocalDateTime.now(),
                description = if (state == TaskState.COMPLETED) "Task completed"
                else "Task state changed"
            )
        )

        db.collection("Tasks").document(task.id).update("state", state).await()
    }

    suspend fun updateUserCategoryToTask(task: Task, userId: String, newCategory: String) {
        val taskReference = db.collection("Tasks").document(task.id)

        val updatedCategories = task.categories.toMutableMap()
        updatedCategories[userId] = newCategory

        taskReference.update("categories", updatedCategories.values.toList()).await()
    }

    suspend fun addCommentToTask(taskId: String, comment: Comment) {
        db.collection("Tasks").document(taskId).collection("comments").add(
            mapOf(
                "authorId" to comment.authorId,
                "content" to comment.content,
                "date" to Timestamp(Date(comment.date.toInstant(ZoneOffset.UTC).toEpochMilli()))
            )
        ).await()
    }




















    //  Users
    private val _users = MutableStateFlow(DataBase.users)
    val users: StateFlow<List<User>> = _users

    private fun updateU(userId: String, user: User) {
        val updatedUsers = _users.value.toMutableList()
        val index = updatedUsers.indexOfFirst { it.id == userId }

        if (index != -1) {
            updatedUsers[index] = user
            _users.value = updatedUsers
        }
    }

    private fun updateKpi(userId: String, teamId: String, kpiCategory: String, value: Int = 1) {
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
            updateTask1(taskId, task.copy(categories = categories))
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

    private fun updateTask1(taskId: String, task: Task) {
        val updatedTasks = _tasks.value.toMutableList()
        val index = updatedTasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            updatedTasks[index] = task
            _tasks.value = updatedTasks
        }
    }

    fun deleteTask1(taskId: String) {
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





    fun removeAttachment(taskId: String, attachmentId: String) {
        _tasks.value.find { it.id == taskId }?.let { task ->
            val attachments = task.attachments.toMutableList()

            attachments.removeIf { it.id == attachmentId }
            updateTask1(taskId, task.copy(attachments = attachments))
        }
    }
}