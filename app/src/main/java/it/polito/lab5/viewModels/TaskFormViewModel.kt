package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.model.Action
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Repeat
import it.polito.lab5.model.Tag
import it.polito.lab5.model.Task
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.model.calculateScore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class TaskFormViewModel(val teamId: String?, private val currentTaskId: String?, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    val teams = model.teams
    private var loggedInUser : User? = null
    var currentTask: Task? = null
    var users: List<User>? = emptyList()
    var team: Team? = null

    fun getTeam(teamId: String) = model.getTeam(teamId)
    fun getUser(userId: String) = model.getUser(userId)
    fun getUserKpi(userId: String) = model.getUserKpi(userId)
    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)

    private fun updateTask(taskId: String, task: Task) = model.updateTask(taskId, task)

    private fun updateKpi(userId: String, teamId: String, kpiCategory: String, value: Int = 1) = model.updateKpi(userId, teamId, kpiCategory, value)

    suspend fun validate(): String {
        var id = ""

        checkTitle()
        checkDescription()
        checkDueDate()
        checkDelegateMembers()
        checkEndRepeatDate()

        if(titleError.isBlank() && descriptionError.isBlank() && dueDateError.isBlank() && delegatedMembersError.isBlank() && endRepeatDateError.isBlank()) {
            if (currentTask == null) {
                val categories: MutableMap<String, String> = mutableMapOf()
                val history = mutableListOf(
                    Action(
                        id = 0.toString(),
                        memberId = loggedInUser?.id ?: "",
                        taskState = TaskState.NOT_ASSIGNED,
                        date = LocalDate.now(),
                        description = "Task created"
                    )
                )

                if(delegatedMembers.isNotEmpty()) {
                    history.add(
                        Action(
                            id = "",
                            memberId = loggedInUser?.id ?: "",
                            taskState = TaskState.PENDING,
                            date = LocalDate.now(),
                            description = "Task assigned"
                        )
                    )

                    users?.filter { delegatedMembers.contains(it.id) }?.forEach { member ->
                        if(teamId != null) {
                            val kpi = member.kpiValues[teamId]
                            val updatedKpi = kpi?.copy(
                                assignedTasks = kpi.assignedTasks + 1,
                                score = calculateScore(kpi.assignedTasks + 1, kpi.completedTasks)
                            )

                            updatedKpi?.let { model.updateUserKpi(member.id, member.joinedTeams, teamId to it) }
                        }
                        categories[member.id] = "Recently assigned"
                    }
                }

                //  TODO: add case of repeat task

                teamId?.let {
                    id = model.createTask(
                        Task(
                            id = "",
                            title = title,
                            description = description,
                            teamId = teamId,
                            dueDate = dueDate,
                            repeat = repeat,
                            tag = tag,
                            teamMembers = delegatedMembers,
                            state = if(delegatedMembers.isEmpty()) TaskState.NOT_ASSIGNED else TaskState.PENDING,
                            comments = emptyList(),
                            categories = categories,
                            attachments = emptyList(),
                            history = history,
                            parentId = null,
                            endDateRepeat = endRepeatDate
                        )
                    )
                }

            } else {
                id = currentTask!!.id
                var taskState = currentTask!!.state
                val categories: MutableMap<String, String> = currentTask!!.categories.toMutableMap()
                val history: MutableList<Action> = currentTask!!.history.toMutableList()

                if(currentTask!!.teamMembers.isEmpty() && delegatedMembers.isNotEmpty()) {
                    history.add(
                        Action(
                            id = currentTask!!.history.size.toString(),
                            memberId = DataBase.LOGGED_IN_USER_ID,
                            taskState = TaskState.PENDING,
                            date = LocalDate.now(),
                            description = "Task assigned"
                        )
                    )

                    taskState = TaskState.PENDING
                }

                delegatedMembers.forEach { memberId ->
                    //  Add task to Recently assigned category for all new delegated members
                    if(!currentTask!!.categories.containsKey(memberId)) { categories[memberId] = "Recently assigned" }

                    //  Increase assignedTasks Kpi value for all new delegated members
                    if(!currentTask!!.teamMembers.contains(memberId)) {
                        updateKpi(memberId, currentTask!!.teamId, "assignedTasks")
                    }
                }

                //  Remove from categories all members no longer delegated for the task
                currentTask!!.categories.filterKeys { !delegatedMembers.contains(it) }.map { it.key }.forEach {
                    memberId -> categories.remove(memberId)
                }

                //  Decrease assignedTasks Kpi value for all members no longer delegated for the task
                currentTask!!.teamMembers.filter { !delegatedMembers.contains(it) }.forEach{ memberId ->
                    updateKpi(memberId, currentTask!!.teamId, "assignedTasks", -1)
                }

                updateTask(
                    currentTask!!.id, currentTask!!.copy(
                    title = title,
                    description = description,
                    repeat = repeat,
                    tag = tag,
                    teamMembers = delegatedMembers,
                    dueDate = dueDate,
                    state = taskState,
                    history = history,
                    categories = categories
                ))
            }
        }
        return id
    }

    var title by mutableStateOf("")
        private set
    var titleError by mutableStateOf("")
        private set
    fun setTitleValue(t: String) {
        title = t
    }
    private fun checkTitle() {
        titleError = if (title.isBlank())
            "Title cannot be blank"
        else if (title.length > 50)
            "Title must contain less than 50 characters"
        else
            ""
    }

    var description by mutableStateOf("")
        private set
    var descriptionError by mutableStateOf("")
        private set
    fun setDescriptionValue(d: String) {
        description = d
    }
    private fun checkDescription() {
        descriptionError = if (description.length > 250)
            "Description must contain less than 250 characters"
        else
            ""
    }

    var tag by mutableStateOf(Tag.UNDEFINED)
        private set
    fun setTagValue(t: Tag) {
        tag = t
    }

    var dueDate: LocalDate? by mutableStateOf(null)
        private set
    var dueDateError by mutableStateOf("")
    fun setDueDateValue(d: LocalDate) {
        dueDate = d
    }
    private fun checkDueDate() {
        dueDateError = if(dueDate == null && (repeat != Repeat.NEVER ||
            (currentTask != null && currentTask!!.state != TaskState.NOT_ASSIGNED) || delegatedMembers.isNotEmpty()))
                "Due date must be set"
            else ""
    }

    var endRepeatDate: LocalDate? by mutableStateOf(null)
        private set
    var endRepeatDateError by mutableStateOf("")
    fun setEndRepeatDateValue(d: LocalDate?) {
        endRepeatDate = d
    }
    private fun checkEndRepeatDate() {
        if(repeat != Repeat.NEVER && endRepeatDate == null){
            endRepeatDateError = "End repeat date must be set"
        }
    }

    var delegatedMembers: MutableList<String> = mutableStateListOf()
        private set
    var delegatedMembersError by mutableStateOf("")
        private set
    fun addMember(memberId: String) {
        if(!delegatedMembers.contains(memberId)) {
            delegatedMembers.add(memberId)
        }
    }
    fun removeMember(memberId: String) {
        delegatedMembers.removeIf { id -> id == memberId }
    }

    private fun checkDelegateMembers() {
        delegatedMembersError = if(currentTask != null && currentTask!!.state != TaskState.NOT_ASSIGNED && delegatedMembers.isEmpty())
            "At least one member must be delegated"
            else ""
    }

    var repeat by mutableStateOf(Repeat.NEVER)
        private set
    fun setRepeatValue(r: Repeat) {
        repeat = r
        if(r != Repeat.NEVER){
            setShowEndRepeatFieldValue(true)
        }
        else{
            setEndRepeatDateValue(null)
            setShowEndRepeatFieldValue(false)
        }
    }
    var showEndRepeatField by mutableStateOf(false)
        private set
    fun setShowEndRepeatFieldValue(b: Boolean) {
        showEndRepeatField = b
    }


    fun resetErrorMsg(all: Boolean = false) {
        if(all) {
            titleError = ""
            descriptionError = ""
        } else {
            dueDateError = ""
            delegatedMembersError = ""
            endRepeatDateError = ""
        }
    }

    var showTagMenu by mutableStateOf(false)
        private set
    fun setShowTagMenuValue(b: Boolean) {
        showTagMenu = b
    }

    var showDueDateDialog by mutableStateOf(false)
        private set
    fun setShowDueDateDialogValue(b: Boolean) {
        showDueDateDialog = b
    }

    var showEndRepeatDateDialog by mutableStateOf(false)
        private set
    fun setShowEndRepeatDateDialogValue(b: Boolean) {
        showEndRepeatDateDialog = b
    }

    var showRepeatMenu by mutableStateOf(false)
        private set
    fun setShowRepeatMenuValue(b: Boolean) {
        showRepeatMenu = b
    }

    var showMemberBottomSheet by mutableStateOf(false)
        private set
    fun setShowMemberBottomSheetValue(b: Boolean) {
        showMemberBottomSheet = b
    }

    var triState: ToggleableState by mutableStateOf(ToggleableState.Indeterminate)
        private set
    init {
        val team = teams.value.find { it.id == (currentTask?.teamId ?: teamId) }

        triState = team?.let {
            when (delegatedMembers.size) {
                0 -> ToggleableState.Off
                in 1 until it.members.size -> ToggleableState.Indeterminate
                else -> ToggleableState.On
            }
        } ?: ToggleableState.Indeterminate
    }

    fun setTriStateValue(t: ToggleableState) {
        triState = t
    }

    fun toggleTriState() {
        triState = when(triState) {
            ToggleableState.On -> ToggleableState.Off
            ToggleableState.Off -> ToggleableState.On
            ToggleableState.Indeterminate -> ToggleableState.On
        }
    }

    init {
        val userid = auth.getSignedInUserId()

        viewModelScope.launch {
            loggedInUser = userid?.let { model.getUser(it).first() }
            currentTask = currentTaskId?.let { model.getTask(it).first() }
            team = (currentTask?.teamId ?: teamId)?.let { model.getTeam(it).first() }
            users = team?.members?.keys?.toList()?.let { getUsersTeam(it).first() }?.map {
                val kpi = model.getUserKpi(it.id).first()
                it.copy(kpiValues = kpi.toMap())
            }
            title = currentTask?.title ?: ""
            description = currentTask?.description ?: ""
            dueDate = currentTask?.dueDate
            repeat = currentTask?.repeat ?: Repeat.NEVER
            endRepeatDate = currentTask?.endDateRepeat
            tag = currentTask?.tag ?: Tag.UNDEFINED
            currentTask?.let { delegatedMembers.addAll(it.teamMembers) }
        }
    }
}