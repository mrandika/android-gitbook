package space.mrandika.gitbook.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import space.mrandika.gitbook.prefs.setting.SettingPreferences
import space.mrandika.gitbook.viewmodel.repositories.RepositoriesViewModel
import space.mrandika.gitbook.viewmodel.repositories.RepositoryViewModel
import space.mrandika.gitbook.viewmodel.setting.SettingViewModel
import space.mrandika.gitbook.viewmodel.users.UserViewModel
import space.mrandika.gitbook.viewmodel.users.UsersViewModel

class ViewModelFactory private constructor(private val mApplication: Application, private val mPref: SettingPreferences?) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application, pref: SettingPreferences?): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application, pref)
                }
            }

            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            return UsersViewModel(mApplication) as T
        } else if (modelClass.isAssignableFrom(RepositoriesViewModel::class.java)) {
            return RepositoriesViewModel(mApplication) as T
        } else if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return mPref?.let { SettingViewModel(it) } as T
        }

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(mApplication) as T
        } else if (modelClass.isAssignableFrom(RepositoryViewModel::class.java)) {
            return RepositoryViewModel(mApplication) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}