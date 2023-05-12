package space.mrandika.gitbook.viewmodel.users

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.mrandika.gitbook.config.ApiConfig
import space.mrandika.gitbook.model.local.user.UserLocal
import space.mrandika.gitbook.model.remote.user.User
import space.mrandika.gitbook.model.remote.user.UserResult
import space.mrandika.gitbook.repository.UserRepository

class UsersViewModel(application: Application): ViewModel() {
    // Room
    private val mUserRepository: UserRepository = UserRepository(application)

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    // Result
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _followings = MutableLiveData<List<User>>()
    val followings: LiveData<List<User>> = _followings

    private val _followers = MutableLiveData<List<User>>()
    val followers: LiveData<List<User>> = _followers

    fun getUsers() {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getUsers()
        client.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _users.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun getAllFavorites(): LiveData<List<UserLocal>> = mUserRepository.getAllUser()

    fun searchUsers(query: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().searchUsers(query)
        client.enqueue(object : Callback<UserResult> {
            override fun onResponse(call: Call<UserResult>, response: Response<UserResult>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _users.value = response.body()?.items
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<UserResult>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun getFollowings(username: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getUserFollowing(username)
        client.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _followings.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun getFollowers(username: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getUserFollower(username)
        client.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _followers.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }
}