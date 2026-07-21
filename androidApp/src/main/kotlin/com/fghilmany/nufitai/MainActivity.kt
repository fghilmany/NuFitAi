package com.fghilmany.nufitai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fghilmany.nufitai.app.NuFitAIApp
import com.fghilmany.nufitai.di.appModule
import com.fghilmany.nufitai.di.onboardingModule
import com.fghilmany.nufitai.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(platformModule, appModule, onboardingModule)
        }

        setContent {
            NuFitAIApp()
        }
    }
}
