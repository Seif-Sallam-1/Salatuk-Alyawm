// In: AdhkarScreen.kt
package com.seif.salatukalyawm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seif.salatukalyawm.data.Dhikr
import com.seif.salatukalyawm.ui.adhkar.AdhkarViewModel

// تم تغيير اسم الدالة الرئيسية ليعكس وظيفتها الجديدة
@Composable
fun AdhkarCategoriesScreen(
    adhkarViewModel: AdhkarViewModel = viewModel(),
    onCategoryClick: (String) -> Unit // سيتم تمرير هذه الدالة من NavigationGraph
) {
    val categories by adhkarViewModel.categories.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories, key = { it }) { categoryName ->
            CategoryItem(
                categoryName = categoryName,
                onClick = { onCategoryClick(categoryName) }
            )
        }
    }
}

@Composable
fun CategoryItem(categoryName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = categoryName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            textAlign = TextAlign.Right,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// DhikrItem لا يزال هنا لأنه قد يكون مطلوبًا في AdhkarListScreen
// من الأفضل نقله إلى ملف خاص به لاحقًا لتنظيم الكود
@Composable
fun DhikrItem(dhikr: Dhikr) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dhikr.text,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right,
                fontSize = 18.sp,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "التكرار: ${dhikr.count} مرات",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
