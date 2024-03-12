package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

class ProductActivity : AppCompatActivity() {

    private var adapter: ProductAdapter? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        loadUI()
        val logout: Button = findViewById(R.id.logoutBtn)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser?.uid.isNullOrEmpty()) {
            logout.visibility = View.GONE
        } else {
            logout.visibility = View.VISIBLE
            val welcomeTxt:TextView = findViewById(R.id.welcomeTxt)
            "Welcome ${auth.currentUser?.displayName}".also { welcomeTxt.text = it }
        }


        logout.setOnClickListener {
            Common.signOut(auth,it)
        }
    }

    private fun loadUI() {
        val query = FirebaseDatabase.getInstance().reference.child("trinketStore").child("products")
        val options =
            FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
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

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}