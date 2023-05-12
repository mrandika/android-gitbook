package space.mrandika.gitbook.database.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import space.mrandika.gitbook.model.local.repository.RepositoryLocal

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(repo: RepositoryLocal)

    @Delete
    fun delete(repo: RepositoryLocal)

    @Query("SELECT EXISTS (SELECT * FROM repository WHERE name = :name AND owner = :owner)")
    fun isRepoExist(name: String, owner: String): Boolean

    @Query("SELECT * from repository ORDER BY id ASC")
    fun getAllRepositories(): LiveData<List<RepositoryLocal>>
}