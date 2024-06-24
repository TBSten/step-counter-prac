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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import me.tbsten.prac.foregroundservice.ui.theme.ForegroundServicePracTheme

private val permissionsList = mutableListOf<String>()
    .apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        add(Manifest.permission.FOREGROUND_SERVICE_HEALTH)
        add(Manifest.permission.ACTIVITY_RECOGNITION)
    }.toList()

class MainActivity : ComponentActivity() {
    private val stepCounter: StateFlow<Long?> by lazy {
        stepCounterFlow
            .stateIn(
                lifecycleScope,
                SharingStarted.WhileSubscribed(),
                null,
            )
    }

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
                        val permissionsState =
                            rememberMultiplePermissionsState(
                                permissionsList,
                            )

                        Column {
                            Text("permissions: ${permissionsState.permissions.joinToString(", ") { "${it.permission}:${it.status.isGranted}" }}")
                            Text("allPermissionsGranted: ${permissionsState.allPermissionsGranted}")
                            if (permissionsState.allPermissionsGranted) {
                                val stepCount by stepCounter.collectAsState()
                                Text(
                                    text = if (stepCount != null) "$stepCount 歩" else "データがありません",
                                    style = MaterialTheme.typography.displayMedium,
                                )
                                Button(onClick = context::startMyService) {
                                    Text("Start MyService")
                                }
                            } else {
                                OutlinedButton(onClick = permissionsState::launchMultiplePermissionRequest) {
                                    Text("Request Permission")
                                }
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
