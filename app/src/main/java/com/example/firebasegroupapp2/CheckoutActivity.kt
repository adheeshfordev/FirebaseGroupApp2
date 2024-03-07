package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        var uid = ""
        if (currentUser == null) {
            val i = Intent(this, ProductActivity::class.java)
            startActivity(i)
        } else {
            uid = currentUser.uid
        }

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val phone = findViewById<EditText>(R.id.phone)
        val street = findViewById<EditText>(R.id.street)
        val city = findViewById<EditText>(R.id.city)
        val country = findViewById<EditText>(R.id.country)

        //https://kotlinlang.org/docs/strings.html#string-templates
        val shippingAddress = "${name.text}, ${street.text}, ${city.text}, $country.text email:${email.text}, phone: $phone.text"


        val checkoutBtn: Button = findViewById(R.id.checkoutBtn)
        checkoutBtn.setOnClickListener {
            val cartRef = FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/details")
            cartRef.get()
                .addOnSuccessListener {
                    val cart = it.getValue(Cart::class.java)
                    val orderId = UUID.randomUUID().toString()
                    val cartItemRef =
                        FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/cartItems")
                    cartItemRef.get().addOnSuccessListener {
                        val cartItemData = it.children
                        for (cartItemSnapshot in cartItemData) {
                            val cartItem: CartItem? =
                                cartItemSnapshot.getValue(CartItem::class.java)

                            val orderItem = OrderItem(orderId,"Product 1",
                                cartItem!!.qty,
                                cartItem.price, cartItem.total
                            )
                            FirebaseDatabase.getInstance().reference
                                .child("trinketStore/order/$orderId/orderItem").push().setValue(orderItem)
                            cartItemRef.child(cartItemSnapshot.key!!).removeValue()

                        }
                    }.addOnFailureListener {
                        Log.e("Firebase Error", "Issues fetching cart item data from firebase")
                    }
                    val order =
                        Order(uid, orderId, cart!!.qty, shippingAddress, cart.total)
                    FirebaseDatabase.getInstance().reference.child("trinketStore/order/$orderId/details")
                        .setValue(order)
                    cartRef.removeValue()
                }.addOnFailureListener {
                    Log.e("Firebase Error", "Issues fetching cart data from firebase")

                }
            val i = Intent(it.context, CheckoutSuccessfulActivity::class.java)
            it.context.startActivity(i)
        }


    }
}