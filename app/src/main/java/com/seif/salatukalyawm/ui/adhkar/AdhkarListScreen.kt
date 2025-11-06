// In: AdhkarListScreen.kt
package com.seif.salatukalyawm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seif.salatukalyawm.ui.adhkar.AdhkarViewModel
@Composable
fun AdhkarListScreen(
    categoryName: String,
    adhkarViewModel: AdhkarViewModel = viewModel()
) {
    val adhkarList by adhkarViewModel.getAdhkarByCategory(categoryName).collectAsState(initial = emptyList())

    if (adhkarList.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(adhkarList, key = { it.id }) { dhikr ->
                DhikrItem(dhikr = dhikr)
            }
        }
    }
    // يمكنك إضافة مؤشر تحميل هنا إذا أردت
}
