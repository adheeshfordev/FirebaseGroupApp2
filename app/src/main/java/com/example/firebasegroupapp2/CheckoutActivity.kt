package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import com.example.firebaseprojectgroup2.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID
import java.util.regex.Pattern

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

        val checkoutBtn: Button = findViewById(R.id.checkoutBtn)
        checkoutBtn.setOnClickListener {
            val hasErrors = validateInput()
            //https://kotlinlang.org/docs/strings.html#string-templates
            val shippingAddress =
                "${name.text}, ${street.text}, ${city.text}, " +
                        "${country.text} email:${email.text}, phone: ${phone.text}"

            if (!hasErrors) {
                createOrder(uid, shippingAddress)
                val i = Intent(it.context, CheckoutSuccessfulActivity::class.java)
                it.context.startActivity(i)
            }
        }
    }

    private fun validateInput() : Boolean {
        var hasErrors = false

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val phone = findViewById<EditText>(R.id.phone)
        val street = findViewById<EditText>(R.id.street)
        val city = findViewById<EditText>(R.id.city)
        val country = findViewById<EditText>(R.id.country)
        val card = findViewById<EditText>(R.id.card)
        val cvv = findViewById<EditText>(R.id.CVV)
        val expiry = findViewById<EditText>(R.id.expiry)


        val requiredFields = listOf(name, email, phone, street, city, country, card,
            cvv, expiry)
        var firstErrorField:EditText? = null
        for (field in requiredFields) {
            if (field.text.isNullOrEmpty()) {
                field.error = "Field is required"
                if (firstErrorField == null) firstErrorField = field
                hasErrors = true
            }
        }

        /* https://developer.android.com/reference/android/util/Patterns */
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text).find()) {
            if (firstErrorField == null) {
                firstErrorField = email
                email.error = "Email format is not valid"
            }
            hasErrors = true
        }

        if (!Patterns.PHONE.matcher(phone.text).find()){
            if (firstErrorField == null) {
                firstErrorField = phone
            }
            phone.error = "Phone format is not valid"
            hasErrors = true
        }
        if (!Pattern.matches("[0-9]{16}", card.text) &&
            !Pattern.matches("[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}", card.text)){
            if (firstErrorField == null) {
                firstErrorField = card
            }
            card.error = "Card format is not valid"
            hasErrors = true
        }
        if (!Pattern.matches("[0-9]{2}/[0-9]{2}", expiry.text)){
            if (firstErrorField == null) {
                firstErrorField = expiry
            }
            expiry.error = "Expiry format is not valid. Use mm/yy"
            hasErrors = true
        }
        firstErrorField?.requestFocus()

        return hasErrors
    }
    private fun createOrder(uid: String, shippingAddress: String) {
        val cartRef =
            FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/details")
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

                        val orderItem = OrderItem(
                            orderId, cartItem?.name?:"Product",
                            cartItem!!.qty,
                            cartItem.price, cartItem.total
                        )
                        FirebaseDatabase.getInstance().reference
                            .child("trinketStore/order/$uid/userOrders/$orderId/orderItems").push()
                            .setValue(orderItem)
                        cartItemRef.child(cartItemSnapshot.key!!).removeValue()

                    }
                }.addOnFailureListener {
                    Log.e("Firebase Error", "Issues fetching cart item data from firebase")
                }
                val order =
                    Order(uid, orderId, cart!!.qty, shippingAddress, cart.total)
                FirebaseDatabase.getInstance().reference.child("trinketStore/order/$uid/userOrders/$orderId/details")
                    .setValue(order)
                cartRef.removeValue()
            }.addOnFailureListener {
                Log.e("Firebase Error", "Issues fetching cart data from firebase")
            }
    }
}