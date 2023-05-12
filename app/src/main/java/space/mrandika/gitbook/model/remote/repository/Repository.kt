package space.mrandika.gitbook.model.remote.repository

import com.google.gson.annotations.SerializedName
import space.mrandika.gitbook.model.remote.user.User

data class Repository(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("owner")
	val owner: User,

	@field:SerializedName("name")
	val name: String,
)
