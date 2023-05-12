package space.mrandika.gitbook.model.remote.repository

import com.google.gson.annotations.SerializedName
import space.mrandika.gitbook.model.remote.user.User

data class Commit(
	@field:SerializedName("commit")
	val detail: CommitDetail? = null,

	@field:SerializedName("committer")
	val committer: User? = null,
)

data class CommitDetail(
	@field:SerializedName("author")
	val author: Author? = null,

	@field:SerializedName("message")
	val message: String? = null,
)

data class Author(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null,
)

