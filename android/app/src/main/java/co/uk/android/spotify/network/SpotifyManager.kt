package co.uk.android.spotify.network

import android.util.Log
import co.uk.android.spotify.data.api.AccessToken
import co.uk.android.spotify.data.api.SpotifyApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class SpotifyManager(
    val spotifyApi: SpotifyApi,
    @Named("io")
    private val dispatcher: CoroutineDispatcher,
): SpotifyHandler {
    override suspend fun fetchAccessToken(
        grantType: String,
        clientId: String,
        clientSecret: String,
    ): Result<AccessToken> {
        return safeApiCall {
            spotifyApi.requestAccessToken(
                grantType = grantType,
                clientId = clientId,
                clientSecret = clientSecret,
            )
        }
    }

    private suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = this.dispatcher,
        apiCall: suspend () -> T,
    ) : Result<T> {
        return withContext(dispatcher) {
            runCatching {
                apiCall.invoke()
            }.onFailure {
                Log.e("SAFE API CALL", it.toString())
            }.recoverCatching { throw ApiError.wrap(it) }
        }
    }

}