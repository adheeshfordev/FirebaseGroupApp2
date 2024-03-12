package com.example.firebasegroupapp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasegroupapp2.databinding.ActivityCartBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private var adapter: CartAdapter? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding = ActivityCartBinding.inflate(layoutInflater)
            setContentView(binding.root)
            loadUI()
        } else {
            createSignInIntent()
        }
    }

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract())
    { result ->
        this.onSignInResult(result)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val i = Intent(this, ProductActivity::class.java)
            startActivity(i)
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

    private fun loadUI() {
        var uid = ""
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val i = Intent(this, ProductActivity::class.java)
            startActivity(i)
        } else {
            uid = currentUser.uid
        }
        val query =
            FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/cartItems")
        val options =
            FirebaseRecyclerOptions.Builder<CartItem>().setQuery(query, CartItem::class.java)
                .build()
        adapter = CartAdapter(options)

        FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/details").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cart = snapshot.getValue(Cart::class.java)
                val totalTxt = findViewById<TextView>(R.id.total)
                val totalItemsTxt = findViewById<TextView>(R.id.totalItems)
                "Total Items: ${String.format("%.2f", cart?.total)} CAD".also { totalTxt.text = it }
                "Grand Total: ${String.format("%d", cart?.qty)}".also { totalItemsTxt.text = it }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("MyApp", "Upload cancelled")
            }
        })

        val rView: RecyclerView = findViewById(R.id.cartRecyclerView)

        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter

        val btnCheckout: Button = findViewById(R.id.goToCheckout)
        btnCheckout.setOnClickListener {
            val i = Intent(it.context, CheckoutActivity::class.java)
            it.context.startActivity(i)
        }
        val returnToProducts: Button = findViewById(R.id.returnToProducts)
        returnToProducts.setOnClickListener {
            val i = Intent(it.context, ProductActivity::class.java)
            it.context.startActivity(i)
        }
        val logout: Button = findViewById(R.id.logout)
        logout.setOnClickListener {
            Common.signOut(auth,it)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}