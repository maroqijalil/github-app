package com.beginner.myapplication.utils.network

import com.beginner.myapplication.model.SearchUsersList
import com.beginner.myapplication.model.UserDetail
import com.beginner.myapplication.model.UserFoll
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubEndpoint {
    @GET("/search/users")
    @Headers("Authorization: token Bearer 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")
    suspend fun getSearch(@Query("q") username: String): Response<SearchUsersList>

    @GET("/users/{username}")
    @Headers("Authorization: token Bearer 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")
    fun getDetail(@Path("username") username: String): Call<UserDetail>

    @GET("/users/{username}/followers")
    @Headers("Authorization: token Bearer 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")
    suspend fun getFollowers(@Path("username") username: String): Response<UserFoll>

    @GET("/users/{username}/following")
    @Headers("Authorization: token Bearer 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")
    suspend fun getFollowing(@Path("username") username: String): Response<UserFoll>
}