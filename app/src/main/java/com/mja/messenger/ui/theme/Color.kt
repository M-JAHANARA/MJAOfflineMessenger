package com.mja.messenger.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * MJA Offline Messenger — Premium Minimalist Dark Interface.
 * Every screen must consume colors from this file only.
 */

// Surfaces
val MjaBackground = Color(0xFF090A0C)      // رنگ پس‌زمینه اصلی
val MjaBackground2 = Color(0xFF101216)     // رنگ پس‌زمینه ثانویه
val MjaSplash = Color(0xFF000000)          // پس‌زمینه اسپلش (مشکی خالص)
val MjaCard = Color(0xFF171A20)            // رنگ کارت‌ها
val MjaElevated = Color(0xFF20242B)        // رنگ سطح بالاتر
val MjaBorder = Color(0xFF2B3039)          // رنگ حاشیه

// Text
val MjaTextPrimary = Color(0xFFF4F5F7)     // رنگ متن اصلی
val MjaTextSecondary = Color(0xFFA4A9B2)   // رنگ متن ثانویه
val MjaTextDisabled = Color(0xFF686E78)    // رنگ متن غیرفعال

// Brand
val MjaBrand = Color(0xFFD7DBE2)           // رنگ اصلی برند
val MjaAccent = Color(0xFFAEB8C7)          // رنگ تأکیدی

// Semantic
val MjaSuccess = Color(0xFF37C98B)         // رنگ موفقیت
val MjaWarning = Color(0xFFE4B45B)         // رنگ هشدار
val MjaError = Color(0xFFE35F68)           // رنگ خطا
val MjaOnline = Color(0xFF39C98A)          // رنگ وضعیت آنلاین

// Message bubbles
val MjaBubbleOut = Color(0xFF252C38)       // حباب پیام ارسالی
val MjaBubbleIn = Color(0xFF191C22)        // حباب پیام دریافتی

// Palette used for generated avatars (monochrome-safe, premium look)
val MjaAvatarPalette = listOf(
    Color(0xFF2E3440),
    Color(0xFF343B49),
    Color(0xFF3A3F4B),
    Color(0xFF2A303C),
    Color(0xFF404756),
    Color(0xFF2B3140),
    Color(0xFF454B58),
    Color(0xFF303743),
)

/** Subtle metallic silver gradient used by the MJA logo & primary surfaces. */
val MjaSilverGradient = listOf(
    Color(0xFFF4F5F7),
    Color(0xFFD7DBE2),
    Color(0xFFA4A9B2),
)
