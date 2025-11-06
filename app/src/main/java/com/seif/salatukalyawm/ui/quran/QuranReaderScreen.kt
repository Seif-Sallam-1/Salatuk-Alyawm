// In: ui/quran/QuranReaderScreen.kt
package com.seif.salatukalyawm.ui.quran

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuranReaderScreen(
    startPage: Int,
    quranViewModel: QuranViewModel = viewModel()
) {
    // --- THIS IS THE FIX ---
    // The logic is now simple and direct.
    // To show page 305, we need to go to index 304.
    // The pager index is always (page number - 1).
    val initialPage = (startPage - 1).coerceIn(0, 603)
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { 604 }
    )

    Scaffold(
        bottomBar = {
            // The current page number is always the pager's index + 1.
            val currentPageNumber = pagerState.currentPage + 1
            val pageInfo = quranViewModel.getPageInfo(currentPageNumber)

            QuranPageFooter(
                pageNumber = currentPageNumber,
                surahName = pageInfo?.surahName ?: "",
                juzNumber = pageInfo?.juzNumber ?: 0
            )
        }
    ) { paddingValues ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            // --- THE SECOND PART OF THE FIX ---
            // We set reverseLayout to TRUE. This is the standard for right-to-left reading.
            // Swiping left goes to the next page (e.g., from page 2 to 3).
            // Swiping right goes to the previous page (e.g., from page 3 to 2).
            reverseLayout = true
        ) { pageIndex ->
            // The page number is always the index + 1.
            val pageNumber = pageIndex + 1
            val imagePath = "file:///android_asset/${pageNumber}.png"

            var isLoading by remember { mutableStateOf(true) }
            var isError by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imagePath)
                        .crossfade(true)
                        .listener(
                            onStart = { isLoading = true; isError = false },
                            onSuccess = { _, _ -> isLoading = false },
                            onError = { _, result ->
                                isLoading = false
                                isError = true
                                Log.e("QuranReader", "Failed to load image: $imagePath", result.throwable)
                            }
                        )
                        .build(),
                    contentDescription = "Quran Page $pageNumber",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                if (isLoading) {
                    CircularProgressIndicator()
                }
                if (isError) {
                    Text("Error: Image not found at\n$imagePath")
                }
            }
        }
    }
}

@Composable
fun QuranPageFooter(pageNumber: Int, surahName: String, juzNumber: Int) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Juz $juzNumber", fontWeight = FontWeight.Normal)
            Text(text = surahName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Page $pageNumber", fontWeight = FontWeight.Normal)
        }
    }
}
