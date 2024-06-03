package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.Action
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Repeat
import it.polito.lab5.model.Tag
import it.polito.lab5.model.Task
import it.polito.lab5.model.TaskState
import java.time.LocalDate

class TaskFormViewModel(val teamId: Int?, private val currentTaskId: Int?, val model: MyModel): ViewModel() {
    val teams = model.teams
    val users = model.users
    val currentTask = model.tasks.value.find { it.id == currentTaskId }

    private fun addTask(task: Task): Int = model.addTask(task)

    private fun updateTask(taskId: Int, task: Task) = model.updateTask(taskId, task)

    private fun updateKpi(userId: Int, teamId: Int, kpiCategory: String, value: Int = 1) = model.updateKpi(userId, teamId, kpiCategory, value)

    fun validate(): Int {
        var id = -1

        checkTitle()
        checkDescription()
        checkDueDate()
        checkDelegateMembers()

        if(titleError.isBlank() && descriptionError.isBlank() && dueDateError.isBlank() && delegatedMembersError.isBlank()) {
            if (currentTask == null) {
                val categories: MutableMap<Int, String> = mutableMapOf()
                val history = mutableListOf(
                    Action(
                        id = 0,
                        memberId = DataBase.LOGGED_IN_USER_ID,
                        taskState = TaskState.NOT_ASSIGNED,
                        date = LocalDate.now(),
                        description = "Task created"
                    )
                )

                if(delegatedMembers.isNotEmpty()) {
                    history.add(
                        Action(
                            id = 1,
                            memberId = DataBase.LOGGED_IN_USER_ID,
                            taskState = TaskState.PENDING,
                            date = LocalDate.now(),
                            description = "Task assigned"
                        )
                    )

                    delegatedMembers.forEach { memberId ->
                        teamId?.let { updateKpi(memberId, teamId, "assignedTasks") }
                        categories[memberId] = "Recently assigned"
                    }
                }
                teamId?.let {
                    id = addTask(
                        Task(
                            id = -1,
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
                        )
                    )
                }

            } else {
                id = currentTask.id
                var taskState = currentTask.state
                val categories: MutableMap<Int, String> = currentTask.categories.toMutableMap()
                val history: MutableList<Action> = currentTask.history.toMutableList()

                if(currentTask.teamMembers.isEmpty() && delegatedMembers.isNotEmpty()) {
                    history.add(
                        Action(
                            id = currentTask.history.size,
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
                    if(!currentTask.categories.containsKey(memberId)) { categories[memberId] = "Recently assigned" }

                    //  Increase assignedTasks Kpi value for all new delegated members
                    if(!currentTask.teamMembers.contains(memberId)) {
                        updateKpi(memberId, currentTask.teamId, "assignedTasks")
                    }
                }

                //  Remove from categories all members no longer delegated for the task
                currentTask.categories.filterKeys { !delegatedMembers.contains(it) }.map { it.key }.forEach {
                    memberId -> categories.remove(memberId)
                }

                //  Decrease assignedTasks Kpi value for all members no longer delegated for the task
                currentTask.teamMembers.filter { !delegatedMembers.contains(it) }.forEach{ memberId ->
                    updateKpi(memberId, currentTask.teamId, "assignedTasks", -1)
                }

                updateTask(currentTask.id, currentTask.copy(
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

    var title by mutableStateOf(currentTask?.title ?: "")
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

    var description by mutableStateOf(currentTask?.description ?: "")
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

    var tag by mutableStateOf(currentTask?.tag ?: Tag.UNDEFINED)
        private set
    fun setTagValue(t: Tag) {
        tag = t
    }

    var dueDate: LocalDate? by mutableStateOf(currentTask?.dueDate)
        private set
    var dueDateError by mutableStateOf("")
    fun setDueDateValue(d: LocalDate) {
        dueDate = d
    }
    private fun checkDueDate() {
        dueDateError = if(dueDate == null && (repeat != Repeat.NEVER ||
            (currentTask != null && currentTask.state != TaskState.NOT_ASSIGNED) || delegatedMembers.isNotEmpty()))
                "Due date must be set"
            else ""
    }

    var delegatedMembers: MutableList<Int> = mutableStateListOf<Int>().apply {
        currentTask?.teamMembers?.let { addAll(it) }
    }
        private set
    var delegatedMembersError by mutableStateOf("")
        private set
    fun addMember(memberId: Int) {
        if(!delegatedMembers.contains(memberId)) {
            delegatedMembers.add(memberId)
        }
    }
    fun removeMember(memberId: Int) {
        delegatedMembers.removeIf { id -> id == memberId }
    }

    private fun checkDelegateMembers() {
        delegatedMembersError = if(currentTask != null && currentTask.state != TaskState.NOT_ASSIGNED && delegatedMembers.isEmpty())
            "At least one member must be delegated"
            else ""
    }

    var repeat by mutableStateOf(currentTask?.repeat ?: Repeat.NEVER)
        private set
    fun setRepeatValue(r: Repeat) {
        repeat = r
    }

    fun resetErrorMsg(all: Boolean = false) {
        if(all) {
            titleError = ""
            descriptionError = ""
        } else {
            dueDateError = ""
            delegatedMembersError = ""
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
}