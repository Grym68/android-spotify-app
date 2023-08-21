package co.uk.android.spotify.network

import co.uk.android.spotify.data.api.AccessToken

interface SpotifyHandler {

    suspend fun fetchAccessToken(grantType: String, clientId: String, clientSecret: String): Result<AccessToken>
}