package com.fghilmany.nufitai.domain.onboarding.entity

data class BodyMeasurements(
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val bmi: Double? = null,
) {
    companion object {
        fun computeBmi(heightCm: Double?, weightKg: Double?): Double? {
            if (heightCm == null || weightKg == null) return null
            if (heightCm <= 0 || weightKg <= 0) return null
            val heightM = heightCm / 100.0
            return weightKg / (heightM * heightM)
        }
        fun bmiCategory(bmi: Double): String = when {
            bmi < 18.5 -> "Kurus"; bmi < 25.0 -> "Normal"; bmi < 30.0 -> "Gemuk"; else -> "Obesitas"
        }
    }
}

data class UserProfile(
    val id: String, val parqCompleted: Boolean = false, val healthFlags: Set<HealthFlag> = emptySet(),
    val experience: GymExperience? = null, val goal: FitnessGoal? = null, val frequency: WorkoutFrequency? = null,
    val split: WorkoutSplit? = null, val splitAutoSelected: Boolean = false, val splitReason: String? = null,
    val bodyMeasurements: BodyMeasurements? = null, val assessmentCompleted: Boolean = false,
    val createdAt: String, val updatedAt: String,
)

data class OnboardingDraft(
    val id: String, val parqAnswers: Map<Int, Boolean> = emptyMap(), val parqCompleted: Boolean = false,
    val assessmentStep: Int? = null, val assessmentAnswers: Map<Int, String> = emptyMap(),
    val bodyHeightCm: Double? = null, val bodyWeightKg: Double? = null,
    val startedAt: String, val completedAt: String? = null,
)

data class ParqQuestion(val index: Int, val text: String, val flag: HealthFlag?)

val parqQuestions = listOf(
    ParqQuestion(0, "Apakah dokter pernah menyatakan Anda memiliki masalah jantung?", HealthFlag.HEART_PROBLEM),
    ParqQuestion(1, "Apakah Anda sering merasakan nyeri di dada saat beraktivitas?", HealthFlag.CHEST_PAIN),
    ParqQuestion(2, "Apakah Anda sering merasa pusing atau pingsan?", HealthFlag.FAINTING),
    ParqQuestion(3, "Apakah Anda memiliki masalah tulang atau sendi yang bisa diperparah oleh olahraga?", HealthFlag.BONE_JOINT_PROBLEM),
    ParqQuestion(4, "Apakah Anda memiliki tekanan darah tinggi yang belum terkontrol?", HealthFlag.HIGH_BLOOD_PRESSURE),
    ParqQuestion(5, "Apakah Anda sedang mengonsumsi obat rutin dari dokter?", HealthFlag.TAKING_MEDICATION),
    ParqQuestion(6, "Apakah ada kondisi kesehatan lain yang perlu kami ketahui?", HealthFlag.OTHER_HEALTH_CONDITION),
)
