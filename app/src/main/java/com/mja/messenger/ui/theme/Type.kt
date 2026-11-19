package com.mja.messenger.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mja.messenger.R

/** Persian typeface for the whole product; Latin glyphs come from Vazirmatn too. */
val MjaFont = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

/**
 * Type scale — fixed by the MJA design system:
 * 22 / 17 / 15 / 14 / 12 / 11
 */
val MjaTypography = Typography(
    // عنوان اصلی صفحه
    headlineSmall = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 32.sp,
    ),
    // عنوان بخش‌ها
    titleLarge = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 26.sp,
    ),
    // عنوان گفتگوها
    titleMedium = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 24.sp,
    ),
    // متن عادی / پیام
    bodyLarge = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
    ),
    // متن فرعی
    bodySmall = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),
    // اطلاعات کوچک
    labelSmall = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = MjaFont,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 40.sp,
    ),
)
