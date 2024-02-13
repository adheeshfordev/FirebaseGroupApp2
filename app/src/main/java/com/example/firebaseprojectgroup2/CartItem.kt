package com.example.firebaseprojectgroup2

data class CartItem (val cartId:String, val total:Double=0.00, val qty:Int=0, val pid: String = "", val price: Double=0.00) {
}