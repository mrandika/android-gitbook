package space.mrandika.gitbook.model.remote.repository

import com.google.gson.annotations.SerializedName

data class RepositoryResult (
    @field:SerializedName("items")
    val items: List<Repository>
)
