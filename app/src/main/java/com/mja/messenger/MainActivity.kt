package com.mja.messenger

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mja.messenger.navigation.MjaNavHost
import com.mja.messenger.ui.theme.MjaTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    /** Persian is the primary language of the product. */
    override fun attachBaseContext(newBase: Context) {
        val cfg = Configuration(newBase.resources.configuration)
        cfg.setLocale(Locale("fa"))
        cfg.setLayoutDirection(Locale("fa"))
        super.attachBaseContext(newBase.createConfigurationContext(cfg))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MjaTheme {
                MjaNavHost()
            }
        }
    }
}
