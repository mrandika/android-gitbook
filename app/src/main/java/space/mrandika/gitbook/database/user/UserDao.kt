package space.mrandika.gitbook.database.user

import androidx.lifecycle.LiveData
import androidx.room.*
import space.mrandika.gitbook.model.local.user.UserLocal

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: UserLocal)

    @Delete
    fun delete(user: UserLocal)

    @Query("SELECT EXISTS (SELECT * FROM user WHERE login = :login)")
    fun isUserExist(login: String): Boolean

    @Query("SELECT * from user ORDER BY id ASC")
    fun getAllUsers(): LiveData<List<UserLocal>>
}