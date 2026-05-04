package com.derdimet.mobil.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

interface SettingsStorage {
    fun getString(key: String): String?
    fun setString(key: String, value: String)
    fun remove(key: String)
}

@Serializable
data class ProfileSnapshot(
    val role: String,
    val accountType: String,
    val savedAt: Long
)

enum class AnimalCategoryFilter {
    ALL, KUCUKBAS, BUYUKBAS
}

class PreferencesRepository(
    private val storage: SettingsStorage
) {
    private val KEYS = object {
        val REMEMBER = "derdimet_pref_remember"
        val EMAIL = "derdimet_pref_email"
        val PROFILE_SNAPSHOT = "derdimet_pref_profile"
        val ANIMAL_FILTER = "derdimet_pref_animal_filter"
    }

    fun getRememberedEmail(): Pair<Boolean, String> {
        val remember = storage.getString(KEYS.REMEMBER) == "1"
        val email = storage.getString(KEYS.EMAIL) ?: ""
        return Pair(remember, email)
    }

    fun setRememberPreference(remember: Boolean, email: String) {
        if (remember) {
            storage.setString(KEYS.REMEMBER, "1")
            storage.setString(KEYS.EMAIL, email.trim())
        } else {
            storage.remove(KEYS.REMEMBER)
            storage.remove(KEYS.EMAIL)
        }
    }

    fun persistAuthProfileSnapshot(role: String, accountType: String) {
        val snapshot = ProfileSnapshot(role, accountType, kotlinx.datetime.Clock.System.now().toEpochMilliseconds())
        storage.setString(KEYS.PROFILE_SNAPSHOT, Json.encodeToString(snapshot))
    }

    fun getAnimalCategoryFilter(): AnimalCategoryFilter {
        return try {
            val v = storage.getString(KEYS.ANIMAL_FILTER)
            AnimalCategoryFilter.valueOf(v ?: "ALL")
        } catch (e: Exception) {
            AnimalCategoryFilter.ALL
        }
    }

    fun setAnimalCategoryFilter(filter: AnimalCategoryFilter) {
        storage.setString(KEYS.ANIMAL_FILTER, filter.name)
    }
}
