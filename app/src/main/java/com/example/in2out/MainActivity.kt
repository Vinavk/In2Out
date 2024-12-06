package com.example.in2out

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.in2out.Screens.HomeScreen
import com.example.in2out.Screens.MontlyScreen
import com.example.in2out.ui.theme.In2OutTheme
import com.example.in2out.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var notificationPermissionRequest: ActivityResultLauncher<String>

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

        setContent {
            In2OutTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: DataViewModel = hiltViewModel()
                    val context = applicationContext
                    val navControl = rememberNavController()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        when {
                            ContextCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED -> {
                            }

                            else -> {
                                notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }

                    NavHost(
                        navController = navControl,
                        startDestination = Screen.HomeScreen.value
                    ) {
                        composable(route = Screen.MonthlyScreen.value) {
                            MontlyScreen(navHostController = navControl, viewModel)
                        }

                        composable(route = Screen.HomeScreen.value) {
                            HomeScreen(navControl, viewModel, context, dataStore)
                        }
                    }
                }
            }
        }
    }


}

