package com.fghilmany.nufitai.di

import com.fghilmany.nufitai.data.db.DatabaseDriverFactory
import com.fghilmany.nufitai.data.db.NuFitDatabase
import com.fghilmany.nufitai.data.onboarding.repository.SqlDelightOnboardingRepository
import com.fghilmany.nufitai.domain.onboarding.repository.OnboardingRepository
import com.fghilmany.nufitai.presentation.assessment.viewmodel.AssessmentViewModel
import com.fghilmany.nufitai.presentation.measurements.viewmodel.MeasurementsViewModel
import com.fghilmany.nufitai.presentation.parq.viewmodel.ParqViewModel
import com.fghilmany.nufitai.presentation.splash.viewmodel.SplashViewModel
import com.fghilmany.nufitai.usecase.onboarding.CompleteParq
import com.fghilmany.nufitai.usecase.onboarding.CompleteParqImpl
import com.fghilmany.nufitai.usecase.onboarding.DetermineNextScreen
import com.fghilmany.nufitai.usecase.onboarding.DetermineNextScreenImpl
import com.fghilmany.nufitai.usecase.onboarding.GenerateMonthlyPlan
import com.fghilmany.nufitai.usecase.onboarding.GenerateMonthlyPlanImpl
import com.fghilmany.nufitai.usecase.onboarding.ResolveSplit
import com.fghilmany.nufitai.usecase.onboarding.ResolveSplitImpl
import com.fghilmany.nufitai.usecase.onboarding.SaveAssessmentAnswer
import com.fghilmany.nufitai.usecase.onboarding.SaveAssessmentAnswerImpl
import com.fghilmany.nufitai.usecase.onboarding.SaveBodyMeasurements
import com.fghilmany.nufitai.usecase.onboarding.SaveBodyMeasurementsImpl
import com.fghilmany.nufitai.usecase.onboarding.SaveParqAnswer
import com.fghilmany.nufitai.usecase.onboarding.SaveParqAnswerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val onboardingModule = module {
    // Database - DatabaseDriverFactory must be provided by platform-specific module
    single { NuFitDatabase(get<DatabaseDriverFactory>().createDriver()) }

    // Repository
    single { SqlDelightOnboardingRepository(get()) } bind OnboardingRepository::class

    // Use cases — bind interface to implementation
    factory { SaveParqAnswerImpl(get()) } bind SaveParqAnswer::class
    factory { CompleteParqImpl(get()) } bind CompleteParq::class
    factory { SaveAssessmentAnswerImpl(get()) } bind SaveAssessmentAnswer::class
    factory { ResolveSplitImpl() } bind ResolveSplit::class
    factory { GenerateMonthlyPlanImpl() } bind GenerateMonthlyPlan::class
    factory { SaveBodyMeasurementsImpl(get()) } bind SaveBodyMeasurements::class
    factory { DetermineNextScreenImpl(get()) } bind DetermineNextScreen::class

    // ViewModels
    viewModelOf(::SplashViewModel)
    viewModelOf(::ParqViewModel)
    viewModelOf(::AssessmentViewModel)
    viewModelOf(::MeasurementsViewModel)
}
