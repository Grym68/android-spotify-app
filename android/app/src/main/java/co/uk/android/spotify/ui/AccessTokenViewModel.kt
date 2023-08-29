package co.uk.android.spotify.ui

import androidx.lifecycle.ViewModel
import co.uk.android.spotify.data.api.AccessToken
import co.uk.android.spotify.data.repositories.TokensRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AccessTokenViewModel(
    val tokensRepository: TokensRepository,
    coroutineScopeProvider: CoroutineScope? = null
) : ViewModel() {
    val viewModelScope = getViewModelScope(coroutineScopeProvider) // work on this

    data class State(
        // something
        val urmom: AccessToken? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = getToken()

    private fun getToken(): StateFlow<State> {
        val success = tokensRepository.state.value.accessToken?.getOrNull()
        _state.update { it.copy(urmom = success) }
        return _state
    }
}
