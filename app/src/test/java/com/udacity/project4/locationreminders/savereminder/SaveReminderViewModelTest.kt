package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.data.source.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    // run all tasks in synchronous using architecture Components
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    // initialize main coroutines for unit testing

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SaveReminderViewModel
    // using a fake repo to be inject it to ViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setupViewModel() {
        // initialize the repo with no items
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
    }
    // (assertEquals) validate when normal data entered => return true

    @Test
    fun saveReminder_test() = runBlocking {
        val reminder1 = ReminderDataItem(
            "test",
            "test Description",
            "test Location",
            29.290,
            29.320
        )
        viewModel.saveReminder(reminder1)

        TestCase.assertEquals("done saving", viewModel.showToast.getOrAwaitValue())
    }

    //(assertTrue) validate when normal data entered => return true

    @Test
    fun validateEnteredData_test_trueResult() = runBlocking {
        val reminder1 = ReminderDataItem(
            "test Title",
            "test Description",
            "test Location",
            29.290,
            29.320
        )
        val result = viewModel.validateEnteredData(reminder1)
        TestCase.assertTrue(result)
    }


    // validate when title is null = > return false
    @Test
    fun validateDataEntered_test_emptyTitle_falseResult() = runBlocking {
        val reminder1 = ReminderDataItem(
            null ,
            "test Description",
            "test Location",
            29.290,
            29.320
        )
        val result = viewModel.validateEnteredData(reminder1)

        assertFalse(result)
    }

    // validate when normal data entered => return true
    @Test
    fun validateWithSaveReminderItem_test_trueResult() = runBlocking {
        val reminder1 = ReminderDataItem(
            "test Title",
            "test Description",
            "test Location",
            29.290,
            29.320
        )
        val result = viewModel.validateAndSaveReminder(reminder1)

        TestCase.assertTrue(result)
    }

    // validate with title = null
    @Test
    fun validateWithSaveReminderItem_test_falseResult() = runBlocking {
        val reminder1 = ReminderDataItem(
            null ,
            "test Description",
            "test Location",
            29.290,
            29.320
        )
        val result = viewModel.validateAndSaveReminder(reminder1)

        assertFalse(result)
    }

    // validate when title,location is empty
    @Test
    fun checkLoading() {
        mainCoroutineRule.pauseDispatcher()
        val reminder1 = ReminderDataItem(
            "",
            "test Description",
            "",
            29.290,
            29.320
        )
        viewModel.saveReminder(reminder1)
        TestCase.assertTrue(viewModel.showLoading.getOrAwaitValue())
    }

    @After
    fun teardown() {
        stopKoin()
    }

}