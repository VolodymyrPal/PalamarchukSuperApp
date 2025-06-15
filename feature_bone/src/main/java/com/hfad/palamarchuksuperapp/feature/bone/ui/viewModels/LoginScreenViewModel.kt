package com.hfad.palamarchuksuperapp.feature.bone.ui.viewModels

import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : GenericViewModel<LoginScreenViewModel.StateLoginScreen, LoginScreenViewModel.Event, LoginScreenViewModel.Effect>() {

    override val uiState: StateFlow<StateLoginScreen> = MutableStateFlow(StateLoginScreen())
    override val _dataFlow: Flow<Any> = flow {}

    override val _errorFlow: Flow<AppError?> = MutableStateFlow(null)
    override fun event(event: Event) {
        when (event) {
            is Event.EmailChanged -> {
                _email.value = event.email
                _errorFlow.value = null
            }

            is Event.PasswordChanged -> {
                _password.value = event.password
                _errorFlow.value = null
            }

            is Event.RememberMeChanged -> {
                _rememberMe.value = event.rememberMe
            }

            is Event.PasswordVisibilityToggled -> {
                _passwordVisible.value = !_passwordVisible.value
            }

            is Event.LoginButtonClicked -> {
                viewModelScope.launch {
                    val result = authRepository.login(
                        username = _email.value,
                        password = _password.value,
                    )
                    if (result is Result.Success) {
                        effect(Effect.LoginSuccess)
                    } else {
                        _errorFlow.value = (result as Result.Error).error
                        effect(Effect.ShowError(result.error.message ?: "Unknown error"))
                    }
                }
            }

            is Event.BiometricLoginClicked -> {
            }

            is Event.ErrorDismissed -> {
                _errorFlow.value = null
            }
        }
    }

    sealed class Event : BaseEvent {
        data class EmailChanged(val email: String) : Event()
        data class PasswordChanged(val password: String) : Event()
        data class RememberMeChanged(val rememberMe: Boolean) : Event()
        object PasswordVisibilityToggled : Event()
        object LoginButtonClicked : Event()
        object BiometricLoginClicked : Event()
        object ErrorDismissed : Event()
    }

    sealed class Effect : BaseEffect {
        object LoginSuccess : Effect()
        object BiometricAuthFailed : Effect()
        data class ShowError(val message: String) : Effect()

    }

    data class LoginScreenState(
        var email: String = "Login Screen State - email test", //TODO remove test var / data
        var password: String = "Login Screen State - password test", //TODO remove test var / data
        val rememberMe: Boolean = false,
        val isLoading: Boolean = false,
        val isCreatingPossible: Boolean = false,
        val error: AppError? = null,
        val passwordVisible: Boolean = false,
        val isAlreadyLogged: Boolean = false,
    ) : ScreenState
}