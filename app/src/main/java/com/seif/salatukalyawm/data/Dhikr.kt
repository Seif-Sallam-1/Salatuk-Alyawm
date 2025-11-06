// In: data/Dhikr.kt
package com.seif.salatukalyawm.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adhkar_table")
data class Dhikr(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val count: String,
    val category: String // <-- هذا كل ما نحتاجه
)