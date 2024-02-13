package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val phone = findViewById<EditText>(R.id.phone)
        val street = findViewById<EditText>(R.id.street)
        val city = findViewById<EditText>(R.id.city)
        val country = findViewById<EditText>(R.id.country)

        //https://kotlinlang.org/docs/strings.html#string-templates
        val shippingAddress = "${name}, ${street}, ${city}, ${country} email:${email}, phone: ${phone}"


        val checkoutBtn: Button = findViewById(R.id.checkoutBtn)
        checkoutBtn.setOnClickListener {

            val i = Intent(it.context, ProductActivity::class.java)
            /*FirebaseDatabase.getInstance().reference.orderByValue().
            val order = Order(UUID.randomUUID().toString(), qty, shippingAddress, total)
            val orderItem = OrderItem(UUID.randomUUID().toString(), qty, price,total);
            FirebaseDatabase.getInstance().reference.child("trinketStore/order").push().setValue(order)
            FirebaseDatabase.getInstance().reference.child("trinketStore/orderItem").push().setValue(orderItem)*/
            Toast.makeText(it.context, "Checkout completed successfully", Toast.LENGTH_LONG).show()
            it.context.startActivity(i)
        }
    }
}