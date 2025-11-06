package com.seif.salatukalyawm.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DhikrDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(adhkar: List<Dhikr>)

    @Query("SELECT COUNT(*) FROM adhkar_table")
    suspend fun getTotalCount(): Int

    // دالة جديدة: لجلب قائمة بأسماء الفئات الفريدة فقط
    @Query("SELECT DISTINCT category FROM adhkar_table")
    fun getAllCategories(): Flow<List<String>>

    // دالة جديدة: لجلب الأذكار حسب اسم الفئة المحدد
    @Query("SELECT * FROM adhkar_table WHERE category = :categoryName")
    fun getAdhkarByCategory(categoryName: String): Flow<List<Dhikr>>

    @Query("SELECT COUNT(*) FROM adhkar_table WHERE category = :category")
    suspend fun getAdhkarCountByCategory(category: String): Int

    @Query("SELECT * FROM adhkar_table WHERE category LIKE '%' || :keyword || '%'")
    fun getAdhkarByKeyword(keyword: String): Flow<List<Dhikr>>

}