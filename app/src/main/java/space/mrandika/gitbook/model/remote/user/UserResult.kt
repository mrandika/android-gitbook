package space.mrandika.gitbook.model.remote.user

import com.google.gson.annotations.SerializedName

data class UserResult (
    @field:SerializedName("items")
    val items: List<User>
)