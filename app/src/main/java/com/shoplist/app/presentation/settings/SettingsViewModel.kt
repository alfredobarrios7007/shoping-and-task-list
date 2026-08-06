package com.shoplist.app.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingsUiState(
    val selectedLanguageTag: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(selectedLanguageTag = AppCompatDelegate.getApplicationLocales().toLanguageTags().ifBlank { null })
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onSelectLanguage(languageTag: String?) {
        val locales = if (languageTag == null) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
        _uiState.value = SettingsUiState(selectedLanguageTag = languageTag)
    }
}
