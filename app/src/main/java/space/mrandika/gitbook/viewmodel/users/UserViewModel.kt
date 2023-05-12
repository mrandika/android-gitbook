package space.mrandika.gitbook.viewmodel.users

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.mrandika.gitbook.config.ApiConfig
import space.mrandika.gitbook.model.local.user.UserLocal
import space.mrandika.gitbook.model.remote.repository.Repository
import space.mrandika.gitbook.model.remote.user.UserDetail
import space.mrandika.gitbook.repository.UserRepository

class UserViewModel(application: Application): ViewModel() {
    // Room
    private val mUserRepository: UserRepository = UserRepository(application)

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    // Result
    private val _user = MutableLiveData<UserDetail>()
    val user: LiveData<UserDetail> = _user

    private val _repositories = MutableLiveData<List<Repository>>()
    val repositories: LiveData<List<Repository>> = _repositories

    fun getUserDetail(username: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getUser(username)
        client.enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    private fun addToFavorites(user: UserLocal) {
        mUserRepository.insert(user)
    }

    private fun removeFromFavorites(user: UserLocal) {
        mUserRepository.delete(user)
    }

    fun isUserExist(callback: (result: Boolean, user: UserLocal) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = mapRemoteToLocal(_user.value)
            val isExist = mUserRepository.isUserExist(user)

            callback.invoke(isExist, user)
        }
    }

    fun onFavorited(callback: (result: Boolean) -> Unit) {
        isUserExist { exist, user ->
            var result = false

            if (exist) {
                removeFromFavorites(user)
            } else {
                result = true
                addToFavorites(user)
            }

            callback.invoke(result)
        }
    }

    private fun mapRemoteToLocal(user: UserDetail?): UserLocal {
        return UserLocal(user?.id ?: 0, user?.login ?: "", user?.avatarUrl ?: "")
    }
}