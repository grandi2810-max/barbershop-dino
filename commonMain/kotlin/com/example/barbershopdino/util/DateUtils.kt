package com.example.barbershopdino.util

import com.example.barbershopdino.getCurrentDateIso
import com.example.barbershopdino.model.AppLanguage

object DateUtils {

    fun getDeviceCurrentDateIso(): String {
        return getCurrentDateIso()
    }

    fun getMonthYearHeader(dateIso: String, language: AppLanguage): String {
        val parts = dateIso.split("-")
        if (parts.size < 2) return if (language == AppLanguage.IT) "Maggio 2025" else "May 2025"
        val year = parts[0]
        val monthNum = parts[1].toIntOrNull() ?: 5

        val monthName = when (monthNum) {
            1 -> if (language == AppLanguage.IT) "Gennaio" else "January"
            2 -> if (language == AppLanguage.IT) "Febbraio" else "February"
            3 -> if (language == AppLanguage.IT) "Marzo" else "March"
            4 -> if (language == AppLanguage.IT) "Aprile" else "April"
            5 -> if (language == AppLanguage.IT) "Maggio" else "May"
            6 -> if (language == AppLanguage.IT) "Giugno" else "June"
            7 -> if (language == AppLanguage.IT) "Luglio" else "July"
            8 -> if (language == AppLanguage.IT) "Agosto" else "August"
            9 -> if (language == AppLanguage.IT) "Settembre" else "September"
            10 -> if (language == AppLanguage.IT) "Ottobre" else "October"
            11 -> if (language == AppLanguage.IT) "Novembre" else "November"
            12 -> if (language == AppLanguage.IT) "Dicembre" else "December"
            else -> if (language == AppLanguage.IT) "Maggio" else "May"
        }
        return "$monthName $year"
    }

    fun isSunday(dateIso: String): Boolean {
        val parts = dateIso.split("-")
        if (parts.size < 3) return false
        val month = parts[1].toIntOrNull() ?: 5
        val day = parts[2].toIntOrNull() ?: 15

        // Sundays for May 2025: 4, 11, 18, 25; June 2025: 1, 8, 15, 22, 29; July: 6, 13, 20, 27; Aug: 3, 10, 17, 24, 31
        return when (month) {
            5 -> day in listOf(4, 11, 18, 25)
            6 -> day in listOf(1, 8, 15, 22, 29)
            7 -> day in listOf(6, 13, 20, 27)
            8 -> day in listOf(3, 10, 17, 24, 31)
            else -> false
        }
    }

    fun isSummerPeriod(dateIso: String): Boolean {
        val parts = dateIso.split("-")
        if (parts.size < 3) return false
        val month = parts[1].toIntOrNull() ?: 5
        return month in 6..8
    }

    fun isSummerOrHoliday(dateIso: String): Boolean {
        return isSunday(dateIso) || isSummerPeriod(dateIso)
    }

    fun getTimeSlotsForDate(dateIso: String): List<String> {
        if (isSunday(dateIso)) {
            // Sunday is CLOSED / Non prenotabile!
            return emptyList()
        }

        return if (isSummerPeriod(dateIso)) {
            // Periodo estivo (Lun-Sab): 14:00 - 20:00
            listOf("14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00")
        } else {
            // Giorni ordinari (Lun-Sab): 16:00 - 20:00
            listOf("16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00")
        }
    }
}
