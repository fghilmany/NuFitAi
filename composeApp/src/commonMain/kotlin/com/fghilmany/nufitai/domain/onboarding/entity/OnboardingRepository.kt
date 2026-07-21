package com.fghilmany.nufitai.domain.onboarding.repository

import com.fghilmany.nufitai.core.error.AppResult
import com.fghilmany.nufitai.domain.onboarding.entity.OnboardingDraft
import com.fghilmany.nufitai.domain.onboarding.entity.UserProfile

/**
 * Repository contract for onboarding data persistence.
 * Implementations handle the actual storage (SQLDelight, in-memory, etc.).
 */
interface OnboardingRepository {
    suspend fun saveDraft(draft: OnboardingDraft): AppResult<Unit>
    suspend fun getDraft(): AppResult<OnboardingDraft?>
    suspend fun deleteDraft(): AppResult<Unit>
    suspend fun saveProfile(profile: UserProfile): AppResult<Unit>
    suspend fun getProfile(): AppResult<UserProfile?>
    suspend fun hasProfile(): AppResult<Boolean>
    suspend fun isParqCompleted(): AppResult<Boolean>
}
