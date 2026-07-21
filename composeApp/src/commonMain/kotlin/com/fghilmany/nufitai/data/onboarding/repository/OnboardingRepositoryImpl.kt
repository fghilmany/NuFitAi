package com.fghilmany.nufitai.data.onboarding.repository

import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.domain.onboarding.entity.OnboardingDraft
import com.fghilmany.nufitai.domain.onboarding.entity.UserProfile
import com.fghilmany.nufitai.domain.onboarding.repository.OnboardingRepository

class InMemoryOnboardingRepository : OnboardingRepository {
    private var currentDraft: OnboardingDraft? = null
    private var currentProfile: UserProfile? = null

    override suspend fun saveDraft(draft: OnboardingDraft): AppResult<Unit> { currentDraft = draft; return AppResult.Success(Unit) }
    override suspend fun getDraft(): AppResult<OnboardingDraft?> = AppResult.Success(currentDraft)
    override suspend fun deleteDraft(): AppResult<Unit> { currentDraft = null; return AppResult.Success(Unit) }
    override suspend fun saveProfile(profile: UserProfile): AppResult<Unit> { currentProfile = profile; return AppResult.Success(Unit) }
    override suspend fun getProfile(): AppResult<UserProfile?> = AppResult.Success(currentProfile)
    override suspend fun hasProfile(): AppResult<Boolean> = AppResult.Success(currentProfile != null)
    override suspend fun isParqCompleted(): AppResult<Boolean> = AppResult.Success(currentProfile?.parqCompleted == true)
}
