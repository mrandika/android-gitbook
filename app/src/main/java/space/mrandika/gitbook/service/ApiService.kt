package space.mrandika.gitbook.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import space.mrandika.gitbook.model.remote.repository.Commit
import space.mrandika.gitbook.model.remote.repository.Repository
import space.mrandika.gitbook.model.remote.repository.RepositoryDetail
import space.mrandika.gitbook.model.remote.repository.RepositoryResult
import space.mrandika.gitbook.model.remote.user.User
import space.mrandika.gitbook.model.remote.user.UserDetail
import space.mrandika.gitbook.model.remote.user.UserResult

interface ApiService {
    @GET("users")
    fun getUsers(): Call<List<User>>

    @GET("search/users")
    fun searchUsers(
        @Query("q") query: String
    ): Call<UserResult>

    @GET("users/{login}")
    fun getUser(
        @Path("login") login: String
    ): Call<UserDetail>

    @GET("users/{login}/repos")
    fun getUserRepo(
        @Path("login") login: String
    ): Call<List<Repository>>

    @GET("users/{login}/following")
    fun getUserFollowing(
        @Path("login") login: String
    ): Call<List<User>>

    @GET("users/{login}/followers")
    fun getUserFollower(
        @Path("login") login: String
    ): Call<List<User>>

    @GET("repositories")
    fun getRepositories(): Call<List<Repository>>

    @GET("search/repositories")
    fun searchRepositories(
        @Query("q") query: String
    ): Call<RepositoryResult>

    @GET("repos/{login}/{repo_name}")
    fun getRepository(
        @Path("login") login: String,
        @Path("repo_name") repoName: String
    ): Call<RepositoryDetail>

    @GET("repos/{login}/{repo_name}/commits")
    fun getRepositoryCommits(
        @Path("login") login: String,
        @Path("repo_name") repoName: String
    ): Call<List<Commit>>
}