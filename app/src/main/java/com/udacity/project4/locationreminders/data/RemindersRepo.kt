package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

interface RemindersRepo {

    suspend fun getReminders(): Result<List<ReminderDTO>>
    suspend fun getReminder(id: String): Result<ReminderDTO>
    suspend fun saveReminder(reminder: ReminderDTO)
    suspend fun deleteAllReminders()

}