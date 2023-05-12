package space.mrandika.gitbook.viewmodel.repositories

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
import space.mrandika.gitbook.model.local.repository.RepositoryLocal
import space.mrandika.gitbook.model.remote.repository.Commit
import space.mrandika.gitbook.model.remote.repository.RepositoryDetail
import space.mrandika.gitbook.repository.RepoRepository

class RepositoryViewModel(application: Application): ViewModel() {
    // Room
    private val mRepoRepository: RepoRepository = RepoRepository(application)

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    // Result
    private val _repository = MutableLiveData<RepositoryDetail>()
    val repository: LiveData<RepositoryDetail> = _repository

    private val _commits = MutableLiveData<List<Commit>>()
    val commits: LiveData<List<Commit>> = _commits

    fun getRepository(login: String, name: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getRepository(login, name)
        client.enqueue(object : Callback<RepositoryDetail> {
            override fun onResponse(call: Call<RepositoryDetail>, response: Response<RepositoryDetail>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _repository.value = response.body()

                    getCommits(login, name)
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<RepositoryDetail>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun getCommits(login: String, name: String) {
        val client = ApiConfig.getApiService().getRepositoryCommits(login, name)
        client.enqueue(object : Callback<List<Commit>> {
            override fun onResponse(call: Call<List<Commit>>, response: Response<List<Commit>>) {
                if (response.isSuccessful) {
                    _commits.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<List<Commit>>, t: Throwable) {
                _isError.value = true
            }
        })
    }

    private fun addToFavorites(repo: RepositoryLocal) {
        mRepoRepository.insert(repo)
    }

    private fun removeFromFavorites(repo: RepositoryLocal) {
        mRepoRepository.delete(repo)
    }

    fun isRepoExist(callback: (result: Boolean, repo: RepositoryLocal) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val repo = mapRemoteToLocal(_repository.value)
            val isExist = mRepoRepository.isRepoExist(repo.name, repo.owner)

            callback.invoke(isExist, repo)
        }
    }

    fun onFavorited(callback: (result: Boolean) -> Unit) {
        isRepoExist { exist, repo ->
            var result = false

            if (exist) {
                removeFromFavorites(repo)
            } else {
                result = true
                addToFavorites(repo)
            }

            callback.invoke(result)
        }
    }

    private fun mapRemoteToLocal(repo: RepositoryDetail?): RepositoryLocal {
        return RepositoryLocal(repo?.id ?: 0, repo?.description ?: "", repo?.owner?.login ?: "", repo?.owner?.avatarUrl ?: "", repo?.name ?: "")
    }
}