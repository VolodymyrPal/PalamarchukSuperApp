package com.hfad.palamarchuksuperapp.feature.bone.ui.login

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LoginWithCredentialsUseCase
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LogoutUseCase
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.ObserveLoginStatusUseCase
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.RefreshTokenUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginScreenViewModel @Inject constructor(
    observeLoginStatusUseCase: ObserveLoginStatusUseCase,
    private val loginWithCredentialsUseCase: LoginWithCredentialsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) : GenericViewModel<LoginScreenViewModel.LoginScreenState, LoginScreenViewModel.Event, LoginScreenViewModel.Effect>() {

    override val _dataFlow: Flow<LogStatus> = observeLoginStatusUseCase().onEach {
        when (it) {
            LogStatus.LOGIN_ALLOWED -> {
                effect(Effect.LoginSuccess)
            }

            LogStatus.REQUIRE_WEAK_LOGIN -> {
                effect(Effect.RequireWeakLogin)
            }

            LogStatus.TOKEN_REFRESH_REQUIRED -> {
                refreshTokenUseCase() // Refresh token if required TODO
            }

            else -> {}
        }
    }

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)
    override val _loading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val _email = MutableStateFlow("State - email test") //TODO remove test var / data
    private val _password = MutableStateFlow("State - password test")//TODO remove test var / data
    private val _rememberMe = MutableStateFlow(false)
    private val _passwordVisible = MutableStateFlow(false)

    override val uiState: StateFlow<LoginScreenState> = combine(
        _dataFlow,
        _errorFlow,
        _loading,
        _email,
        _password,
        _rememberMe,
        _passwordVisible,
    ) { flows ->
        var isLoggedIn = flows[0] as LogStatus
        val error = flows[1] as AppError?
        val loading = flows[2] as Boolean
        val email = flows[3] as String
        val password = flows[4] as String
        val rememberMe = flows[5] as Boolean
        val passwordVisible = flows[6] as Boolean

        LoginScreenState(
            email = email,
            password = password,
            rememberMe = rememberMe,
            passwordVisible = passwordVisible,
            isLoading = loading,
            error = error,
            isCreatingPossible = false, // Need to update this logic based on requirements
            isAlreadyLogged = isLoggedIn != LogStatus.NOT_LOGGED // Logic based on requirements
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = LoginScreenState()
    )

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
                login()
            }

            is Event.BiometricLoginClicked -> {
            }

            is Event.ErrorDismissed -> {
                _errorFlow.value = null
            }

            is Event.ClearLogin -> {
                viewModelScope.launch {
                    logoutUseCase()
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _loading.value = true
            _errorFlow.value = null

            try {
                val result = loginWithCredentialsUseCase(
                    username = _email.value,
                    password = _password.value,
                    isRemembered = _rememberMe.value
                )

                when (result) {
                    is AppResult.Success -> {
                        _loading.value = false
                    }

                    is AppResult.Error -> {
                        _errorFlow.value = result.error
                        effect(Effect.ShowError(result.error.message ?: "Login failed"))
                        _loading.value = false
                    }
                }
            } catch (e: Exception) {
                val error = AppError.NetworkException.ApiError.CustomApiError(
                    message = "Login error: ${e.message}", cause = e
                )
                _errorFlow.value = error
                effect(Effect.ShowError(error.message.toString()))
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
        object Logout : Event()
    }

    sealed class Effect : BaseEffect {
        object LoginSuccess : Effect()
        object RequireWeakLogin : Effect()
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
        val isAlreadyLogged: Boolean = true,
    ) : ScreenState
}