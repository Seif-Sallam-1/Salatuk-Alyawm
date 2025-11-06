// In: TasbihScreen.kt
package com.seif.salatukalyawm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TasbihScreen() {
    var count by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.title_tasbih),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "$count",
            fontSize = 80.sp,
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { count++ }, modifier = Modifier.size(150.dp)) {
                Text("+", fontSize = 40.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { count = 0 }) {
            Text(stringResource(id = R.string.btn_reset)) // <-- أضف "btn_reset" إلى strings.xml
        }
    }
}
