package com.example.firebasegroupapp2

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProductAdapter(options: FirebaseRecyclerOptions<Product>) :
    FirebaseRecyclerAdapter<Product, ProductAdapter.MyViewHolder>(options) {
    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.product_row_layout, parent, false)) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.name)
        val price: TextView = itemView.findViewById(R.id.price)
        val viewDetails: Button = itemView.findViewById(R.id.viewDetails)
        val addToCart: Button = itemView.findViewById(R.id.addToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Product) {
        holder.productName.text = model.name
        "${String.format("%.2f", model.price)} CAD".also { holder.price.text = it }
        val theImage = model.img

        if (theImage.indexOf("gs://") > -1) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(theImage)
            Glide.with(holder.productImage.context).load(storageReference).into(holder.productImage)
        } else {
            Glide.with(holder.productImage.context).load(theImage).into(holder.productImage)
        }
        holder.viewDetails.setOnClickListener {
            val i = Intent(it.context, DetailActivity::class.java)
            i.putExtra("productImg", theImage)
            i.putExtra("name", model.name)
            i.putExtra("price", model.price)
            i.putExtra("pid", model.pid)
            it.context.startActivity(i)
        }
        holder.addToCart.setOnClickListener {
            val i = Intent(it.context, CartActivity::class.java)

            Common.addToCart(model)

            it.context.startActivity(i)
        }
    }

    private fun addToCart(model: Product) {
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