package com.nestorian87.orionix_track.presentation.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.domain.error.AppError
import com.nestorian87.orionix_track.presentation.common.components.OrionixBackdrop
import com.nestorian87.orionix_track.presentation.common.components.OrionixLoadingDialog
import com.nestorian87.orionix_track.presentation.common.components.OrionixLabeledTextField
import com.nestorian87.orionix_track.presentation.common.components.OrionixPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixPrimaryButton
import com.nestorian87.orionix_track.presentation.common.mapper.toDefaultText
import com.nestorian87.orionix_track.presentation.theme.OrionixTrackTheme

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    val errorText = when (uiState.error) {
        is AppError.Validation.InvalidEmailFormat -> stringResource(R.string.error_login_invalid_email)
        is AppError.Auth.InvalidCredentials -> stringResource(R.string.error_login_invalid_credentials)
        else -> uiState.error?.toDefaultText()
    }

    LoginScreenContent(
        uiState = uiState,
        errorText = errorText,
        onEmailChange = loginViewModel::onEmailChanged,
        onPasswordChange = loginViewModel::onPasswordChanged,
        onLoginClick = loginViewModel::login
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    errorText: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val colors = MaterialTheme.colorScheme

    OrionixBackdrop {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OrionixPanel {
                    Text(
                        text = stringResource(R.string.login_card_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = colors.onSurface
                    )
                    Text(
                        text = stringResource(R.string.login_card_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurfaceVariant
                    )

                    OrionixLabeledTextField(
                        label = stringResource(R.string.field_email),
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(R.string.placeholder_email),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OrionixLabeledTextField(
                        label = stringResource(R.string.field_password),
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(R.string.placeholder_password),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onLoginClick()
                            }
                        ),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    errorText?.let {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = colors.error.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.error
                            )
                        }
                    }

                    OrionixPrimaryButton(
                        text = stringResource(R.string.action_sign_in),
                        enabled = !uiState.isLoading && uiState.email.isNotBlank() && uiState.password.isNotBlank(),
                        onClick = {
                            focusManager.clearFocus()
                            onLoginClick()
                        }
                    )
                }
            }
        }

        OrionixLoadingDialog(isVisible = uiState.isLoading)
    }
}

@Preview()
@Composable
private fun LoginPreview() {
    OrionixTrackTheme {
        LoginScreenContent(
            uiState = LoginUiState(email = "test@mail.com"),
            errorText = null,
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {}
        )
    }
}
