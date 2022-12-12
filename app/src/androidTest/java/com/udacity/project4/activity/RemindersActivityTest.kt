package com.udacity.project4.activity

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//end TO end test to => : black box test the app
class RemindersActivityTest :
// extending Koin Test => embed automatically close @after method to close Koin after every test
AutoCloseKoinTest() {
    private lateinit var reminderDataSource: ReminderDataSource
    private lateinit var appContext: Application
    /*
     * as using Koin as service locator library to develop the code
     * we will use Koin to test our code too.
     * now we will initialize Koin related code to be able to use it in out testing
     * in the code below.
     */
    @Before
    fun setup(){
        //stop the main app koin
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()

        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        // identify a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get the real repository
        reminderDataSource = get()
        //clear all data to start from the start
        runBlocking {
            reminderDataSource.deleteAllReminders()
        }
    }

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    // declare fun to register IdlingResource
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }
    // declare fun to unRegister IdlingResource
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    // get the app activity
    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>) : Activity {
        lateinit var activity: Activity
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    // test reminder without a title
    @Test
    fun saveReminderNoTitle(){

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        dataBindingIdlingResource.monitorActivity(activityScenario)

        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.saveReminder))
            .perform(ViewActions.click())

        val snackBarMessage = appContext.getString(R.string.err_enter_title)

        Espresso.onView(ViewMatchers.withText(snackBarMessage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }

    // test reminder without any selected loaction => and show a toast message
    @Test
    fun reminderSavedWithNoSelectedLocation_ToastMessage() {

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        dataBindingIdlingResource.monitorActivity(activityScenario)

        Espresso.onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.reminderTitle)).perform(ViewActions.typeText("R1 Title"))

        Espresso.closeSoftKeyboard()

        Espresso.onView(withId(R.id.saveReminder)).perform(ViewActions.click())

        val snackBarMessage = appContext.getString(R.string.err_select_location)

        Espresso.onView(ViewMatchers.withText(snackBarMessage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()


    }

    // reminder successfully saved
    @Test
    fun reminderSaved_ToastMessage(){

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        dataBindingIdlingResource.monitorActivity(activityScenario)

        Espresso.onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.selectLocation)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.map)).perform(ViewActions.longClick())

        Espresso.onView(withId(R.id.reminderTitle))
            .perform(ViewActions.replaceText("Reminder1 Title"))

        Espresso.onView(withId(R.id.reminderDescription))
            .perform(ViewActions.replaceText("Reminder1 Description"))

        Espresso.onView(withId(R.id.saveReminder)).perform(ViewActions.click())

        Espresso.onView(withText(R.string.reminder_saved))
            .inRoot(
                RootMatchers.withDecorView(
                    CoreMatchers.not(
                        CoreMatchers.`is`(
                            getActivity(
                                activityScenario
                            ).window.decorView
                        )
                    )
                )
            )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        activityScenario.close()
    }



}