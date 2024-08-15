package com.bishal.gadgetrate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bishal.gadgetrate.R
import com.bishal.gadgetrate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}