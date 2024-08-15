package com.example.gadgetrate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gadgetrate.auth.LoginActivity
import com.example.gadgetrate.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Delay of 3 seconds before checking authentication state
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationState()
        }, 3000) // 3000 milliseconds = 3 seconds
    }

    private fun checkAuthenticationState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is logged in, check user type
            val userId = currentUser.uid
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userType = document.getString("userType")
                        if (userType == "user") {
                            navigateToMainActivity(R.id.userDashboard)
                        } else if (userType == "admin") {
                            navigateToMainActivity(R.id.adminDashboard)
                        } else {
                            Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show()
                            navigateToLoginActivity()
                        }
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                        navigateToLoginActivity()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to retrieve user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    navigateToLoginActivity()
                }
        } else {
            // User is not logged in, navigate to LoginActivity
            navigateToLoginActivity()
        }
    }

    private fun navigateToMainActivity(destinationId: Int) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("destination", destinationId)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
