package space.mrandika.gitbook.model.local.repository

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "repository")
@Parcelize
data class RepositoryLocal(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("owner")
    val owner: String,

    @ColumnInfo("avatar_url")
    val avatarUrl: String,

    @ColumnInfo("name")
    val name: String,
): Parcelable