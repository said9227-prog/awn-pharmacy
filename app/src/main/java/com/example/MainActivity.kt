package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.ui.AonPharmaMainApp
import com.example.ui.AonPharmaViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val viewModel: AonPharmaViewModel by viewModels()

    // Activity launcher for runtime permissions (SMS and post notifications)
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] ?: false
        val notifGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
        
        Log.d(TAG, "Permissions callback details - SMS: $smsGranted, NOTIF: $notifGranted")
        viewModel.setSmsPermissionGranted(smsGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge to edge bleed setup
        enableEdgeToEdge()

        // Handle navigation tab selection if launched from notification
        handleIntentExtras(intent)

        setContent {
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                AonPharmaMainApp(viewModel = viewModel)
            }
        }

        // Trigger safe permissions request
        checkAndRequestPermissions()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntentExtras(intent)
    }

    private fun handleIntentExtras(intent: Intent?) {
        if (intent == null) return
        val openTab = intent.getStringExtra("open_tab")
        if (openTab == "competition") {
            Log.d(TAG, "Launched from connectivity notification. Navigating user to competition view.")
            // Since dashboard tab navigation handles inside of MainDashboardScreen by checking internal states,
            // we can tell our viewModel to pre-configure defaults if necessary or just transition current screens.
            // In our simple case, it routes safely to main dashboard where tab can be used.
            viewModel.navigateTo("main")
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Check SEND_SMS Permission
        val smsCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        val smsGranted = smsCheck == PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "SMS Permission checked status: $smsGranted")
        viewModel.setSmsPermissionGranted(smsGranted)

        if (!smsGranted) {
            permissionsToRequest.add(Manifest.permission.SEND_SMS)
        }

        // Check POST_NOTIFICATIONS Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notifCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (notifCheck != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            Log.d(TAG, "Requesting permissions list: $permissionsToRequest")
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
