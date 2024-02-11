package com.example.section1_group2_project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val btnCheckout: Button  = findViewById<Button>(R.id.addToCheckout)
        btnCheckout.setOnClickListener {
            val i = Intent(it.context, CheckoutActivity::class.java)
            it.context.startActivity(i)
        }
        val returnToProducts: Button = findViewById<Button>(R.id.returnToProducts)
        returnToProducts.setOnClickListener {
            val i = Intent(it.context, ProductActivity::class.java)
            it.context.startActivity(i)
        }


    }
}