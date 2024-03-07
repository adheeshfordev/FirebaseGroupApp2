package com.example.firebasegroupapp2

import android.content.Intent
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

import com.google.firebase.storage.FirebaseStorage

class ProductAdapter(options: FirebaseRecyclerOptions<Product>) :
    FirebaseRecyclerAdapter<Product, ProductAdapter.MyViewHolder>(options) {
    private lateinit var auth: FirebaseAuth
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
            i.putExtra("description", model.description)
            it.context.startActivity(i)
        }
        holder.addToCart.setOnClickListener {
            val i = Intent(it.context, CartActivity::class.java)
            auth = FirebaseAuth.getInstance()
            Common.addToCart(auth.currentUser?.uid, model)

            it.context.startActivity(i)
        }
    }
}