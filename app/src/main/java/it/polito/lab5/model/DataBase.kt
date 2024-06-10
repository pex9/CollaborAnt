package it.polito.lab5.model

import it.polito.lab5.ui.theme.CollaborantColors
import java.time.LocalDate
import java.time.LocalDateTime

object DataBase {
    val default_categories = listOf("Recently assigned", "To do today", "To do next week")
    val users = listOf(
        User(
            1,
            "John",
            "Doe",
            "john.doe",
            "john@example.com",
            "123456789",
            "New York",
            "I’m a junior Computer Engineer specialized in the AI field. I love coding in C++, Java and Python. I speek English and Italian as well.",
            Empty(CollaborantColors.Yellow),
            8,
            listOf(
                100 to KPI(10, 5, 100),
                101 to KPI(50, 49, 2000),
                102 to KPI(5, 2, 20),
            ),
            // lista che estende default_categories
            categories = mutableListOf<String>().apply { addAll(default_categories) }
        ),
        User(
            2,
            "Jane",
            "Smith",
            "jane.smith",
            "jane@example.com",
            "987654321",
            "Los Angeles",
            "Team member",
            Empty(
                CollaborantColors.LightBlue
            ),
            3,
            listOf(
                100 to KPI(10, 20, 200),
                101 to KPI(200, 100, 3500),
                102 to KPI(900, 800, 9000),
            ),
            categories = mutableListOf<String>().apply { addAll(default_categories) }
        ),
        User(
            3,
            "Alice",
            "Johnson",
            "alice.johnson",
            "alice@example.com",
            "456123789",
            "Chicago",
            "Team member",
            Empty(
                CollaborantColors.MediumBlue
            ),
            3,
            listOf(
                100 to KPI(10, 7, 150),
                101 to KPI(300, 200, 5800),
                102 to KPI(390, 10, 300),
            ),
            categories = mutableListOf<String>().apply { addAll(default_categories) }
        ),
        User(
            id = 4,
            first = "John",
            last = "Doe",
            nickname = "johndoe",
            email = "john@example.com",
            telephone = "123456789",
            location = "New York",
            description = "Lorem ipsum dolor sit amet",
            imageProfile = Empty(
                CollaborantColors.LightBlue
            ),
            3,
            listOf(
                100 to KPI(30, 28, 1200),
                101 to KPI(0, 0, 0),
                102 to KPI(40, 40, 3000),
            ),
            categories = mutableListOf<String>().apply { addAll(default_categories) }
        ),

        User(
            id = 5,
            first = "Alice",
            last = "Smith",
            nickname = "alicesmith",
            email = "alice@example.com",
            telephone = "987654321",
            location = "Los Angeles",
            description = "Consectetur adipiscing elit",
            imageProfile = Empty(
                CollaborantColors.BorderGray
            ),
            3,
            listOf(
                100 to KPI(102, 98, 4000),
                101 to KPI(20, 3, 200),
                102 to KPI(0, 0, 0),
            ),
            categories = mutableListOf<String>().apply { addAll(default_categories) }
        ),

        User(
            id = 6,
            first = "Bob",
            last = "Johnson",
            nickname = "bobjohnson",
            email = "bob@example.com",
            telephone = "555555555",
            location = "Chicago",
            description = "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua",
            imageProfile = Empty(
                CollaborantColors.Yellow
            ),
            3,
            listOf(
                100 to KPI(23, 10, 600),
                101 to KPI(12, 0, 0),
                102 to KPI(1, 1, 20),
            ),
            categories = mutableListOf<String>().apply { addAll(default_categories) }
        )
    )

    const val LOGGED_IN_USER_ID = 1

    private val teamMembers = listOf(
        Pair(users[0].id, Role.TEAM_MANAGER),
        Pair(users[1].id, Role.JUNIOR_MEMBER),
        Pair(users[2].id, Role.JUNIOR_MEMBER),
        Pair(users[3].id, Role.SENIOR_MEMBER),
        Pair(users[4].id, Role.JUNIOR_MEMBER),
        Pair(users[5].id, Role.JUNIOR_MEMBER)
    )

