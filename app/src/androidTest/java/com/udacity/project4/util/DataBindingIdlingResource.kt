package com.udacity.project4.util

import android.app.Activity
import android.app.Application
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingResource
import java.util.UUID

class DataBindingIdlingResource : IdlingResource {
    // list of :  registered callbacks
    private val idlingCallbacks = mutableListOf<IdlingResource.ResourceCallback>()
    // unique Id
    private val id = UUID.randomUUID().toString()
    // handle with if isIdle is called and the result was false. then track this to avoid calling
    // onTransitionToIdle callbacks if Espresso never
    // thought we were idle in the first place.
    private var wasNotIdle = false

    var appContext : Application = ApplicationProvider.getApplicationContext()

    lateinit var activity: FragmentActivity

    override fun getName() = "DataBinding $id"

    override fun isIdleNow(): Boolean {
        val idle = !getBindings().any { it.hasPendingBindings() }
        @Suppress("LiftReturnOrAssignment")
        if (idle) {
            if (wasNotIdle) {
                idlingCallbacks.forEach { it.onTransitionToIdle() }
            }
            wasNotIdle = false
        } else {
            wasNotIdle = true
            activity.findViewById<View>(android.R.id.content).postDelayed({
                isIdleNow
            }, 16)
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallbacks.add(callback)
    }

     // get all bindings classes to all fragments which available .
    private fun getBindings(): List<ViewDataBinding> {
        val fragments = (activity as? FragmentActivity)
            ?.supportFragmentManager
            ?.fragments

        val bindings =
            fragments?.mapNotNull {
                it.view?.getBinding()
            } ?: emptyList()
        val childrenBindings = fragments?.flatMap { it.childFragmentManager.fragments }
            ?.mapNotNull { it.view?.getBinding() } ?: emptyList()

        return bindings + childrenBindings
    }
}

private fun View.getBinding(): ViewDataBinding? = DataBindingUtil.getBinding(this)

// sets the activity from an ActivityScenario
// to be used from DataBindingIdlingResource.
fun DataBindingIdlingResource.monitorActivity(
    activityScenario: ActivityScenario<out FragmentActivity>
) {
    activityScenario.onActivity {
        this.activity = it
    }
}