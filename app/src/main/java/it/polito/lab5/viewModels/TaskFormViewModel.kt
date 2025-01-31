package it.polito.lab5.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.gui.taskForm.generateDueDates
import it.polito.lab5.model.Action
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.KPI
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Option
import it.polito.lab5.model.Repeat
import it.polito.lab5.model.Tag
import it.polito.lab5.model.Task
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.model.calculateScore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class TaskFormViewModel(val teamId: String?, private val currentTaskId: String?, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    var loggedInUser : User? = null
    var currentTask: Task? = null
    var users: List<User>? = emptyList()
    var team: Team? = null

    fun getTeam(teamId: String) = model.getTeam(teamId)
    fun getUser(userId: String) = model.getUser(userId)
    fun getUserKpi(userId: String) = model.getUserKpi(userId)
    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)

    private suspend fun createTask(task: Task) = model.createTask(task)

    private suspend fun updateTask(task: Task, option: Option) = model.updateTask(task, option)

    private suspend fun addActionToHistory(taskId: String, action: Action) = model.addActionToTaskHistory(taskId, action)

    private suspend fun updateUserKpi( userId: String, joinedTeams: Long, kpiValues: Pair<String, KPI>) = model.updateUserKpi(userId, joinedTeams, kpiValues)

    suspend fun validate(): String {
        var id = ""

        checkTitle()
        checkDescription()
        checkDueDate()
        checkDelegateMembers()
        checkEndRepeatDate()

        if(titleError.isBlank() && descriptionError.isBlank() && dueDateError.isBlank() && delegatedMembersError.isBlank() && endRepeatDateError.isBlank()) {
            try {
                viewModelScope.async {
                    showLoading = true

                    if (currentTask == null) {
                        val categories: MutableMap<String, String> = mutableMapOf()
                        val history = mutableListOf(
                            Action(
                                id = "",
                                memberId = loggedInUser?.id ?: "",
                                taskState = TaskState.NOT_ASSIGNED,
                                date = LocalDateTime.now(),
                                description = "Task created"
                            )
                        )

                        if(delegatedMembers.isNotEmpty()) {
                            history.add(
                                Action(
                                    id = "",
                                    memberId = loggedInUser?.id ?: "",
                                    taskState = TaskState.PENDING,
                                    date = LocalDateTime.now(),
                                    description = "Task assigned"
                                )
                            )

                            users?.filter { delegatedMembers.contains(it.id) }?.forEach { member ->
                                if(teamId != null && repeat == Repeat.NEVER) {
                                    val kpi = member.kpiValues[teamId]
                                    val updatedKpi = kpi?.copy(
                                        assignedTasks = kpi.assignedTasks + 1,
                                        score = calculateScore(kpi.assignedTasks + 1, kpi.completedTasks)
                                    )

                                    updatedKpi?.let { updateUserKpi(member.id, member.joinedTeams, teamId to it) }
                                }

                                categories[member.id] = "Recently Assigned"
                            }
                        }

                        val newTask = teamId?.let {Task(
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
                        )}

                        //  Create first instance of task
                        newTask?.let { id = createTask(it) }

                        if(repeat != Repeat.NEVER && newTask != null && dueDate != null && endRepeatDate != null) {
                            val dates = generateDueDates(dueDate!!, endRepeatDate!!, repeat)

                            //  Create all task's copy
                            updateTask(newTask.copy(id = id, parentId = id), Option.CURRENT)
                            dates.forEach { date -> createTask(newTask.copy(parentId = id, dueDate = date)) }

                            //  Update user kpi for all task's copy
                            users?.filter { delegatedMembers.contains(it.id) }?.forEach { member ->
                                if(teamId != null) {
                                    val kpi = member.kpiValues[teamId]
                                    val updatedKpi = kpi?.copy(
                                        assignedTasks = kpi.assignedTasks + dates.size + 1,
                                        score = calculateScore(kpi.assignedTasks + dates.size + 1, kpi.completedTasks)
                                    )

                                    updatedKpi?.let { updateUserKpi(member.id, member.joinedTeams, teamId to it) }
                                }
                            }
                        }
                    } else if(repeat != Repeat.NEVER) {
                        id = currentTask!!.id
                        var taskState = currentTask!!.state

                        if((taskState == TaskState.OVERDUE || taskState == TaskState.COMPLETED) && dueDate!! > LocalDate.now() && optionSelected == Option.CURRENT) {
                            addActionToHistory(currentTask!!.id,
                                Action(
                                    id = "",
                                    memberId = loggedInUser?.id ?: "",
                                    taskState = TaskState.PENDING,
                                    date = LocalDateTime.now(),
                                    description = "Task rescheduled"
                                )
                            )

                            if(taskState == TaskState.COMPLETED) {
                                users?.filter { delegatedMembers.contains(it.id) }?.forEach { member ->
                                    //  Decrease completedTasks Kpi value for all delegated members
                                    val kpi = member.kpiValues[currentTask!!.teamId]
                                    val updatedKpi = kpi?.copy(
                                        completedTasks = kpi.completedTasks - 1,
                                        score = calculateScore(kpi.assignedTasks, kpi.completedTasks - 1)
                                    )

                                    updatedKpi?.let { updateUserKpi(member.id, member.joinedTeams, currentTask!!.teamId to it) }
                                }
                            }

                            taskState = TaskState.PENDING
                        }

                        updateTask(
                            currentTask!!.copy(
                                title = title,
                                tag = tag,
                                description = description,
                                dueDate = dueDate,
                                state = taskState
                            ),
                            optionSelected
                        )
                    } else {
                        id = currentTask!!.id
                        var isOnCompletedReschedule = false
                        var taskState = currentTask!!.state
                        val categories: MutableMap<String, String> = currentTask!!.categories.toMutableMap()

                        if(currentTask!!.teamMembers.isEmpty() && delegatedMembers.isNotEmpty()) {
                            addActionToHistory(currentTask!!.id,
                                Action(
                                    id = "",
                                    memberId = loggedInUser?.id ?: "",
                                    taskState = TaskState.PENDING,
                                    date = LocalDateTime.now(),
                                    description = "Task assigned"
                                )
                            )

                            taskState = TaskState.PENDING
                        }

                        //  Overdue Task rescheduled
                        if((taskState == TaskState.OVERDUE || taskState == TaskState.COMPLETED) && dueDate!! > LocalDate.now()) {
                            addActionToHistory(currentTask!!.id,
                                Action(
                                    id = "",
                                    memberId = loggedInUser?.id ?: "",
                                    taskState = TaskState.PENDING,
                                    date = LocalDateTime.now(),
                                    description = "Task rescheduled"
                                )
                            )

                            if(taskState == TaskState.COMPLETED) { isOnCompletedReschedule = true }

                            taskState = TaskState.PENDING
                        }

                        users?.filter { delegatedMembers.contains(it.id) }?.forEach { member ->
                            //  Add task to Recently assigned category for all new delegated members
                            if(!currentTask!!.categories.containsKey(member.id)) { categories[member.id] = "Recently Assigned" }

                            //  Increase assignedTasks Kpi value for all new delegated members
                            if(!currentTask!!.teamMembers.contains(member.id)) {
                                val kpi = member.kpiValues[currentTask!!.teamId]
                                val updatedKpi = kpi?.copy(
                                    assignedTasks = kpi.assignedTasks + 1,
                                    score = calculateScore(kpi.assignedTasks + 1, kpi.completedTasks)
                                )

                                updatedKpi?.let { updateUserKpi(member.id, member.joinedTeams, currentTask!!.teamId to it) }
                            }
                        }


                        //  Remove from categories all members no longer delegated for the task
                        currentTask!!.categories.filterKeys { !delegatedMembers.contains(it) }.map { it.key }.forEach {
                                memberId -> categories.remove(memberId)
                        }

                        if(isOnCompletedReschedule) {
                            users?.filter { currentTask!!.teamMembers.contains(it.id) && delegatedMembers.contains(it.id)}?.forEach { member ->
                                //  Decrement completedTasks Kpi value for previous delegated members since the task has been rescheduled
                                val kpi = member.kpiValues[currentTask!!.teamId]
                                val updatedKpi = kpi?.copy(
                                    completedTasks = kpi.completedTasks - 1,
                                    score = calculateScore(kpi.assignedTasks, kpi.completedTasks - 1)
                                )

                                updatedKpi?.let { updateUserKpi(member.id, member.joinedTeams, currentTask!!.teamId to it) }
                            }
                        }

                        //  Decrease assignedTasks Kpi value for all members no longer delegated for the task
                        users?.filter { currentTask!!.teamMembers.contains(it.id) && !delegatedMembers.contains(it.id)}?.forEach { member ->
                            val kpi = member.kpiValues[currentTask!!.teamId]

                            val newCompletedKpi = if(isOnCompletedReschedule) { (kpi?.completedTasks ?: 0) - 1 }
                                else { kpi?.completedTasks ?: 0 }

                            val updatedKpi = kpi?.copy(
                                assignedTasks = kpi.assignedTasks - 1,
                                completedTasks = newCompletedKpi,
                                score = calculateScore(kpi.assignedTasks - 1, newCompletedKpi)
                            )

                            updatedKpi?.let { updateUserKpi(member.id, member.joinedTeams, currentTask!!.teamId to it) }
                        }

                        updateTask(
                            currentTask!!.copy(
                                title = title,
                                description = description,
                                repeat = repeat,
                                tag = tag,
                                teamMembers = delegatedMembers,
                                dueDate = dueDate,
                                state = taskState,
                                categories = categories,
                                endDateRepeat = endRepeatDate
                            ),
                            Option.CURRENT
                        )
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("Server Error", e.message.toString())
                showLoading = false
                return ""
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
            else if(currentTask != null && repeat != Repeat.NEVER && optionSelected != Option.CURRENT && dueDate != currentTask!!.dueDate)
                "Due date can be changed just for the current task"
            else ""
    }

    var endRepeatDate: LocalDate? by mutableStateOf(null)
        private set
    var endRepeatDateError by mutableStateOf("")
    fun setEndRepeatDateValue(d: LocalDate?) {
        endRepeatDate = d
    }
    private fun checkEndRepeatDate() {
        if (repeat != Repeat.NEVER) {
            endRepeatDateError = if (endRepeatDate == null) "End repeat date must be set"
                else if (dueDate != null && endRepeatDate!! < dueDate) "End repeat date must be after due date"
                else ""
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
            else if(delegatedMembers.isEmpty() && repeat != Repeat.NEVER) "Recurrent task must be delegated to someone"
            else ""
    }

    var repeat by mutableStateOf(Repeat.NEVER)
        private set
    fun setRepeatValue(r: Repeat) {
        repeat = r
        if(r != Repeat.NEVER){
            showEndRepeatField = true
        }
        else{
            setEndRepeatDateValue(null)
            showEndRepeatField = false
        }
    }

    var showEndRepeatField by mutableStateOf(false)
        private set

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

    var showLoading by mutableStateOf(false)
        private set

    var optionSelected by mutableStateOf(Option.CURRENT)
        private set
    fun setOptionSelectedValue(o: Option) {
        optionSelected = o
    }

    var showRepeatEditDialog by mutableStateOf(false)
        private set
    fun setShowRepeatEditDialogValue(b: Boolean) {
        showRepeatEditDialog = b
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
            showEndRepeatField = endRepeatDate != null

            triState = team?.let {
                when (delegatedMembers.size) {
                    0 -> ToggleableState.Off
                    in 1 until it.members.size -> ToggleableState.Indeterminate
                    else -> ToggleableState.On
                }
            } ?: ToggleableState.Indeterminate
        }
    }
}