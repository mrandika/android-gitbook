package space.mrandika.gitbook.repository

import android.app.Application
import androidx.lifecycle.LiveData
import space.mrandika.gitbook.database.repository.RepositoryDao
import space.mrandika.gitbook.database.repository.RepositoryRoomDatabase
import space.mrandika.gitbook.model.local.repository.RepositoryLocal
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RepoRepository(application: Application) {
    private val mRepositoriesDao: RepositoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = RepositoryRoomDatabase.getDatabase(application)
        mRepositoriesDao = db.repositoryDao()
    }

    fun getAllRepositories(): LiveData<List<RepositoryLocal>> = mRepositoriesDao.getAllRepositories()

    fun insert(repo: RepositoryLocal) {
        executorService.execute { mRepositoriesDao.insert(repo) }
    }

    fun delete(repo: RepositoryLocal) {
        executorService.execute { mRepositoriesDao.delete(repo) }
    }

    fun isRepoExist(name: String, owner: String): Boolean {
        return mRepositoriesDao.isRepoExist(name, owner)
    }
}