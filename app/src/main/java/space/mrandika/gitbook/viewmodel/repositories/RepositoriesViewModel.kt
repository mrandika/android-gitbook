package space.mrandika.gitbook.viewmodel.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.mrandika.gitbook.config.ApiConfig
import space.mrandika.gitbook.model.local.repository.RepositoryLocal
import space.mrandika.gitbook.model.remote.repository.Repository
import space.mrandika.gitbook.model.remote.repository.RepositoryResult
import space.mrandika.gitbook.repository.RepoRepository

class RepositoriesViewModel(application: Application): ViewModel() {
    // Room
    private val mRepoRepository: RepoRepository = RepoRepository(application)

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    // Result
    private val _repositories = MutableLiveData<List<Repository>>()
    val repositories: LiveData<List<Repository>> = _repositories

    fun getRepositories() {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getRepositories()
        client.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _repositories.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun getAllFavorites(): LiveData<List<RepositoryLocal>> = mRepoRepository.getAllRepositories()

    fun searchRepositories(query: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().searchRepositories(query)
        client.enqueue(object : Callback<RepositoryResult> {
            override fun onResponse(call: Call<RepositoryResult>, response: Response<RepositoryResult>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _repositories.value = response.body()?.items
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<RepositoryResult>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }
}