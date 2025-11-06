// In: ui/theme/ForceLightMode.kt
package com.seif.salatukalyawm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable


@Composable
fun ForceLightMode(
    content: @Composable () -> Unit
) {
    // احصل على الألوان الافتراضية للتطبيق
    val currentColors = MaterialTheme.colorScheme

    // قم بإنشاء نسخة من الألوان، ولكن باستخدام لوحة الألوان الفاتحة
    val lightColors = LightColorScheme.copy(
        // احتفظ باللون الأساسي (primary) من المظهر الحالي للحفاظ على هوية التطبيق
        primary = currentColors.primary,
        onPrimary = currentColors.onPrimary,
        primaryContainer = currentColors.primaryContainer,
        onPrimaryContainer = currentColors.onPrimaryContainer,
        // يمكنك الاحتفاظ بألوان أخرى إذا أردت
    )

    // قم بتطبيق المظهر الجديد الذي يستخدم الألوان الفاتحة فقط
    MaterialTheme(
        colorScheme = lightColors,
        typography = Typography, // استخدم نفس الخطوط
        shapes = Shapes(), // استخدم نفس الأشكال
        content = content // اعرض المحتوى الذي تم تمريره
    )
}
