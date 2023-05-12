package space.mrandika.gitbook.viewmodel.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import space.mrandika.gitbook.prefs.setting.SettingPreferences

class SettingViewModel(private val pref: SettingPreferences): ViewModel() {
    fun getThemeSettings(): LiveData<Int> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(darkMode: Int) {
        viewModelScope.launch {
            pref.saveThemeSetting(darkMode)
            AppCompatDelegate.setDefaultNightMode(darkMode)
        }
    }
}