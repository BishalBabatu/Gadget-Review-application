package com.example.gadgetrate.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gadgetrate.R
import com.example.gadgetrate.admin.model.Product
import com.example.gadgetrate.databinding.ItemViewAllProductsBinding


class ProductsAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemViewAllProductsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemViewAllProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.tvProductName.text = "Name: ${product.productName}"
        holder.binding.tvProductDescription.text = "Description: ${product.productDescription}"
        holder.binding.tvCategory.text = "Category: ${product.productCategory}"


        Glide.with(holder.binding.ivProductImage.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.placeholder)
            .into(holder.binding.ivProductImage)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}
