package com.fghilmany.nufitai.di

import org.junit.After
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.check.checkModules

class DiVerificationTest {

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `verify Koin configuration for all modules`() {
        startKoin {
            modules(platformModule, appModule, onboardingModule)
        }.checkModules()
    }
}
