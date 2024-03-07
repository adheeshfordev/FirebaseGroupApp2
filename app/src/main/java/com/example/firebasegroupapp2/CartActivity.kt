package com.example.firebasegroupapp2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasegroupapp2.databinding.ActivityCartBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private var adapter: CartAdapter? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val i = Intent(this, ProductActivity::class.java)
            startActivity(i)
        }

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_cart)

        val query = FirebaseDatabase.getInstance().reference.child("trinketStore").child("cartItem")
        val options = FirebaseRecyclerOptions.Builder<CartItem>().setQuery(query, CartItem::class.java).build()
        adapter = CartAdapter(options)

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

    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}