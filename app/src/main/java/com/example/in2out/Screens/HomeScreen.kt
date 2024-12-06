package com.example.in2out.Screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import com.example.in2out.Screen
import com.example.in2out.viewmodel.DataStoreHelper
import com.example.in2out.viewmodel.DataViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navControl: NavHostController,
    viewModel: DataViewModel,
    context: Context,
    dataStore: DataStore<Preferences>
) {
    val coroutineScope = rememberCoroutineScope()
    val inTime = remember { mutableStateOf("") }
    val outTime = remember { mutableStateOf("") }
    val visibility = remember { mutableStateOf(false) }
    val isButtonEnabled = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val lastClickTime = DataStoreHelper.getLastClickTime(dataStore)
            val savedInTime = DataStoreHelper.getInTime(dataStore)
            val savedOutTime = DataStoreHelper.getOutTime(dataStore)

            val currentTime = System.currentTimeMillis()
            val timeDifference = currentTime - lastClickTime

            isButtonEnabled.value = timeDifference >= 30_600_000
            visibility.value = timeDifference < 30_600_000
            inTime.value = savedInTime
            outTime.value = savedOutTime
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "In & Out Tracker",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    GradientButton(
                        text = "Monthly Data",
                        icon = Icons.Default.Notifications,
                        onClick = { navControl.navigate(Screen.MonthlyScreen.value) }
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val currentTime = System.currentTimeMillis()
                                val calendar = Calendar.getInstance()
                                val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                                val formattedInTime = "In Time: ${dateFormatter.format(calendar.time)}"
                                calendar.add(Calendar.MILLISECOND, 30_600_000)
                                val formattedOutTime = "Out Time: ${dateFormatter.format(calendar.time)}"

                                viewModel.savedata(formattedInTime, formattedOutTime, context)
                                DataStoreHelper.saveData(
                                    dataStore,
                                    formattedInTime,
                                    formattedOutTime,
                                    currentTime,
                                    false
                                )

                                inTime.value = formattedInTime
                                outTime.value = formattedOutTime
                                visibility.value = true
                                isButtonEnabled.value = false
                            }
                        },
                        enabled = isButtonEnabled.value,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isButtonEnabled.value) "Add In" else "Wait for SomeTime",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    GradientButton(
                        text = "Stop Alarm",
                        icon = Icons.Default.ThumbUp,
                        onClick = { viewModel.StopAlarm(context) },
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                    )

                    if (visibility.value) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = inTime.value,fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = outTime.value,
                                fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    }

                }
            }
        }
    )
}


@Composable
fun GradientButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(50.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

