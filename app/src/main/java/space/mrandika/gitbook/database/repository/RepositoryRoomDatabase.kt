package space.mrandika.gitbook.database.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import space.mrandika.gitbook.model.local.repository.RepositoryLocal

@Database(entities = [RepositoryLocal::class], version = 1)
abstract class RepositoryRoomDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao

    companion object {
        @Volatile
        private var INSTANCE: RepositoryRoomDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): RepositoryRoomDatabase {
            if (INSTANCE == null) {
                synchronized(RepositoryRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        RepositoryRoomDatabase::class.java, "repository_database")
                        .build()
                }
            }
            return INSTANCE as RepositoryRoomDatabase
        }
    }
}