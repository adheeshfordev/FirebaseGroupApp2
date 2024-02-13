package com.example.firebaseprojectgroup2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.Button
import com.example.firebaseprojectgroup2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goToProducts:Button = findViewById<Button>(R.id.goToProducts)
        goToProducts.setOnClickListener {
            val i = Intent(this, ProductActivity::class.java)
            startActivity(i)
        }
    }

}