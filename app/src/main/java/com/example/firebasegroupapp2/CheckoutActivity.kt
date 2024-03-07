package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.firebaseprojectgroup2.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class CheckoutActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val i = Intent(this, ProductActivity::class.java)
            startActivity(i)
        }

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val phone = findViewById<EditText>(R.id.phone)
        val street = findViewById<EditText>(R.id.street)
        val city = findViewById<EditText>(R.id.city)
        val country = findViewById<EditText>(R.id.country)

        //https://kotlinlang.org/docs/strings.html#string-templates
        val shippingAddress = "${name}, ${street}, ${city}, $country email:${email}, phone: $phone"


        val checkoutBtn: Button = findViewById(R.id.checkoutBtn)
        checkoutBtn.setOnClickListener {
            val cartRef = FirebaseDatabase.getInstance().reference.child("trinketStore/cart")
            cartRef.get()
                .addOnSuccessListener {
                    val cart = it.getValue(Cart::class.java)
                    val cartItemRef =
                        FirebaseDatabase.getInstance().reference.child("trinketStore/cartItem")
                    cartItemRef.get().addOnSuccessListener {
                        val cartItemData = it.children
                        for (cartItemSnapshot in cartItemData) {
                            val cartItem: CartItem? =
                                cartItemSnapshot.getValue(CartItem::class.java)

                            val orderItem = OrderItem(
                                UUID.randomUUID().toString(), cartItem!!.qty,
                                cartItem.price, cartItem.total
                            )
                            FirebaseDatabase.getInstance().reference
                                .child("trinketStore/orderItem").push().setValue(orderItem)
                            cartItemRef.child(cartItemSnapshot.key!!).removeValue()

                        }
                    }.addOnFailureListener {

                    }
                    val order =
                        Order(UUID.randomUUID().toString(), cart!!.qty, shippingAddress, cart.total)
                    FirebaseDatabase.getInstance().reference.child("trinketStore/order")
                        .push().setValue(order)
                    cartRef.child(it.key!!).removeValue()
                }.addOnFailureListener {

                }
            val i = Intent(it.context, CheckoutSuccessfulActivity::class.java)
            it.context.startActivity(i)
        }

        val browseProductBtn: Button = findViewById(R.id.browseProductsBtn)
        browseProductBtn.setOnClickListener {

            val i = Intent(it.context, ProductActivity::class.java)
            it.context.startActivity(i)
        }

        val goToCartBtn: Button = findViewById(R.id.goToCartBtn)
        goToCartBtn.setOnClickListener {

            val i = Intent(it.context, CartActivity::class.java)
            it.context.startActivity(i)
        }
    }
}