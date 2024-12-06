package com.example.in2out.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.in2out.db.NoteData
import com.example.in2out.viewmodel.DataViewModel
import java.time.LocalDate
import java.time.Month

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MontlyScreen(navHostController: NavHostController, viewmodel: DataViewModel) {
    val dataList by viewmodel.getDataRepo().collectAsState(initial = emptyList())
    val currentDate = LocalDate.now()
    val currentMonth = currentDate.month
    val currentYear = currentDate.year


    val currentMonthData = dataList.filter { note ->
        val noteDateStr = note.intime.removePrefix("In Time: ")
        val noteDate = LocalDate.parse(noteDateStr.substring(0, 10))
        noteDate.month == currentMonth && noteDate.year == currentYear
    }


    val pastMonthData = dataList.filter { note ->
        val noteDateStr = note.intime.removePrefix("In Time: ")
        val noteDate = LocalDate.parse(noteDateStr.substring(0, 10))
        noteDate.month != currentMonth || noteDate.year != currentYear
    }.groupBy { note ->
        val noteDateStr = note.intime.removePrefix("In Time: ")
        val noteDate = LocalDate.parse(noteDateStr.substring(0, 10))
        Pair(noteDate.month, noteDate.year)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (pastMonthData.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pastMonthData.forEach { (monthYear, notes) ->
                    item {
                        PastMonthItem(
                            monthYear = monthYear,
                            notes = notes
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (currentMonthData.isEmpty()) {
                Text(
                    text = "No notes available",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentMonthData) { note ->
                        NoteCard(note = note)
                    }
                }
            }
        }
    }
}

@Composable
fun PastMonthItem(monthYear: Pair<Month, Int>, notes: List<NoteData>) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "${monthYear.first} ${monthYear.second}",
            style = MaterialTheme.typography.labelSmall
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(notes) { note ->
                NoteCard(note = note)
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.intime,
                style = MaterialTheme.typography.titleMedium
                , fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.outime,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
