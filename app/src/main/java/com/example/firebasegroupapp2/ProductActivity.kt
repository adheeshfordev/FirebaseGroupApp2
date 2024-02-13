package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class ProductActivity : AppCompatActivity() {

    private var adapter: ProductAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val query = FirebaseDatabase.getInstance().reference.child("trinketStore").child("products")
        val options = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
        adapter = ProductAdapter(options)

        val rView: RecyclerView = findViewById(R.id.rView)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter

        val goToCart = findViewById<Button>(R.id.goToCart)
        goToCart.setOnClickListener {
            val i = Intent(it.context, CartActivity::class.java)
            it.context.startActivity(i)
        }

    }

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract())
    { result ->
        this.onSignInResult(result)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val query = FirebaseDatabase.getInstance().reference.child("trinketStore").child("products")
            val options = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
            adapter = ProductAdapter(options)

            val rView: RecyclerView = findViewById(R.id.rView)
            rView.layoutManager = LinearLayoutManager(this)
            rView.adapter = adapter
        } else {
            createSignInIntent()
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)

    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}