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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class CartAdapter(options: FirebaseRecyclerOptions<CartItem>) :
    FirebaseRecyclerAdapter<CartItem, CartAdapter.MyViewHolder>(options) {

    private lateinit var auth: FirebaseAuth
    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.cart_row_layout, parent, false)) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.name)
        val price: TextView = itemView.findViewById(R.id.price)
        val qty: TextView = itemView.findViewById(R.id.qty)
        val removeFromCart: Button = itemView.findViewById(R.id.removeFromCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: CartItem) {

        val pid = model.pid
        val productDb = FirebaseDatabase.getInstance().reference.child("trinketStore/products/$pid")

        holder.removeFromCart.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid?:""
            val cartItemRef =
                FirebaseDatabase.getInstance().reference.child("trinketStore/cart/$uid/cartItems")
            cartItemRef.get().addOnSuccessListener {
                val cartItemData = it.children
                for (cartItemSnapshot in cartItemData) {
                    val cartItem = cartItemSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null && cartItem.pid == model.pid) {
                        if (cartItemSnapshot != null && cartItemSnapshot.key != null) {
                            //As suggested by Android Studio
                            cartItemRef.child(cartItemSnapshot.key!!).removeValue()
                            val i = Intent(holder.itemView.context, CartActivity::class.java)
                            holder.itemView.context.startActivity(i)
                        }
                    }
                }
                Common.calculateCartTotals(uid)
            }.addOnFailureListener {

            }
        }



        productDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                holder.productName.text = product?.name

                //Formatted as suggested by Android Studio for suppressing all warnings
                "${String.format("%.2f", product?.price)} CAD".also { holder.price.text = it }
                "Qty: ${model.qty}".also { holder.qty.text = it }

                val theImage = product?.img

                if (!theImage.isNullOrEmpty()) {
                    if (theImage.indexOf("gs://") > -1) {
                        val storageReference =
                            FirebaseStorage.getInstance().getReferenceFromUrl(theImage)
                        Glide.with(holder.productImage.context).load(storageReference)
                            .into(holder.productImage)
                    } else {
                        Glide.with(holder.productImage.context).load(theImage)
                            .into(holder.productImage)
                    }
                }

                holder.productImage.setOnClickListener {
                    val i = Intent(it.context, DetailActivity::class.java)
                    i.putExtra("productImg", theImage)
                    i.putExtra("name", product?.name)
                    i.putExtra("price", product?.price)
                    i.putExtra("pid", product?.pid)
                    i.putExtra("description", product?.description)
                    it.context.startActivity(i)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("MyApp", "Upload cancelled")
            }
        })
    }

}