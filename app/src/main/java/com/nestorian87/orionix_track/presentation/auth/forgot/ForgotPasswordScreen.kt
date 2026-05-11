package com.nestorian87.orionix_track.presentation.auth.forgot

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.presentation.common.components.OrionixBackdrop
import com.nestorian87.orionix_track.presentation.common.components.OrionixLabeledTextField
import com.nestorian87.orionix_track.presentation.common.components.OrionixLoadingDialog
import com.nestorian87.orionix_track.presentation.common.components.OrionixPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixPrimaryButton
import com.nestorian87.orionix_track.presentation.common.components.OrionixSecondaryButton
import com.nestorian87.orionix_track.presentation.common.components.OrionixStatePanel
import com.nestorian87.orionix_track.presentation.common.mapper.toDefaultText

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorText = when {
        uiState.showRequiredFieldsError -> stringResource(R.string.error_forgot_email_required)
        else -> uiState.error?.toDefaultText()
    }

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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OrionixPanel {
                    Text(
                        text = stringResource(R.string.forgot_password_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.forgot_password_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OrionixLabeledTextField(
                        label = stringResource(R.string.field_email),
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        placeholder = stringResource(R.string.placeholder_email),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )
                    if (uiState.isSent) {
                        OrionixStatePanel(title = stringResource(R.string.forgot_password_sent))
                    }
                    errorText?.let {
                        OrionixStatePanel(
                            title = stringResource(R.string.error_unexpected),
                            message = it,
                            isError = true
                        )
                    }
                    OrionixPrimaryButton(
                        text = stringResource(R.string.action_send_reset_link),
                        onClick = viewModel::sendResetLink,
                        enabled = !uiState.isLoading
                    )
                    OrionixSecondaryButton(
                        text = stringResource(R.string.action_back_to_login),
                        onClick = onBack,
                        enabled = !uiState.isLoading
                    )
                }
            }
        }
        OrionixLoadingDialog(isVisible = uiState.isLoading)
    }
}
