package com.example.section1_group2_project1

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
import com.google.firebase.storage.FirebaseStorage

class ProductAdapter(options: FirebaseRecyclerOptions<Product>):
    FirebaseRecyclerAdapter<Product, ProductAdapter.MyViewHolder>(options) {
    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.product_row_layout, parent, false)) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.name)
        val price: TextView = itemView.findViewById(R.id.price)
        val viewDetails: Button = itemView.findViewById(R.id.viewDetails)
        val addToCheckout: Button = itemView.findViewById(R.id.addToCheckout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ProductAdapter.MyViewHolder, position: Int, model: Product) {
        holder.productName.text = model.name
        holder.price.text = model.price.toString()
        val theImage = model.img


        if (theImage.indexOf("gs://") > -1) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(theImage)
            Glide.with(holder.productImage.context).load(storageReference).into(holder.productImage)
        } else {
            Glide.with(holder.productImage.context).load(theImage).into(holder.productImage)
        }
        holder.viewDetails.setOnClickListener {
            val i = Intent(it.context, DetailActivity::class.java)
            it.context.startActivity(i)
        }
        holder.addToCheckout.setOnClickListener {
            val i = Intent(it.context, CheckoutActivity::class.java)
            it.context.startActivity(i)
        }
    }
}