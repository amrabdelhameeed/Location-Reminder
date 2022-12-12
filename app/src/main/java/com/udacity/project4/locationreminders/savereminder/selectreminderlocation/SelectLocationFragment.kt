
package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.BuildConfig
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment() {
    lateinit var map: GoogleMap
    private lateinit var binding: FragmentSelectLocationBinding
    override val _viewModel: SaveReminderViewModel by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync { mMap ->
            map = mMap
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            map.clear()
            val googlePlex = CameraPosition.builder()
                .target(LatLng(45.54515, -180.1215454))
                .zoom(11f)
                .bearing(0f)
                .tilt(44f)
                .build()
            map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 500, null)
            onlongTapMap(map)
            setPosition(map)
            setMapStyle(map)
            enableMyLocation()
        }
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
    private fun whenLocationSelected(lat: Double, lng: Double, locStr: String) {
        _viewModel.latitude.value = lat
        _viewModel.longitude.value = lng
        _viewModel.reminderSelectedLocationStr.value = locStr
        findNavController().navigate(R.id.action_selectLocationFragment_to_saveReminderFragment)
    }
    private fun onlongTapMap(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "%1$.3f, %2$.3f",
                latLng.latitude,
                latLng.longitude
            )
            whenLocationSelected(latLng.latitude , latLng.longitude , snippet )
            map.clear()
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(snippet)
            )
        }
    }

    private fun setPosition(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            whenLocationSelected(poi.latLng.latitude , poi.latLng.longitude , poi.name)
            map.clear()
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                context?.let {
                    MapStyleOptions.loadRawResourceStyle(
                        it.applicationContext,
                        R.raw.map_style
                    )
                }
            )
        } catch (e: Resources.NotFoundException) {
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (isPermissionGranted())
            if (::map.isInitialized)
                map.isMyLocationEnabled = true
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (isLocationEnabled(requireContext())){
                map.isMyLocationEnabled = true
                return
            }else {
                checkDeviceLocationSettings()
            }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        map.moveCamera(CameraUpdateFactory.zoomIn())
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.LIBRARY_PACKAGE_NAME, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            }
        }
    }

    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender, 29,
                        null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 29){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(requireContext() , "Location Enabled" , Toast.LENGTH_SHORT).show()
            }
            else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction("Ok") {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
    }
}

