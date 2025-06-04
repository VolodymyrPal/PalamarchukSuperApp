package com.hfad.palamarchuksuperapp.feature.bone.ui.login

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.FeatureTheme
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppOutlinedTextField
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.AppText
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appColors
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appEditOutlinedTextConfig
import com.hfad.palamarchuksuperapp.core.ui.composables.basic.appTextConfig
import com.hfad.palamarchuksuperapp.feature.bone.R
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.LocalNavAnimatedVisibilityScope
import com.hfad.palamarchuksuperapp.feature.bone.ui.screens.LocalSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreenRoot(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
) {
    val localTransitionScope = LocalSharedTransitionScope.current
        ?: error(IllegalStateException("No SharedElementScope found"))
    val animatedContentScope = LocalNavAnimatedVisibilityScope.current
        ?: error(IllegalStateException("No AnimatedVisibility found"))

    with(localTransitionScope) {
        val modifierToTransition = Modifier.sharedBounds(
            this.rememberSharedContentState("bone"),
            animatedContentScope,
        )
        LoginScreen(
            modifier = modifier,
            onLoginClick = onLoginClick,
            onForgotPasswordClick = onForgotPasswordClick,
            onSignUpClick = onSignUpClick,
            modifierToTransition = modifierToTransition
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    modifierToTransition: Modifier = Modifier,
) {
    var state: LoginScreenState = LoginScreenState()

    var passwordVisible by remember { mutableStateOf(true) } //TODO TEST ONLY
    var isLoading by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
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
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    AppOutlinedTextField(
                        value = state.email,
                        onValueChange = {
                            state = state.copy(email = it)
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
                        shape = MaterialTheme.shapes.medium
                    )

                    AppOutlinedTextField(
                        value = state.password,
                        onValueChange = { state = state.copy(password = it) },
                        labelRes = R.string.password,
                        outlinedTextConfig = appEditOutlinedTextConfig(
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.Info else Icons.Default.Face,
                                        contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль",
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = MaterialTheme.shapes.medium,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                        )
                        AppText(
                            value = "Remember me",
                            appTextConfig = appTextConfig(
                                fontSize = 14.sp,
                            )
                        )

                        AppText(
                            value = "Password recovery",
                            appTextConfig = appTextConfig(
                                fontSize = 14.sp,
                                textAlign = TextAlign.End,
                            ),
                            modifier = Modifier
                                .clickable { onForgotPasswordClick() }
                                .weight(1f),

                            )
                    }
                    Row {
                        Button(
                            onClick = {
//                                isLoading = true
                                onLoginClick(state.email, state.password)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .then(
                                    modifierToTransition
                                ),
                            shape = MaterialTheme.shapes.extraLarge,
                            enabled = !isLoading && state.email.isNotBlank() && state.password.isNotBlank(),
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
                        IconButton(
                            onClick = { /* Handle user's fingerprints to log in */ },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(48.dp),
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.fingerprint),
                                    contentDescription = "Info",
                                    tint = LocalContentColor.current.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .size(36.dp),
                                )
                            }
                        )
                    }
                }
            }

            if (state.isCreatingPossible) {

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
                    onClick = onSignUpClick,
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

data class LoginScreenState(
    var email: String = "Test", //TODO remove test var / data
    var password: String = "Test", //TODO remove test var / data
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val isCreatingPossible: Boolean = false,
)

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FeatureTheme(darkTheme = false) {
        LoginScreen()
    }
}