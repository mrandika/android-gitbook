package space.mrandika.gitbook.model.remote.user
import com.google.gson.annotations.SerializedName

data class User(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("login")
	val login: String,

	@field:SerializedName("avatar_url")
	val avatarUrl: String,
)
