package com.beginner.myapplication.utils.network

import com.google.gson.GsonBuilder
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.github.com"

    val gitHubClient: GitHubEndpoint
        get() {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .allEnabledTlsVersions()
                .allEnabledCipherSuites()
                .build()

            val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    onIntercept(chain)
                }
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(loggingInterceptor)
                .connectionSpecs(Collections.singletonList(spec))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(GitHubEndpoint::class.java)
        }

    @Throws(IOException::class)
    private fun onIntercept(chain: Interceptor.Chain): Response {
        try {
            val response = chain.request().newBuilder()
                .header("Authorization", "Bearer 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")
                .build()
            return chain.proceed(response)
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
        }

        return chain.proceed(chain.request())
    }
}