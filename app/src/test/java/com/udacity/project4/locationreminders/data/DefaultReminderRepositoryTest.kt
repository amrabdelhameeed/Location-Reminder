package com.udacity.project4.locationreminders.data

import com.udacity.project4.data.source.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DefaultReminderRepositoryTest {
    private val localReminder: List<ReminderDTO> = listOf(ReminderDTO("reminder1 Title" , "reminder1 Description" , "reminder1 Location" ,
        26.10 , 26.10), ReminderDTO("reminder2 Title" , "reminder2 Description" , "reminder2 Location" ,
        26.10 , 26.10)).sortedBy { it.id }
    private lateinit var reminderLocalDS : FakeDataSource
    private lateinit var reminderRepo : DefaultReminderRepository
    @Before
    fun createRepository() {
        reminderLocalDS = FakeDataSource(localReminder.toMutableList())
        reminderRepo = DefaultReminderRepository(
            reminderLocalDS , Dispatchers.Unconfined
        )
    }
    @Test
    fun getReminders() = runBlocking {
        val allReminders = reminderRepo.getReminders() as Success
        assertThat(allReminders.data , IsEqual(localReminder))
    }
}