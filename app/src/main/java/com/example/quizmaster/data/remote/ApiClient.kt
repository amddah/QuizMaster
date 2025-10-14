package com.example.quizmaster.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * API Client configuration
 */
object ApiClient {
    
    // TODO: Replace with your actual API base URLs
    private const val BASE_URL = "https://your-api-server.com/"
    private const val COURSE_API_BASE_URL = "https://your-course-api.com/"
    
    private var authToken: String? = null
    
    fun setAuthToken(token: String?) {
        authToken = token
    }
    
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        chain.proceed(requestBuilder.build())
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val courseRetrofit = Retrofit.Builder()
        .baseUrl(COURSE_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
    
    val quizApiService: QuizApiService by lazy {
        retrofit.create(QuizApiService::class.java)
    }
    
    val quizAttemptApiService: QuizAttemptApiService by lazy {
        retrofit.create(QuizAttemptApiService::class.java)
    }
    
    val courseApiService: CourseApiService by lazy {
        courseRetrofit.create(CourseApiService::class.java)
    }
}
