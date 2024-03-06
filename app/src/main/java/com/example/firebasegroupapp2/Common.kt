package com.example.firebasegroupapp2

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

//https://www.baeldung.com/kotlin/static-methods#companion-objects
class Common {
    companion object {
        fun addToCart(model: Product) {
            FirebaseDatabase.getInstance().reference.child("trinketStore/cart").get()
                .addOnSuccessListener {
                    val cartData = it.value
                    val cart: Cart?
                    val cartWrapper: Map<String, Any> = cartData as Map<String, Any>
                    for ((cartKey, cartMap) in cartWrapper) {
                        cart = Cart()
                        for ((field, value) in (cartMap as HashMap<String, Any>)) {

                            if (field == "cartId") {
                                cart.cartId = value as String
                            }
                            if (field == "total") {
                                cart.total = value as Double
                            }
                        }

                        break
                    }

                    var productInCart: CartItem? = null
                    val cartItemRef =
                        FirebaseDatabase.getInstance().reference.child("trinketStore/cartItem")
                    cartItemRef.get().addOnSuccessListener {
                        val cartItemData = it.children
                        for (cartItemSnapshot in cartItemData) {
                            val cartItem: CartItem? =
                                cartItemSnapshot.getValue(CartItem::class.java)

                            if (cartItem != null && cartItem.pid == model.pid) {
                                productInCart = cartItem
                                val cartItemKey = cartItemSnapshot.key ?: ""
                                if (cartItemSnapshot.key != null) {
                                    cartItemRef.child(cartItemKey).child("qty")
                                        .setValue(cartItem.qty + 1)
                                    break
                                }

                            }
                            if (productInCart != null) {
                                break
                            }
                        }

                        if (productInCart == null) {
                            val cartItem = CartItem(
                                UUID.randomUUID().toString(),
                                model.price,
                                1,
                                model.pid,
                                model.price
                            )
                            cartItemRef.push().setValue(cartItem)
                        }

                    }.addOnFailureListener {
                        Log.e("FirebaseError", "Could not parse cart item data in Firebase")
                    }

                }.addOnFailureListener {
                    Log.e("FirebaseError", "Could not parse cart data in Firebase")
                }
        }

    }
}