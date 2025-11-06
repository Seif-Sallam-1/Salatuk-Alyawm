// In: ui/adhkar/AdhkarViewModel.kt
package com.seif.salatukalyawm.ui.adhkar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.seif.salatukalyawm.data.AppDatabase
import com.seif.salatukalyawm.data.Dhikr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.InputStream

class AdhkarViewModel(application: Application) : AndroidViewModel(application) {

    private val dhikrDao = AppDatabase.getDatabase(application).dhikrDao()

    // Flow لجلب كل الفئات من قاعدة البيانات
    val categories: Flow<List<String>> = dhikrDao.getAllCategories()

    init {
        populateDatabaseIfNeeded()
    }

    private fun populateDatabaseIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            if (dhikrDao.getTotalCount() == 0) {
                populateDatabaseFromAsset()
            }
        }
    }

    // دالة لجلب الأذكار لفئة معينة
    fun getAdhkarByCategory(categoryName: String): Flow<List<Dhikr>> {
        return dhikrDao.getAdhkarByCategory(categoryName)
    }

    private suspend fun populateDatabaseFromAsset() {
        val context = getApplication<Application>().applicationContext
        val jsonString: String
        try {
            val inputStream: InputStream = context.assets.open("adhkar.json")
            jsonString = inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        val adhkarToInsert = mutableListOf<Dhikr>()
        val categoriesArray = JSONArray(jsonString)

        for (i in 0 until categoriesArray.length()) {
            val categoryObject = categoriesArray.getJSONObject(i)
            val categoryName = categoryObject.getString("category")

            if (!categoryObject.has("array")) continue
            val adhkarArray = categoryObject.getJSONArray("array")

            for (j in 0 until adhkarArray.length()) {
                val dhikrObject = adhkarArray.getJSONObject(j)
                val text = dhikrObject.getString("text")
                val count = dhikrObject.optString("count", "1")

                // لا يوجد فصل ذكي، فقط نستخدم الفئة كما هي
                adhkarToInsert.add(
                    Dhikr(
                        text = text,
                        count = count.ifEmpty { "1" },
                        category = categoryName
                    )
                )
            }
        }

        if (adhkarToInsert.isNotEmpty()) {
            dhikrDao.insertAll(adhkarToInsert)
        }
    }
}
