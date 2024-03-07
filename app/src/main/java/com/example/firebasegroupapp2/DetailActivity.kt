package com.example.firebasegroupapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val productImage: ImageView = findViewById(R.id.Img)
        val nameTxt: TextView = findViewById(R.id.name)
        val priceTxt: TextView = findViewById(R.id.price)
        val descriptionTxt: TextView = findViewById(R.id.description)

        val theImage = intent.getStringExtra("productImg")
        val name = intent.getStringExtra("name")
        val pid = intent.getStringExtra("pid") ?: "1"
        val price = intent.getDoubleExtra("price", 0.00)
        val description = intent.getStringExtra("description")
        if (!theImage.isNullOrBlank()) {
            if (theImage.indexOf("gs://") > -1) {
                val storageReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(theImage)
                Glide.with(productImage.context).load(storageReference)
                    .into(productImage)
            } else {
                Glide.with(productImage.context).load(theImage).into(productImage)
            }
        }
        nameTxt.text = name
        "${String.format("%.2f", price)} CAD".also { priceTxt.text = it }
        descriptionTxt.text = description

        val btnToCart: Button = findViewById(R.id.addToCart)
        btnToCart.setOnClickListener {
            val i = Intent(it.context, CartActivity::class.java)
            /*Initialized only using the necessary values. !! used as suggested by Android Studio
             to fix warnings*/
            val product = Product(name!!, price, 0, theImage!!, pid, "")
            Common.addToCart(product)
            it.context.startActivity(i)
        }
        val returnToProducts: Button = findViewById(R.id.returnToProducts)
        returnToProducts.setOnClickListener {
            val i = Intent(it.context, ProductActivity::class.java)
            it.context.startActivity(i)
        }


    }
}