    private val chats = listOf(
        // Day 1, August 1st:

        Message(
            senderId = 1,
            receiverId = null,
            date = LocalDateTime.now().minusDays(30),
            content = "Helloo"
        ),

        Message(
            senderId = 2,
            receiverId = 1,
            date = LocalDateTime.now().minusDays(29), // 30 minutes later
            content = "Hi everyone! I'm excited to be here."
        ),

        Message(
            senderId = 3,
            receiverId = null,
            date = LocalDateTime.now().minusDays(28), // 1 hour later
            content = "Me too! I've been looking forward to discussing this theme."
        ),

        // Day 5, August 5th:

        Message(
            senderId = 4,
            receiverId = 2,
            date = LocalDateTime.now().minusDays(26), // 4 days later
            content = "Has anyone read the latest article about this theme?"
        ),

        Message(
            senderId = 5,
            receiverId = null,
            date = LocalDateTime.now().minusDays(24), // 4 days and 30 minutes later
            content = "Yes, I did! It was really insightful."
        ),

        Message(
            senderId = 6,
            receiverId = 3,
            date = LocalDateTime.now().minusDays(23), // 4 days and 1 hour later
            content = "I agree. It brought up some interesting points."
        ),

        // Day 10, September 10th:

        Message(
            senderId = 1,
            receiverId = null,
            date = LocalDateTime.now().minusDays(22), // 9 days later
            content = "Hey everyone, I wanted to share a new resource I found on this theme."
        ),

        Message(
            senderId = 1,
            receiverId = null,
            date = LocalDateTime.now().minusDays(21), // 9 days and 30 minutes later
            content = "Thanks for sharing! I'll check it out."
        ),

        Message(
            senderId = 2,
            receiverId = null,
            date = LocalDateTime.now().minusDays(20), // 9 days and 1 hour later
            content = "Me too! This theme is so interesting, I'm always eager to learn more."
        ),

        // Day 15, October 15th:

        Message(
            senderId = 3,
            receiverId = null,
            date = LocalDateTime.now().minusDays(19), // 14 days later
            content = "I'm hosting a virtual event on this theme next week. Would anyone be interested in attending?"
        ),

        Message(
            senderId = 4,
            receiverId = null,
            date = LocalDateTime.now().minusDays(19).plusMinutes(30), // 14 days and 30 minutes later
            content = "That sounds great! I'd love to attend."
        ),

        Message(
            senderId = 5,
            receiverId = null,
            date = LocalDateTime.now().minusDays(19).plusHours(1), // 14 days and 1 hour later
            content = "Me too! Please share the details when you have them."
        )
    )

    val teams = listOf(
        Team(
            id = 100,
            name = "Mobile Application Development",
            description = "Description of Team A",
            image = Empty(CollaborantColors.LightBlue),
            members = teamMembers,
            chat = listOf(chats[0], chats[1], chats[2], chats[3], chats[4], chats[5], chats[6], chats[7], chats[8], chats[9], chats[10], chats[11])
        ),
        Team(
            id = 101,
            name = "Team B",
            description = "Description of Team B",
            image = Empty(CollaborantColors.Yellow40),
            members = listOf(
                Pair(users[1].id, Role.TEAM_MANAGER),
                Pair(users[2].id, Role.JUNIOR_MEMBER),
                Pair(users[3].id, Role.SENIOR_MEMBER),
                Pair(users[4].id, Role.JUNIOR_MEMBER),
                Pair(users[0].id, Role.SENIOR_MEMBER),
                Pair(users[5].id, Role.JUNIOR_MEMBER)
            ),
            chat = listOf(chats[3], chats[4], chats[5])
        ),
        Team(
            id = 102,
            name = "Team C",
            description = "Description of Team C",
            image = Empty(CollaborantColors.MediumBlue40),
            members = listOf(
                Pair(users[1].id, Role.JUNIOR_MEMBER),
                Pair(users[2].id, Role.JUNIOR_MEMBER),
                Pair(users[3].id, Role.SENIOR_MEMBER),
                Pair(users[4].id, Role.JUNIOR_MEMBER),
                Pair(users[5].id, Role.JUNIOR_MEMBER),
                Pair(users[0].id, Role.TEAM_MANAGER)
            ),
            chat = listOf(chats[6], chats[7], chats[8], chats[9])
        )
    )

