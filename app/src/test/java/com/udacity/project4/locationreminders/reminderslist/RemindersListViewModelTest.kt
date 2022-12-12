package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.data.source.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var reminderDataSource: FakeDataSource
    // run all tasks in synchronous using architecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    // initialize main coroutines for unit testing
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    // using a fake repo to be inject it to ViewModel
    @Before
    fun setupViewModel() {
        //initialize repo with no any reminders
        reminderDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(), reminderDataSource
        )
    }


    // test with fake data
    @Test
    fun loadRemindersWithFakeData() {
        val reminder1 = ReminderDTO(
            "R1 Title", "R1 Description", "R1 Location",
            30.310, 31.320
        )
        val reminder2 = ReminderDTO(
            "R2 Title", "R2 Description", "R2 Location",
            32.330, 33.340
        )
        val reminder3 = ReminderDTO(
            "R3 Title", "R3 Description", "R3 Location",
            34.350, 35.300
        )
        val localReminders: MutableList<ReminderDTO> = mutableListOf(
            reminder1, reminder2, reminder3
        )
        reminderDataSource = FakeDataSource(localReminders)
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(), reminderDataSource
        )
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue(), (not(emptyList())))
    }
    // test with no items
    @Test
    fun loadRemindersWithNoItems(){
        reminderDataSource = FakeDataSource()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue() , IsEqual(emptyList<ReminderDTO>()))
    }
    // test with null value
    @Test
    fun loadRemindersWithNullValue(){
        //show error
        reminderDataSource.setErrorForReturn(true)
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("Test exception")
        )
    }
    // test with no items 2
    @Test
    fun loadRemindersWithNoItemsII(){
        val emptyRemindersList: MutableList<ReminderDTO> = mutableListOf()
        reminderDataSource = FakeDataSource(emptyRemindersList)
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(), reminderDataSource
        )
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue() , IsEqual(true))
    }
    @After
    fun teardown() {
        stopKoin()
    }
}