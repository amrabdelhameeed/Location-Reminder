package com.udacity.project4.data.source

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.data.dto.Result.Error

// it's a fake data source to act as the real data source
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf())
    : ReminderDataSource {

    private var shouldReturnError = false


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (!shouldReturnError){
            return Success(ArrayList(reminders as ArrayList<ReminderDTO>))
        }
        return Error("Error test")
    }

    fun setErrorForReturn (value: Boolean) {
        shouldReturnError = value == true
    }
    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        if(shouldReturnError){
            return Error("Error test")
        }

        val reminder = reminders?.find { it.id == id }
        return if(reminder != null){
            Success(reminder)

        }else {
            Error("Error test")

        }
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}

