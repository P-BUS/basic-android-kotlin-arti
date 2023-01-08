package com.example.arti.data.network

import com.example.arti.data.model.OpenLibrarySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/*

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val logging = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

// Build a Retrofit object with the Moshi converter and logging client
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .baseUrl(BASE_URL)
    .build()
*/

interface BooksApiService {
    //Searches e-books with full text by description and language
    @GET("search.json")
    suspend fun getSearchBooks(
        @Query("q") searchText: String = "Ukraine",
        @Query("language") booksLanguage: String = "ukr",
        @Query("has_fulltext") hasFullText: String = "true",
        @Query("mode") typeOfDocument: String = "ebooks"
    ): OpenLibrarySearchResponse
}

/*object BooksRemoteDataSource {
    val retrofitApiService: BooksApiService by lazy {
        retrofit.create(BooksApiService::class.java)
    }
}*/
