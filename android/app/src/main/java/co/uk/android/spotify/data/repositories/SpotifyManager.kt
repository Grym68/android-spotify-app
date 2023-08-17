package co.uk.android.spotify.data.repositories

import co.uk.android.spotify.data.api.AccessToken
import co.uk.android.spotify.network.SpotifyHandler

class SpotifyManager(): SpotifyHandler {
    override suspend fun fetchAccessToken(): Result<AccessToken> {
        TODO("Not yet implemented")
    }


}