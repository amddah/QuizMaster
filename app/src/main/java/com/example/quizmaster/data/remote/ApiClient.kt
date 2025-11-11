package com.example.quizmaster.data.remote

import com.example.quizmaster.data.model.UserRole
import com.example.quizmaster.data.model.UserRoleDeserializer
import com.google.gson.GsonBuilder
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
    
    // API base URLs - Use 10.0.2.2 for Android emulator to access host machine's localhost
    // For physical devices, replace with your computer's IP address (e.g., "http://192.168.1.100:8080/api/v1/")
    private const val BASE_URL = "http://192.168.1.180:8080/api/v1/"
    private const val COURSE_API_BASE_URL = "http://10.0.2.2:8080/api/v1/"

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
    
    // Custom Gson with UserRole deserializer to handle lowercase values from API
    private val gson = GsonBuilder()
        .registerTypeAdapter(UserRole::class.java, UserRoleDeserializer())
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    private val courseRetrofit = Retrofit.Builder()
        .baseUrl(COURSE_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
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
    
    val leaderboardApiService: LeaderboardApiService by lazy {
        retrofit.create(LeaderboardApiService::class.java)
    }
    
    fun createGamificationService(): GamificationApiService {
        return retrofit.create(GamificationApiService::class.java)
    }
}
