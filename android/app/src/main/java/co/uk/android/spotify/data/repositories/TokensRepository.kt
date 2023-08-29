package co.uk.android.spotify.data.repositories

import co.uk.android.spotify.data.api.AccessToken
import co.uk.android.spotify.data.api.AccessTokenRequest
import co.uk.android.spotify.network.SpotifyHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TokensRepository(
    private val spotifyManager: SpotifyHandler
) {
    data class AccessTokenState(
        // prolly some id too?
        val isLoading: Boolean = false,
        val accessToken: Result<AccessToken>? = null,
        val tokenRequest: AccessTokenRequest? = null // ??? Maybe not needed?
        // deffo an expiry for the accessToken.
    )

    private var _state = MutableStateFlow(AccessTokenState())
    val state: StateFlow<AccessTokenState> = _state
    private var tokenJob: Job? = null

    fun retrieve(scope: CoroutineScope, grantType: String, clientId: String, clientSecret: String) {
        val request = AccessTokenRequest(
            grantType = grantType,
            clientId = clientId,
            clientSecret = clientSecret
        )
        retrieve(scope, request)
    }

    private fun retrieve(scope: CoroutineScope, request: AccessTokenRequest) {
        tokenJob?.cancel()
        tokenJob = scope.launch {
            _state.update { it.copy(isLoading = true, tokenRequest = request) }
            val result = spotifyManager.fetchAccessToken(
                request.grantType,
                request.clientId,
                request.clientSecret
            )
            _state.update { it.copy(isLoading = false, accessToken = result) }
        }
    }
}
