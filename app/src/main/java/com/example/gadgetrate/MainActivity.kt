package com.example.gadgetrate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gadgetrate.auth.LoginActivity
import com.example.gadgetrate.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        // Retrieve the NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.adminDashboard, R.id.userDashboard, R.id.viewAllProducts, R.id.viewAllReviewFragment, R.id.viewAllProductsUser), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Check if there is a destination passed in the intent
        intent?.extras?.getInt("destination")?.let { destination ->
            navController.navigate(destination)
        }

        // Fetch user role and setup navigation view
        fetchUserRole { role ->
            userRole = role
            setupNavigationView(navView, drawerLayout, navController)
        }
    }

    private fun fetchUserRole(callback: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("userType") ?: "user"
                    callback(role)
                }
                .addOnFailureListener {
                    callback("user")
                }
        } else {
            callback("user")
        }
    }

    private fun setupNavigationView(navView: NavigationView, drawerLayout: DrawerLayout, navController: NavController) {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (userRole == "admin") {
                        navController.navigate(R.id.adminDashboard)
                    } else {
                        navController.navigate(R.id.userDashboard)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.userProfileFragment -> {
                    navController.navigate(R.id.userProfileFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.viewAllProducts -> {
                    if (userRole == "admin") {
                        navController.navigate(R.id.viewAllProducts)
                    } else {
                        navController.navigate(R.id.viewAllProductsUser)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.viewAllReviewFragment -> {
                    navController.navigate(R.id.viewAllReviewFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
