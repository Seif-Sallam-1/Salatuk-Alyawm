// In: ui/quran/SurahIndexScreen.kt
package com.seif.salatukalyawm.ui.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seif.salatukalyawm.data.SurahText

@Composable
fun SurahIndexScreen(
    quranViewModel: QuranViewModel = viewModel(),
    onSurahClick: (startPage: Int) -> Unit
) {
    val uiState by quranViewModel.uiState.collectAsState()

    when (val currentState = uiState) {
        is QuranIndexUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is QuranIndexUiState.Success -> {
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(currentState.surahs, key = { it.number }) { surah ->
                    SurahIndexItem(
                        surah = surah,
                        onClick = { onSurahClick(surah.startPage) }
                    )
                }
            }
        }
        is QuranIndexUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "An error occurred:\n${currentState.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun SurahIndexItem(surah: SurahText, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${surah.number}",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(40.dp)
                )
                Column {
                    Text(text = surah.name.ar, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = "Number of verses: ${surah.verses.size}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(text = "Page ${surah.startPage}", fontSize = 16.sp)
        }
    }
}
