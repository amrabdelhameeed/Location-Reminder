package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT)
            GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
    }
    companion object {
        const val ACTION_GEOFENCE_EVENT =
            "ACTION_GEOFENCE_EVENT"
    }

}