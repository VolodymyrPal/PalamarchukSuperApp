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
            is Event.loginFieldChanges -> {

            }

            is Event.loginButtonClicked -> {

            }
        }
    }


    sealed class Event : BaseEvent {
        data class loginFieldChanges(val username: String, val password: String) : Event()
        class loginButtonClicked() : Event()
    }

    sealed class Effect : BaseEffect {
        data class LoginSuccess(val login: String) : Effect()
    }

    data class StateLoginScreen(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: AppError? = null,
    ) : ScreenState
}