package com.udacity.project4.locationreminders.reminderslist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.core.location.LocationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.BuildConfig
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import com.udacity.project4.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var bin: FragmentRemindersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bin =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminders, container, false
            )
        bin.viewModel = _viewModel
        setHasOptionsMenu(true)
        bin.refreshLayout.setOnRefreshListener {
            _viewModel.loadReminders()
            bin.refreshLayout.isRefreshing = false
        }
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))
        return bin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bin.lifecycleOwner = this
        bin.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        _viewModel.loadReminders()
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }

        bin.reminderssRecyclerView.setup(adapter)
    }
    private fun navigateToAddReminder() {
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                activity?.finish()
                val intent = Intent(activity, AuthenticationActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)

    }

}
