package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

class ReminderDescriptionActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }
    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        binding.reminderDataItem = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem
        binding.textViewTitle.text = binding.reminderDataItem!!.title
        binding.textViewDescription.text = binding.reminderDataItem!!.description
        binding.textViewLocation.text = binding.reminderDataItem!!.location
    }
}