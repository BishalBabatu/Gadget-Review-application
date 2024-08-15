package com.bishal.gadgetrate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bishal.gadgetrate.R
import com.bishal.gadgetrate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.homeBtn.setOnClickListener{
            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
        }




    }
}