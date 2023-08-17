package co.uk.android.spotify.module

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.core.annotation.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


@Module
@ComponentScan("co.uk.practice.compose_practice")
class AppModule() {

    @Factory
    fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Factory
    @Named("io")
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Factory
    @Named("main")
    fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Single
    fun okHttp(): OkHttpClient {
        //TODO: Look if there is a benefit into doing it in 3 steps rather then 1
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(timeout = 30, TimeUnit.SECONDS)
            . readTimeout(timeout = 30, TimeUnit.SECONDS)
            .writeTimeout(timeout = 30, unit = TimeUnit.SECONDS)
            .cache(null)
        return builder.build()
    }

    @Single
    fun moshi(): Moshi = Moshi.Builder()
        .build()

    @Single
    fun retrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://accounts.spotify.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}