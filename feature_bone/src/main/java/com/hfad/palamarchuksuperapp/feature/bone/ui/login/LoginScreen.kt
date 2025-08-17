package com.hfad.palamarchuksuperapp.feature.bone.ui.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppOutlinedTextField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appEditOutlinedTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.daggerViewModel
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.FeatureBoneRoutes
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.LocalBoneDependencies
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.LocalNavAnimatedVisibilityScope
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.LocalNavController
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.LocalSharedTransitionScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: LoginScreenViewModel = daggerViewModel<LoginScreenViewModel>(
        factory = LocalBoneDependencies.current.viewModelFactory
    ),
    navController: NavController? = LocalNavController.current,
) {
    val localTransitionScope = LocalSharedTransitionScope.current
        ?: error(IllegalStateException("No SharedElementScope found"))
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current
        ?: error(IllegalStateException("No AnimatedVisibility found"))

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val biometricHandler = remember { AppBiometricHandler(context) }

    val biometricPrompt = biometricHandler.createBiometricPrompt(
        context as FragmentActivity,
        onSuccess = {
            viewModel.event(LoginScreenViewModel.Event.LoginButtonClicked)
        },
        onError = { errorCode, message ->
            Toast.makeText(
                context, context.getString(R.string.biometric_authentication_error_errorcode_message, errorCode, message), Toast.LENGTH_SHORT
            ).show()
        },
        onFailed = {
            Toast.makeText(
                context,
                context.getString(R.string.biometric_authentication_failed), Toast.LENGTH_SHORT //TODO
            ).show()
        }
    )

    val promptInfo = remember {
        biometricHandler.createPromptInfo(
            title = "Biometric Authentication",
            subtitle = "Log in using your biometric credential"
        )
    }


    LaunchedEffect(Unit) {
        var isNavigated = false
        viewModel.effect.debounce(250).collectLatest { effect ->
            when (effect) {
                is LoginScreenViewModel.Effect.LoginSuccess -> {
                    if (!isNavigated) { // Prevent multiple navigation if effect was emitted multiple times
                        isNavigated = true
                        navController?.navigate(FeatureBoneRoutes.BoneScreen) {
                            popUpTo(FeatureBoneRoutes.LoginScreen) {
                                inclusive = true
                            }
                        }
                    }
                }

                is LoginScreenViewModel.Effect.RequireWeakLogin -> {
                    Toast.makeText(
                        context,
                        "Need simple login (code or biometric)",  //TODO as business requirement
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                is LoginScreenViewModel.Effect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT)
                        .show() //TODO if need to show better error
                }
            }
        }
    }

    if (!state.value.isAlreadyLogged) {
        with(localTransitionScope) {
            val modifierToTransition = Modifier.sharedBounds(
                this.rememberSharedContentState("bone"),
                animatedContentScope,
            )
            LoginScreen(
                modifier = modifier,
                modifierToTransition = modifierToTransition,
                state = state,
                event = viewModel::event,
                biometricCallback = { biometricHandler.authenticate(biometricPrompt, promptInfo) },
                isBiometricAvailable = biometricHandler.canAuthenticate() == BiometricAvailability.Available,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    modifierToTransition: Modifier = Modifier,
    state: State<LoginScreenViewModel.LoginScreenState> = mutableStateOf(LoginScreenViewModel.LoginScreenState()),
    event: (LoginScreenViewModel.Event) -> Unit = {},
    biometricCallback: () -> Unit = {},
    isBiometricAvailable: Boolean = false,
) {

    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            AppText(
                modifier = Modifier,
                value = "Bone",
                appTextConfig = appTextConfig(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold
                )
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppText(
                    value = "Welcome",
                    appTextConfig = appTextConfig(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )

                AppText(
                    value = "Log to your account to continue",
                    appTextConfig = appTextConfig(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppOutlinedTextField(
                        value = state.value.email,
                        onValueChange = {
                            event.invoke(LoginScreenViewModel.Event.EmailChanged(it))
                        },
                        labelRes = R.string.email,
                        outlinedTextConfig = appEditOutlinedTextConfig(
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                    )

                    AppOutlinedTextField(
                        value = state.value.password,
                        onValueChange = {
                            event.invoke(LoginScreenViewModel.Event.PasswordChanged(it))
                        },
                        labelRes = R.string.password,
                        outlinedTextConfig = appEditOutlinedTextConfig(
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { event.invoke(LoginScreenViewModel.Event.PasswordVisibilityToggled) },
                                    content = {
                                        Icon(
                                            if (state.value.passwordVisible) Icons.Default.Info else Icons.Default.Face,
                                            contentDescription = if (state.value.passwordVisible) "Скрыть пароль" else "Показать пароль",
                                        )
                                    }
                                )
                            },
                            visualTransformation = if (state.value.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = state.value.rememberMe,
                            onCheckedChange = {
                                event.invoke(LoginScreenViewModel.Event.RememberMeChanged(it))
                            },
                        )
                        AppText(
                            value = "Remember me",
                            appTextConfig = appTextConfig(
                                fontSize = 14.sp,
                            )
                        )
                    }
                    Row {
                        Button(
                            onClick = {
//                                isLoading = true
                                event.invoke(LoginScreenViewModel.Event.LoginButtonClicked)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .then(
                                    modifierToTransition
                                ),
                            shape = MaterialTheme.shapes.extraLarge,
                            enabled = if (
                                state.value.email.isNotEmpty() &&
                                state.value.password.isNotEmpty()
                            ) true else false,
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                AppText(
                                    value = "Log in",
                                    appTextConfig = appTextConfig(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                )
                            }
                        }
//                        if (isBiometricAvailable) {
                        IconButton(
                            onClick = { biometricCallback.invoke() },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(48.dp),
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.fingerprint),
                                    contentDescription = "Biometric Login",
                                    tint = LocalContentColor.current.copy(alpha = 0.6f),
                                    modifier = Modifier.size(36.dp),
                                )
                            }
                        )
//                        }
                    }
                }
            }

            if (state.value.isCreatingPossible) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                    )
                    Text(
                        text = "или",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 14.sp
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                    )
                }


                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    AppText(
                        value = "Create an account",
                        appTextConfig = appTextConfig(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                    )
                }
            }

            // Информация о компании
            Column(
                modifier = Modifier.padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText(
                    value = "Интегрированное управление цепочкой поставок",
                    appTextConfig = appTextConfig(
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    ),
                )
                AppText(
                    value = "Швейцария • Восточная Европа",
                    appTextConfig = appTextConfig(
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun LoginScreenPreview() {
    FeatureTheme(darkTheme = true) {
        LoginScreen()
    }
}