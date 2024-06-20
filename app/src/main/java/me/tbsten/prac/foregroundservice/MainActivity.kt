package me.tbsten.prac.foregroundservice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import me.tbsten.prac.foregroundservice.ui.theme.ForegroundServicePracTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            ForegroundServicePracTheme {
                Scaffold {
                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val notificationPermission =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                rememberPermissionState(
                                    Manifest.permission.POST_NOTIFICATIONS,
                                )
                            } else {
                                null
                            }
                        if (notificationPermission == null || notificationPermission.status.isGranted) {
                            Button(onClick = context::startMyService) {
                                Text("Start MyService")
                            }
                        } else {
                            OutlinedButton(onClick = notificationPermission::launchPermissionRequest) {
                                Text("Request Permission")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Context.startMyService() {
    Log.d("prac-foreground-service", "start")
    val intent = Intent(this, MyService::class.java)
    ContextCompat.startForegroundService(this, intent)
}
