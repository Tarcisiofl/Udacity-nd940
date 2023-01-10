package br.com.tarcisiofl.asteroidradar.network

import br.com.tarcisiofl.asteroidradar.BuildConfig
import br.com.tarcisiofl.asteroidradar.PictureOfDay
import br.com.tarcisiofl.asteroidradar.domain.Asteroid
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A retrofit service to fetch a devbyte playlist.
 */
interface NasaService {
    @GET("neo/rest/v1/feed")
    fun getAsteroidsListAsync(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String? = null,
        @Query("api_key") key: String = BuildConfig.APIKEY
    ): Deferred<Asteroid>

    @GET("planetary/apod")
    fun getPictureOfTheDayAsync(
        @Query("api_key") key: String = BuildConfig.APIKEY
    ): Deferred<PictureOfDay>
}

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Main entry point for network access. Call like `Network.devbytes.getPlaylist()`
 */
object Network {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val nasa: NasaService = retrofit.create(NasaService::class.java)
}