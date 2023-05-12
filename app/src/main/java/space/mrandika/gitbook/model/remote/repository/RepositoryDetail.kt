package space.mrandika.gitbook.model.remote.repository

import com.google.gson.annotations.SerializedName
import space.mrandika.gitbook.model.remote.user.User

data class RepositoryDetail(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("language")
	val language: String? = null,

	@field:SerializedName("owner")
	val owner: User,

	@field:SerializedName("size")
	val size: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("watchers")
	val watchers: Int? = null,

	@field:SerializedName("forks_count")
	val forksCount: Int? = null
)
