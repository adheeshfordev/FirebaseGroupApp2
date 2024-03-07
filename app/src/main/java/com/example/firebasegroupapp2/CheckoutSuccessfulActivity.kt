package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CheckoutSuccessfulActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout_successful)

        val backToHomeBtn: Button = findViewById(R.id.backToHomeBtn)
        backToHomeBtn.setOnClickListener {

            val i = Intent(it.context, ProductActivity::class.java)
            it.context.startActivity(i)
        }
    }
}
