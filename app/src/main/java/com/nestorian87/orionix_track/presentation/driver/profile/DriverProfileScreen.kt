package com.nestorian87.orionix_track.presentation.driver.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nestorian87.orionix_track.R
import com.nestorian87.orionix_track.presentation.common.components.OrionixErrorPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixLabeledTextField
import com.nestorian87.orionix_track.presentation.common.components.OrionixLoadingDialog
import com.nestorian87.orionix_track.presentation.common.components.OrionixPanel
import com.nestorian87.orionix_track.presentation.common.components.OrionixPrimaryButton
import com.nestorian87.orionix_track.presentation.common.components.OrionixSecondaryButton
import com.nestorian87.orionix_track.presentation.common.components.OrionixStatePanel

@Composable
fun DriverProfileScreen(
    contentPadding: PaddingValues,
    isLogoutLoading: Boolean,
    onLogout: () -> Unit,
    viewModel: DriverProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item {
            ProfileHeader()
        }
        if (uiState.isLoading && uiState.profile == null) {
            item {
                OrionixStatePanel(
                    title = stringResource(R.string.loading_text),
                    isLoading = true
                )
            }
        } else {
            item {
                OrionixPanel {
                    OrionixLabeledTextField(
                        label = stringResource(R.string.profile_name_label),
                        value = uiState.name,
                        onValueChange = viewModel::onNameChanged,
                        placeholder = stringResource(R.string.profile_name_placeholder)
                    )
                    OrionixLabeledTextField(
                        label = stringResource(R.string.profile_surname_label),
                        value = uiState.surname,
                        onValueChange = viewModel::onSurnameChanged,
                        placeholder = stringResource(R.string.profile_surname_placeholder)
                    )
                    ProfileField(
                        label = stringResource(R.string.profile_email_label),
                        value = uiState.profile?.email.orEmpty().ifBlank { stringResource(R.string.placeholder_dash) }
                    )
                    ProfileField(
                        label = stringResource(R.string.profile_company_label),
                        value = uiState.profile?.company?.name.orEmpty().ifBlank { stringResource(R.string.placeholder_dash) }
                    )
                    ProfileFeedback(uiState = uiState)
                    OrionixPrimaryButton(
                        text = stringResource(R.string.action_save_profile),
                        onClick = viewModel::saveProfile,
                        enabled = uiState.hasChanges && !uiState.isSaving && !uiState.isLoading
                    )
                    OrionixSecondaryButton(
                        text = stringResource(R.string.action_log_out),
                        onClick = onLogout,
                        enabled = !isLogoutLoading && !uiState.isSaving
                    )
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }

    OrionixLoadingDialog(
        isVisible = uiState.isSaving,
        message = stringResource(R.string.profile_saving)
    )
}

@Composable
private fun ProfileHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.profile_tab_title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.profile_tab_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileFeedback(uiState: DriverProfileUiState) {
    when {
        uiState.isLoading -> OrionixStatePanel(
            title = stringResource(R.string.loading_text),
            isLoading = true
        )
        uiState.validationError != null -> OrionixStatePanel(
            title = stringResource(R.string.error_title),
            message = stringResource(R.string.profile_required_fields),
            isError = true
        )
        uiState.error != null -> OrionixErrorPanel(error = uiState.error)
        uiState.isSaved -> OrionixStatePanel(
            title = stringResource(R.string.profile_saved)
        )
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