    val tasks = listOf(
        Task(
            id = 0,
            title = "Task 1",
            description = "Description of Task 1",
            teamId = 100,
            dueDate = LocalDate.of(2024, 5, 20),
            repeat = Repeat.DAILY,
            tag = Tag.MEDIUM,
            teamMembers = teamMembers.filter { it.first > 2 }.map { it.first },
            state = TaskState.PENDING,
            comments = listOf(
                Comment("Questo è il primo commento", 1, LocalDateTime.of(2024, 5, 11, 10, 23, 30, 0)),
                Comment("Secondo commento molto interessante", 2, LocalDateTime.of(2024, 5, 6, 20, 50, 51, 0)),
                Comment("Un altro commento per completare la lista", 3, LocalDateTime.of(2024, 5, 11, 6, 15, 10, 0))
            ),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 0,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 1),
                    description = "Task created"
                ),
                Action(
                    id = 1,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 2),
                    description = "Task delegated"
                )
            )
        ),
        Task(
            id = 1,
            title = "Task 2",
            description = "Description of Task 2",
            teamId = 101,
            dueDate = LocalDate.now(),
            repeat = Repeat.NEVER,
            tag = Tag.LOW,
            teamMembers = teamMembers.filter { it.first % 2 != 0 }.map { it.first },
            state = TaskState.PENDING,
            comments = emptyList(),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 2,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 3),
                    description = "Task created"
                ),
                Action(
                    id = 3,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 3),
                    description = "Task delegated"
                )
            )
        ),

        Task(
            id = 2,
            title = "Task 3",
            description = "Description of Task 3",
            teamId = 102,
            dueDate = LocalDate.now(),
            repeat = Repeat.NEVER,
            tag = Tag.UNDEFINED,
            teamMembers = emptyList(),
            state = TaskState.NOT_ASSIGNED,
            comments = emptyList(),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 4,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 3),
                    description = "Task created"
                ),
            )
        ),
        Task(
            id = 3,
            title = "Task 4",
            description = "Description of Task 4",
            teamId = 100,
            dueDate = null,
            repeat = Repeat.NEVER,
            tag = Tag.UNDEFINED,
            teamMembers = emptyList(),
            state = TaskState.NOT_ASSIGNED,
            comments = emptyList(),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 5,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task created"
                ),
            )
        ),
        Task(
            id = 4,
            title = "Task 5",
            description = "Description of Task 5",
            teamId = 100,
            dueDate = LocalDate.of(2024, 5, 11),
            repeat = Repeat.NEVER,
            tag = Tag.LOW,
            teamMembers = emptyList(),
            state = TaskState.NOT_ASSIGNED,
            comments = emptyList(),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 6,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task created"
                ),
            )
        ),

        Task(
            id = 5,
            title = "Task 6",
            description = "Description of Task 6",
            teamId = 101,
            dueDate = LocalDate.of(2024, 5, 25),
            repeat = Repeat.WEEKLY,
            tag = Tag.UNDEFINED,
            teamMembers = listOf(2, 3),
            state = TaskState.IN_PROGRESS,
            comments = listOf(
                Comment("First comment on Task 3", 4, LocalDateTime.of(2024, 5, 13, 8, 30, 15, 0)),
                Comment("Another comment for Task 3", 5, LocalDateTime.of(2024, 5, 14, 12, 45, 30, 0))
            ),
            categories = mapOf(
                1 to "Recently assigned"
            ),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 7,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task created"
                ),
                Action(
                    id = 8,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task delegated"
                ),
                Action(
                    id = 9,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.IN_PROGRESS,
                    date = LocalDate.of(2024, 5, 8),
                    description = "Task status changed"
                )
            )
        ),
        Task(
            id = 6,
            title = "Task 7",
            description = "Description of Task 7",
            teamId = 101,
            dueDate = LocalDate.of(2024, 5, 25),
            repeat = Repeat.WEEKLY,
            tag = Tag.MEDIUM,
            teamMembers = listOf(1, 2, 3),
            state = TaskState.IN_PROGRESS,
            comments = listOf(
                Comment("First comment on Task 3", 4, LocalDateTime.of(2024, 5, 13, 8, 30, 15, 0)),
                Comment("Another comment for Task 3", 5, LocalDateTime.of(2024, 5, 14, 12, 45, 30, 0))
            ),
            categories = mapOf(
                1 to "Recently assigned"
            ),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 10,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task created"
                ),
                Action(
                    id = 11,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task delegated"
                ),
                Action(
                    id = 12,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.IN_PROGRESS,
                    date = LocalDate.of(2024, 5, 8),
                    description = "Task status changed"
                )
            )
        ),
        Task(
            id = 7,
            title = "Task 8",
            description = "Description of Task 8",
            teamId = 102,
            dueDate = LocalDate.of(2024, 5, 19),
            repeat = Repeat.NEVER,
            tag = Tag.MEDIUM,
            teamMembers = listOf(1, 3, 4),
            state = TaskState.ON_HOLD,
            comments = emptyList(),
            categories = mapOf(
                1 to "To do today"
            ),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 13,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 10),
                    description = "Task assigned"
                ),
                Action(
                    id = 14,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 11),
                    description = "Task delegated"
                ),
                Action(
                    id = 15,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.IN_PROGRESS,
                    date = LocalDate.of(2024, 5, 12),
                    description = "Task status changed"
                ),
                Action(
                    id = 16,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.ON_HOLD,
                    date = LocalDate.of(2024, 5, 14),
                    description = "Task status changed"
                )
            )
        ),
        Task(
            id = 8,
            title = "Task 9",
            description = "Description of Task 9",
            teamId = 102,
            dueDate = LocalDate.of(2024, 5, 20),
            repeat = Repeat.NEVER,
            tag = Tag.HIGH,
            teamMembers = listOf(1, 3, 4),
            state = TaskState.ON_HOLD,
            comments = emptyList(),
            categories = mapOf(
                1 to "To do today"
            ),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 17,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 10),
                    description = "Task assigned"
                ),
                Action(
                    id = 18,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 11),
                    description = "Task delegated"
                ),
                Action(
                    id = 19,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.IN_PROGRESS,
                    date = LocalDate.of(2024, 5, 12),
                    description = "Task status changed"
                ),
                Action(
                    id = 20,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.ON_HOLD,
                    date = LocalDate.of(2024, 5, 14),
                    description = "Task status changed"
                )
            )
        ),
        Task(
            id = 9,
            title = "Task 10",
            description = "Description of Task 10",
            teamId = 100,
            dueDate = LocalDate.of(2024, 5, 22),
            repeat = Repeat.NEVER,
            tag = Tag.LOW,
            teamMembers = teamMembers.filter { it.first % 2 != 0 }.map { it.first },
            state = TaskState.COMPLETED,
            comments = emptyList(),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 21,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 17),
                    description = "Task created"
                ),
                Action(
                    id = 22,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 18),
                    description = "Task delegated"
                ),
                Action(
                    id = 23,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.IN_PROGRESS,
                    date = LocalDate.of(2024, 5, 19),
                    description = "Task status changed"
                ),
                Action(
                    id = 24,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.COMPLETED,
                    date = LocalDate.of(2024, 5, 21),
                    description = "Task completed"
                )
            )
        ),
        Task(
            id = 10,
            title = "Task 11",
            description = "Description of Task 11",
            teamId = 101,
            dueDate = LocalDate.of(2024, 5, 26),
            repeat = Repeat.NEVER,
            tag = Tag.HIGH,
            teamMembers = teamMembers.filter { it.first % 2 != 0 }.map { it.first },
            state = TaskState.OVERDUE,
            comments = emptyList(),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 25,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 17),
                    description = "Task created"
                ),
                Action(
                    id = 26,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 18),
                    description = "Task delegated"
                ),
                Action(
                    id = 27,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.IN_PROGRESS,
                    date = LocalDate.of(2024, 5, 19),
                    description = "Task status changed"
                ),
                Action(
                    id = 28,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.OVERDUE,
                    date = LocalDate.of(2024, 5, 23),
                    description = "Task status changed"
                )
            )
        ),
        Task(
            id = 11,
            title = "Task 12",
            description = "Description of Task 12",
            teamId = 102,
            dueDate = LocalDate.of(2024, 5, 22),
            repeat = Repeat.NEVER,
            tag = Tag.MEDIUM,
            teamMembers = listOf(1, 2, 5),
            state = TaskState.OVERDUE,
            comments = emptyList(),
            categories = mapOf(
                1 to "To do next week"
            ),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 29,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 17),
                    description = "Task created"
                ),
                Action(
                    id = 30,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 18),
                    description = "Task delegated"
                ),
                Action(
                    id = 31,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.IN_PROGRESS,
                    date = LocalDate.of(2024, 5, 19),
                    description = "Task status changed"
                ),
                Action(
                    id = 32,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.OVERDUE,
                    date = LocalDate.of(2024, 5, 23),
                    description = "Task status changed"
                )
            )
        ),
        Task(
            id = 12,
            title = "Task 13",
            description = "Description of Task 13",
            teamId = 101,
            dueDate = LocalDate.of(2024, 5, 25),
            repeat = Repeat.WEEKLY,
            tag = Tag.HIGH,
            teamMembers = listOf(1, 2, 3),
            state = TaskState.IN_PROGRESS,
            comments = listOf(
                Comment("First comment on Task 3", 4, LocalDateTime.of(2024, 5, 13, 8, 30, 15, 0)),
                Comment("Another comment for Task 3", 5, LocalDateTime.of(2024, 5, 14, 12, 45, 30, 0))
            ),
            categories = mapOf(
                1 to "Recently assigned"
            ),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 10,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task created"
                ),
                Action(
                    id = 11,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.PENDING,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task delegated"
                ),
                Action(
                    id = 12,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.IN_PROGRESS,
                    date = LocalDate.of(2024, 5, 8),
                    description = "Task status changed"
                )
            )
        ),
        Task(
            id = 13,
            title = "Task 14",
            description = "Description of Task 14",
            teamId = 101,
            dueDate = LocalDate.of(2024, 5, 25),
            repeat = Repeat.WEEKLY,
            tag = Tag.MEDIUM,
            teamMembers = emptyList(),
            state = TaskState.NOT_ASSIGNED,
            comments = listOf(
                Comment("First comment on Task 3", 4, LocalDateTime.of(2024, 5, 13, 8, 30, 15, 0)),
                Comment("Another comment for Task 3", 5, LocalDateTime.of(2024, 5, 14, 12, 45, 30, 0))
            ),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 13,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task created"
                ),
            )
        ),
        Task(
            id = 14,
            title = "Task 15",
            description = "Description of Task 15",
            teamId = 101,
            dueDate = LocalDate.of(2024, 5, 25),
            repeat = Repeat.WEEKLY,
            tag = Tag.MEDIUM,
            teamMembers = emptyList(),
            state = TaskState.NOT_ASSIGNED,
            comments = listOf(
                Comment("First comment on Task 3", 4, LocalDateTime.of(2024, 5, 13, 8, 30, 15, 0)),
                Comment("Another comment for Task 3", 5, LocalDateTime.of(2024, 5, 14, 12, 45, 30, 0))
            ),
            categories = emptyMap(),
            attachments = emptyList(),
            history = listOf(
                Action(
                    id = 14,
                    memberId = LOGGED_IN_USER_ID,
                    taskState = TaskState.NOT_ASSIGNED,
                    date = LocalDate.of(2024, 5, 6),
                    description = "Task created"
                ),
            )
        ),
    )

}