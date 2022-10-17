package com.example.mybeacon

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mybeacon.ui.theme.MyBeaconTheme
import com.example.mybeacon.vm.FinderViewModel
import finder

class MainActivity : ComponentActivity() {
    var neverAskAgainPermissions = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyBeaconTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    finder(FinderViewModel(this.application))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in 1..permissions.size-1) {
            Log.d(TAG, "onRequestPermissionResult for "+permissions[i]+":" +grantResults[i])
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                //check if user select "never ask again" when denying any permission
                if (!shouldShowRequestPermissionRationale(permissions[i])) {
                    neverAskAgainPermissions.add(permissions[i])
                }
            }
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        checkPermissions()
    }

    fun checkPermissions() {
        // basepermissions are for M and higher
        var permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION)
        var permissionRationale ="This app needs fine location permission to detect beacons.  Please grant this now."
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN)
            permissionRationale ="This app needs fine location permission, and bluetooth scan permission to detect beacons.  Please grant all of these now."
        }
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION)
                permissionRationale ="This app needs fine location permission to detect beacons.  Please grant this now."
            }
            else {
                permissions = arrayOf( Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                permissionRationale ="This app needs background location permission to detect beacons in the background.  Please grant this now."
            }
        }
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            permissionRationale ="This app needs both fine location permission and background location permission to detect beacons in the background.  Please grant both now."
        }
        var allGranted = true
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) allGranted = false;
        }
        if (!allGranted) {
            if (neverAskAgainPermissions.count() == 0) {
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("This app needs permissions to detect beacons")
                builder.setMessage(permissionRationale)
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    requestPermissions(
                        permissions,
                        PERMISSION_REQUEST_FINE_LOCATION
                    )
                }
                builder.show()
            }
            else {
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("Functionality limited")
                builder.setMessage("Since location and device permissions have not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location and device discovery permissions to this app.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener { }
                builder.show()
            }
        }
        else {
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        val builder =
                            AlertDialog.Builder(this)
                        builder.setTitle("This app needs background location access")
                        builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener {
                            requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                PERMISSION_REQUEST_BACKGROUND_LOCATION
                            )
                        }
                        builder.show()
                    } else {
                        val builder =
                            AlertDialog.Builder(this)
                        builder.setTitle("Functionality limited")
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener { }
                        builder.show()
                    }
                }
            }
            else if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.S &&
                (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED)) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)) {
                    val builder =
                        AlertDialog.Builder(this)
                    builder.setTitle("This app needs bluetooth scan permission")
                    builder.setMessage("Please grant scan permission so this app can detect beacons.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener {
                        requestPermissions(
                            arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                            PERMISSION_REQUEST_BLUETOOTH_SCAN
                        )
                    }
                    builder.show()
                } else {
                    val builder =
                        AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since bluetooth scan permission has not been granted, this app will not be able to discover beacons  Please go to Settings -> Applications -> Permissions and grant bluetooth scan permission to this app.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
            }
            else {
                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            val builder =
                                AlertDialog.Builder(this)
                            builder.setTitle("This app needs background location access")
                            builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                            builder.setPositiveButton(android.R.string.ok, null)
                            builder.setOnDismissListener {
                                requestPermissions(
                                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                    PERMISSION_REQUEST_BACKGROUND_LOCATION
                                )
                            }
                            builder.show()
                        } else {
                            val builder =
                                AlertDialog.Builder(this)
                            builder.setTitle("Functionality limited")
                            builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                            builder.setPositiveButton(android.R.string.ok, null)
                            builder.setOnDismissListener { }
                            builder.show()
                        }
                    }
                }
            }
        }

    }

    companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }
}
