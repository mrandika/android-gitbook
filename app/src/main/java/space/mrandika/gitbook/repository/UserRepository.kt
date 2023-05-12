package space.mrandika.gitbook.repository

import android.app.Application
import androidx.lifecycle.LiveData
import space.mrandika.gitbook.database.user.UserDao
import space.mrandika.gitbook.database.user.UserRoomDatabase
import space.mrandika.gitbook.model.local.user.UserLocal
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val mUsersDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserRoomDatabase.getDatabase(application)
        mUsersDao = db.userDao()
    }

    fun getAllUser(): LiveData<List<UserLocal>> = mUsersDao.getAllUsers()

    fun insert(user: UserLocal) {
        executorService.execute { mUsersDao.insert(user) }
    }

    fun delete(user: UserLocal) {
        executorService.execute { mUsersDao.delete(user) }
    }

    fun isUserExist(user: UserLocal): Boolean {
        return mUsersDao.isUserExist(user.login)
    }
}