package com.example.firebasegroupapp2

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

//https://www.baeldung.com/kotlin/static-methods#companion-objects
class Common {
    companion object {
        fun addToCart(uid: String?, product: Product, qty: Int = 1) {
            if (!uid.isNullOrEmpty()) {
                val cartId = UUID.randomUUID().toString()
                val cartRef =
                    FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/details")
                cartRef.setValue(Cart(cartId, 0.00, uid, 0))
                val cartItemRef =
                    FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/cartItems")

                cartItemRef.child(product.pid).get().addOnSuccessListener {
                    val cartItem = it.getValue(CartItem::class.java)
                    if (cartItem != null && cartItem.pid.isNotEmpty()) {

                        val newQty = cartItem.qty + qty
                        val newTotal = cartItem.total + product.price * qty
                        cartItem.qty = newQty
                        cartItem.total = newTotal
                        cartItemRef.child(product.pid).setValue(cartItem)

                    } else {
                        cartItemRef.child(product.pid).setValue(
                            CartItem(
                                cartId,
                                product.name,
                                product.price * qty, qty, product.pid, product.price
                            )
                        )
                    }
                    calculateCartTotals(uid)
                }
            }
        }

        fun calculateCartTotals(
            uid: String?
        ) {
            val cartRef =
                FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/details")
            val cartItemRef =
                FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/cartItems")
            cartItemRef.get().addOnFailureListener {
                Log.e("Firebase error", "Issue with fetching cart item data")
            }.addOnSuccessListener {
                var totalQty = 0
                var totalPrice = 0.00
                val cartItems = it.children
                for (cartItemSnapshot in cartItems) {
                    val cartItem: CartItem? = cartItemSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null) {
                        totalQty += cartItem.qty
                        totalPrice += cartItem.total
                    }
                }
                cartRef.child("total").setValue(totalPrice)
                cartRef.child("qty").setValue(totalQty)
            }
        }

        fun signOut(auth:FirebaseAuth, it: View) {
            auth.signOut()
            Toast.makeText(it.context, "Logged out successfully", Toast.LENGTH_LONG).show()
            val i = Intent(it.context, ProductActivity::class.java)
            it.context.startActivity(i)
        }
    }
}