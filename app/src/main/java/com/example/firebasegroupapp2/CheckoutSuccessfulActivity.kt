package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class CheckoutSuccessfulActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout_successful)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val i = Intent(this, ProductActivity::class.java)
            startActivity(i)
        }

        val backToHomeBtn: Button = findViewById(R.id.backToHomeBtn)
        backToHomeBtn.setOnClickListener {

            val i = Intent(it.context, ProductActivity::class.java)
            it.context.startActivity(i)
        }

        val logout: Button = findViewById(R.id.logoutBtn)
        logout.setOnClickListener {
            Common.signOut(auth,it)
        }
    }
}
