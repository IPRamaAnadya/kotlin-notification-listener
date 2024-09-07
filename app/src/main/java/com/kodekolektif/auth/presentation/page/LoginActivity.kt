package com.kodekolektif.auth.presentation.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kodekolektif._core.manager.DeviceInfoManager
import com.kodekolektif._core.network.NetworkResult
import com.kodekolektif.auth.presentation.viewmodel.AuthViewModel
import com.kodekolektif.notiflistener.databinding.ActivityLoginBinding // Import the generated binding class
import com.kodekolektif.notiflistener.presentation.page.MainActivity
import com.kodekolektif.notiflistener.utils.DialogManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: ActivityLoginBinding // Declare the binding variable
    private val deviceInfoManager: DeviceInfoManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        observeLoginState()
    }

    private fun initViews() {
        setupActionBar()

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvSkipLogin.setOnClickListener {
           DialogManager.showAlertDialog(this, "Penting!", "Melewati login membuat service tidak berjalan dengan baik") {
               val intent = Intent(this, MainActivity::class.java)
               startActivity(intent)
               finish()
           }
        }
    }

    private fun setupActionBar() {
        supportActionBar?.title = "Login"
    }

    private fun login() {
        val deviceName = binding.etDeviceName.text.toString().trim() // Get the device name from EditText
        if (deviceName.isNotEmpty()) {
            authViewModel.login(deviceName)
        }
    }

    private fun observeLoginState() {
        authViewModel.loginState.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    // Handle loading
                    Log.e(TAG, "Login loading")
                    binding.btnLogin.text = "Loading..." // Change button text to indicate loading
                    binding.btnLogin.isEnabled = false // Disable the button while loading
                }
                is NetworkResult.Success -> {
                    // Handle successful login
                    val loginResponse = result.data
                    Log.e(TAG, "Login success: $loginResponse")
                    binding.btnLogin.text = "Login" // Reset button text
                    binding.btnLogin.isEnabled = true // Re-enable the button
                    // check if user status is active (2)
                    deviceInfoManager.saveDeviceStatus(loginResponse?.user?.status ?: 0)
                    if (loginResponse?.user?.status == 2) {
                        DialogManager.showAlertDialog(this, "Login berhasil", "Welcome, ${loginResponse.user.name}") {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        DialogManager.showAlertDialog(this, "Login berhasil", "Status device pending. Silahkan hubungi admin agar device dapat menyingkronkan data.") {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                is NetworkResult.Error -> {
                    // Handle error
                    val error = result.errorMessage
                    Log.e(TAG, "Login error: $error")
                    binding.btnLogin.text = "Login" // Reset button text
                    binding.btnLogin.isEnabled = true // Re-enable the button
                    DialogManager.showAlertDialog(this, "Login error", error)
                }
                NetworkResult.Empty -> {
                    Log.e(TAG, "Login empty")
                    binding.btnLogin.text = "Login" // Reset button text
                    binding.btnLogin.isEnabled = true // Re-enable the button
                    DialogManager.showAlertDialog(this, "Login error", "Empty response")
                }
                NetworkResult.NetworkError -> {
                    Log.e(TAG, "Network error")
                    binding.btnLogin.text = "Login" // Reset button text
                    binding.btnLogin.isEnabled = true // Re-enable the button
                    DialogManager.showAlertDialog(this, "Network error", "Please check your internet connection")
                }
                NetworkResult.TimeoutError -> {
                    Log.e(TAG, "Timeout error")
                    binding.btnLogin.text = "Login" // Reset button text
                    binding.btnLogin.isEnabled = true // Re-enable the button
                    DialogManager.showAlertDialog(this, "Timeout error", "Request timeout. Please try again")
                }
            }
        }
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}
