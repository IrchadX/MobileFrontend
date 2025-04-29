
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobileuser_frontend.state.UiState
import com.example.mobileuser_frontend.viewmodel.AuthViewModel
import com.example.mobileuser_frontend.viewmodel.PairingViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAidant( navController: NavController,
               viewModel: PairingViewModel = viewModel(), authViewModel: AuthViewModel = viewModel()) {
    val codeState = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var user_id by remember { mutableStateOf(0) }

    val stateFlow: StateFlow<UiState<String>> = viewModel.state
    val state = stateFlow.collectAsState().value

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val stringId = authViewModel.authRepository.getUserId().firstOrNull()
        user_id = stringId?.toIntOrNull() ?: 0
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Instruction text
            Text(
                text = "Veuillez entrer le code de l'aidant:",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp));
                // TextField for code input
            TextField(
                value = codeState.value,
                onValueChange = { codeState.value = it },
                label =  { Text("Code aidant") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                singleLine = true
            )

            // Submit button
            Button(
                onClick = {
                    if (codeState.value.isNotBlank()) {
                        viewModel.pair(codeState.value, user_id)

                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Veuillez entrer un code valide")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Envoyer")
            }
            when (state) {
                is UiState.Idle -> {

                }

                is UiState.Loading -> {
                    // Show loading indicator
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    // Show success message
                    val message = (state as UiState.Success<String>).data
                    Text("Pairing successful: $message")
                }

                is UiState.Error -> {
                    // Show error message
                    val errorMessage = (state as UiState.Error).message
                    Text("Error: $errorMessage", color = Color.Red)
                }
        }
    }
}
    }
