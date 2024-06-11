package it.polito.lab5.gui.taskForm

import it.polito.lab5.model.Repeat
import java.time.LocalDate

fun getMaxEndDate(repeat: Repeat): LocalDate? {
    return when (repeat) {
        Repeat.NEVER -> null
        Repeat.DAILY -> LocalDate.now().plusDays(30)
        Repeat.WEEKLY -> LocalDate.now().plusMonths(6)
        Repeat.MONTHLY -> LocalDate.now().plusYears(1)
    }
}

fun generateDueDates(startDate: LocalDate, endDate: LocalDate, repeat: Repeat): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var currentDate = when (repeat) {
        Repeat.NEVER -> return emptyList()
        Repeat.DAILY -> startDate.plusDays(1)
        Repeat.WEEKLY -> startDate.plusWeeks(1)
        Repeat.MONTHLY -> startDate.plusMonths(1)
    }

    while (currentDate <= endDate) {
        dates.add(currentDate)

        currentDate = when (repeat) {
            Repeat.DAILY -> currentDate.plusDays(1)
            Repeat.WEEKLY -> currentDate.plusWeeks(1)
            Repeat.MONTHLY -> currentDate.plusMonths(1)
            Repeat.NEVER -> break
        }
    }
    return dates
}